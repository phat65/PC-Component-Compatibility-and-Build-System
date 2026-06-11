package com.example.PCOnlineShop.service.cart;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.dto.cart.CartItemDTO;
import com.example.PCOnlineShop.dto.cart.CartSummaryDTO;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.model.cart.Cart;
import com.example.PCOnlineShop.model.cart.CartItem;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.repository.cart.CartItemRepository;
import com.example.PCOnlineShop.repository.cart.CartRepository;
import com.example.PCOnlineShop.repository.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    private Cart getOrCreateCart(Account account) {
        return cartRepository.findByAccount(account)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setAccount(account);
                    return cartRepository.save(newCart);
                });
    }

    public List<CartItemDTO> getCartItems(Account account) {
        Cart cart = getOrCreateCart(account);
        List<CartItem> items = cartItemRepository.findByCart(cart);
        List<CartItemDTO> dtos = new ArrayList<>();
        for (CartItem item : items) {
            Product product = item.getProduct();
            if (product != null) {
                Hibernate.initialize(product.getImages());
                CartItemDTO dto = new CartItemDTO();
                dto.setCartItemId(item.getCartItemId());
                dto.setProductId(product.getProductId());
                dto.setProductName(product.getProductName());
                dto.setPrice(product.getPrice());
                dto.setQuantity(item.getQuantity());
                dto.setInventoryQuantity(product.getInventoryQuantity());
                dto.setSelected(item.isSelected());
                if(product.getImages() != null && !product.getImages().isEmpty()) {
                    dto.setImageUrl(product.getImages().get(0).getImageUrl());
                } else {
                    dto.setImageUrl("/images/no-image.svg");
                }
                dtos.add(dto);
            }
        }
        dtos.sort(Comparator.comparing(CartItemDTO::getProductName));
        return dtos;
    }

    public double calculateSelectedTotal(List<CartItemDTO> cartItems) {
        return cartItems.stream()
                .filter(CartItemDTO::isSelected)
                .mapToDouble(CartItemDTO::getSubtotal)
                .sum();
    }

    public CartSummaryDTO getCartDetails(Account account) {
        List<CartItemDTO> items = getCartItems(account);
        double total = calculateSelectedTotal(items);
        return new CartSummaryDTO(items, total);
    }

    public double calculateSelectedTotalForAccount(Account account) {
        return calculateSelectedTotal(getCartItems(account));
    }

    public int countItems(Account account) {
        return cartRepository.findByAccount(account)
                .map(cart -> cartItemRepository.findByCart(cart)
                        .stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum())
                .orElse(0);
    }

    public void addToCart(Account account, int productId, int quantity) {
        Cart cart = getOrCreateCart(account);
        Product product = getPurchasableProduct(productId);
        addOrMergeCartItem(cart, product, quantity);
    }

    public void addBuildToCart(Account account, BuildItemDto buildItems) {
        if (buildItems == null) {
            throw new IllegalArgumentException("Build items cannot be null");
        }

        List<Product> products = getBuildProducts(buildItems);
        if (products.isEmpty()) {
            throw new IllegalArgumentException("Please select at least one product before adding the build to cart.");
        }

        Cart cart = getOrCreateCart(account);
        products.forEach(product -> {
            Product purchasableProduct = getPurchasableProduct(product.getProductId());
            addOrMergeCartItem(cart, purchasableProduct, 1);
        });
    }

    public void addListToCart(Account account, List<Integer> productIds, int quantity) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product list is required.");
        }

        Cart cart = getOrCreateCart(account);
        Map<Integer, Integer> requestedQuantities = new LinkedHashMap<>();
        productIds.forEach(productId -> requestedQuantities.merge(productId, quantity, Integer::sum));

        requestedQuantities.forEach((productId, requestedQuantity) -> {
            Product product = getPurchasableProduct(productId);
            addOrMergeCartItem(cart, product, requestedQuantity);
        });
    }

    public void updateQuantity(Account account, int cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if(item.getCart().getAccount().getAccountId() != account.getAccountId()) {
            throw new SecurityException("Not authorized");
        }
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            validatePurchasable(item.getProduct(), quantity);
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    public void removeFromCart(Account account, int cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if(item.getCart().getAccount().getAccountId() != account.getAccountId()) {
            throw new SecurityException("Not authorized");
        }
        cartItemRepository.delete(item);
    }

    public void removeItemsFromCart(Account account, List<Integer> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new IllegalArgumentException("Please select at least one item to delete.");
        }

        cartItemIds.forEach(cartItemId -> removeFromCart(account, cartItemId));
    }

    public Map<Integer, CartItem> getCartMapForCheckout(Account account) {
        Cart cart = getOrCreateCart(account);
        return cartItemRepository.findByCartAndIsSelected(cart, true)
                .stream()
                .peek(item -> validatePurchasable(item.getProduct(), item.getQuantity()))
                .collect(Collectors.toMap(
                        item -> item.getProduct().getProductId(),
                        item -> item,
                        this::mergeDuplicateCheckoutItems,
                        LinkedHashMap::new));
    }

    public void toggleSelectItem(Account account, int cartItemId, boolean isSelected) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if(item.getCart().getAccount().getAccountId() != account.getAccountId()) {
            throw new SecurityException("Not authorized");
        }
        item.setSelected(isSelected);
        cartItemRepository.save(item);
    }

    public void clearSelectedItems(Account account) {
        Cart cart = getOrCreateCart(account);
        cartItemRepository.deleteByCartAndIsSelected(cart, true);
    }

    public void clearCart(Account account) {
        Cart cart = getOrCreateCart(account);
        cartItemRepository.deleteByCart(cart);
    }

    private Product getPurchasableProduct(Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product is required.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
        validatePurchasable(product, 1);
        return product;
    }

    private void addOrMergeCartItem(Cart cart, Product product, int quantity) {
        validatePurchasable(product, quantity);

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int mergedQuantity = item.getQuantity() + quantity;
            validatePurchasable(product, mergedQuantity);
            item.setQuantity(mergedQuantity);
            cartItemRepository.save(item);
            return;
        }

        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        newItem.setSelected(true);
        cartItemRepository.save(newItem);
    }

    private void validatePurchasable(Product product, int requestedQuantity) {
        if (product == null) {
            throw new EntityNotFoundException("Product not found");
        }
        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (!product.isSellableOnStorefront()) {
            throw new IllegalArgumentException("Product is unavailable.");
        }

        Integer inventoryQuantity = product.getInventoryQuantity();
        if (inventoryQuantity != null && requestedQuantity > inventoryQuantity) {
            throw new IllegalArgumentException("Requested quantity exceeds available inventory.");
        }
    }

    private CartItem mergeDuplicateCheckoutItems(CartItem existing, CartItem duplicate) {
        int mergedQuantity = existing.getQuantity() + duplicate.getQuantity();
        validatePurchasable(existing.getProduct(), mergedQuantity);
        existing.setQuantity(mergedQuantity);
        return existing;
    }

    private List<Product> getBuildProducts(BuildItemDto buildItems) {
        List<Product> products = new ArrayList<>();
        if (buildItems.getMainboard() != null) products.add(buildItems.getMainboard().getProduct());
        if (buildItems.getCpu() != null) products.add(buildItems.getCpu().getProduct());
        if (buildItems.getGpu() != null) products.add(buildItems.getGpu().getProduct());
        if (buildItems.getMemory() != null) products.add(buildItems.getMemory().getProduct());
        if (buildItems.getStorage() != null) products.add(buildItems.getStorage().getProduct());
        if (buildItems.getPowerSupply() != null) products.add(buildItems.getPowerSupply().getProduct());
        if (buildItems.getPcCase() != null) products.add(buildItems.getPcCase().getProduct());
        if (buildItems.getCooling() != null) products.add(buildItems.getCooling().getProduct());
        if (buildItems.getOther() != null) products.add(buildItems.getOther());
        return products.stream()
                .filter(Objects::nonNull)
                .toList();
    }
}
