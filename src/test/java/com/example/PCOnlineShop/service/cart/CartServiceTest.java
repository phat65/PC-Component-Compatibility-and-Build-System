package com.example.PCOnlineShop.service.cart;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.model.cart.Cart;
import com.example.PCOnlineShop.model.cart.CartItem;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.model.product.ProductLifecycleStatus;
import com.example.PCOnlineShop.repository.cart.CartItemRepository;
import com.example.PCOnlineShop.repository.cart.CartRepository;
import com.example.PCOnlineShop.repository.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductRepository productRepository;

    private CartService cartService;
    private Account account;
    private Cart cart;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartRepository, cartItemRepository, productRepository);
        account = new Account();
        account.setAccountId(10);

        cart = new Cart();
        cart.setCartId(20);
        cart.setAccount(account);
    }

    @Test
    void addToCartRejectsProductThatIsNotSellingOnStorefront() {
        Product product = product(1, "Discontinued", 10, ProductLifecycleStatus.DISCONTINUED);

        when(cartRepository.findByAccount(account)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> cartService.addToCart(account, 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product is unavailable.");

        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void addToCartRejectsQuantityBeyondInventory() {
        Product product = product(1, "GPU", 2, ProductLifecycleStatus.SELLING);

        when(cartRepository.findByAccount(account)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> cartService.addToCart(account, 1, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Requested quantity exceeds available inventory.");

        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void addToCartMergesWithExistingCartItemAndRechecksInventory() {
        Product product = product(1, "CPU", 5, ProductLifecycleStatus.SELLING);
        CartItem existingItem = cartItem(product, 2);

        when(cartRepository.findByAccount(account)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(existingItem));

        cartService.addToCart(account, 1, 3);

        assertThat(existingItem.getQuantity()).isEqualTo(5);
        verify(cartItemRepository).save(existingItem);
    }

    @Test
    void addListToCartUsesSameMergePathAsSingleAdd() {
        Product product = product(1, "Memory", 10, ProductLifecycleStatus.SELLING);
        CartItem existingItem = cartItem(product, 1);

        when(cartRepository.findByAccount(account)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(existingItem));

        cartService.addListToCart(account, List.of(1, 1), 2);

        assertThat(existingItem.getQuantity()).isEqualTo(5);
        verify(cartItemRepository).save(existingItem);
    }

    @Test
    void checkoutMapMergesDuplicateSelectedItemsForLegacyCartData() {
        Product product = product(1, "Storage", 10, ProductLifecycleStatus.SELLING);
        CartItem first = cartItem(product, 2);
        CartItem duplicate = cartItem(product, 3);

        when(cartRepository.findByAccount(account)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartAndIsSelected(cart, true)).thenReturn(List.of(first, duplicate));

        Map<Integer, CartItem> checkoutMap = cartService.getCartMapForCheckout(account);

        assertThat(checkoutMap).containsOnlyKeys(1);
        assertThat(checkoutMap.get(1).getQuantity()).isEqualTo(5);
    }

    @Test
    void addToCartCreatesSelectedItemForNewProduct() {
        Product product = product(1, "Case", 4, ProductLifecycleStatus.SELLING);

        when(cartRepository.findByAccount(account)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        cartService.addToCart(account, 1, 2);

        ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).save(captor.capture());

        CartItem saved = captor.getValue();
        assertThat(saved.getCart()).isSameAs(cart);
        assertThat(saved.getProduct()).isSameAs(product);
        assertThat(saved.getQuantity()).isEqualTo(2);
        assertThat(saved.isSelected()).isTrue();
    }

    private Product product(int id, String name, Integer inventory, ProductLifecycleStatus lifecycleStatus) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(name);
        product.setStatus(true);
        product.setLifecycleStatus(lifecycleStatus);
        product.setInventoryQuantity(inventory);
        product.setPrice(100);
        return product;
    }

    private CartItem cartItem(Product product, int quantity) {
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setSelected(true);
        return item;
    }
}
