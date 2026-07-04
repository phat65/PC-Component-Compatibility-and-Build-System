package com.example.PCOnlineShop.service.order;

import com.example.PCOnlineShop.constant.OrderPaymentStatus;
import com.example.PCOnlineShop.constant.OrderStatus;
import com.example.PCOnlineShop.constant.PaymentStatus;
import com.example.PCOnlineShop.dto.cart.CartItemDTO;
import com.example.PCOnlineShop.dto.order.CheckoutDTO;
import com.example.PCOnlineShop.dto.order.CheckoutPageDTO;
import com.example.PCOnlineShop.dto.warranty.WarrantyDetailDTO;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.model.account.Address;
import com.example.PCOnlineShop.model.cart.CartItem;
import com.example.PCOnlineShop.model.order.Order;
import com.example.PCOnlineShop.model.order.OrderDetail;
import com.example.PCOnlineShop.model.payment.Payment;
import com.example.PCOnlineShop.model.product.Category;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.repository.order.OrderDetailRepository;
import com.example.PCOnlineShop.repository.order.OrderRepository;
import com.example.PCOnlineShop.repository.payment.PaymentRepository;
import com.example.PCOnlineShop.repository.product.ProductRepository;
import com.example.PCOnlineShop.service.address.AddressService;
import com.example.PCOnlineShop.service.cart.CartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final PayOS payOS;
    private final CartService cartService;
    private final AddressService addressService;

    private static final Map<Integer, Integer> WARRANTY_MONTHS_BY_CATEGORY;
    static {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 12); map.put(2, 36); map.put(3, 24); map.put(4, 12); map.put(5, 12);
        map.put(6, 6);  map.put(7, 12); map.put(8, 12); map.put(9, 6);  map.put(10, 6);
        WARRANTY_MONTHS_BY_CATEGORY = Collections.unmodifiableMap(map);
    }

    public OrderService(OrderRepository orderRepository,
                        OrderDetailRepository orderDetailRepository,
                        ProductRepository productRepository,
                        PaymentRepository paymentRepository,
                        PayOS payOS,
                        @Lazy CartService cartService,
                        AddressService addressService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
        this.payOS = payOS;
        this.cartService = cartService;
        this.addressService = addressService;
    }

    private Order createOrder(Account customerAccount,
                              Map<Integer, CartItem> cartItems,
                              CheckoutDTO checkoutDTO,
                              Map<Integer, Product> lockedProducts) {

        Order order = new Order();
        order.setAccount(customerAccount);
        order.setCreatedDate(new Date());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setShippingMethod(checkoutDTO.getShippingMethod());
        order.setNote(checkoutDTO.getNote());
        order.setShippingFullName(checkoutDTO.getShippingFullName());
        order.setShippingPhone(checkoutDTO.getShippingPhone());
        order.setShippingAddress(checkoutDTO.getShippingAddress());

        List<OrderDetail> orderDetails = new ArrayList<>();
        double calculatedFinalAmount = 0.0;

        for (Map.Entry<Integer, CartItem> entry : cartItems.entrySet()) {
            CartItem item = entry.getValue();
            Product product = lockedProducts.get(entry.getKey());
            if (product == null) {
                throw new EntityNotFoundException("Product not found: " + entry.getKey());
            }
            int quantityToBuy = item.getQuantity();


            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(quantityToBuy);
            detail.setPrice(product.getPrice());
            orderDetails.add(detail);
            calculatedFinalAmount += (product.getPrice() * quantityToBuy);
            reserveInventory(product, quantityToBuy);
        }

        order.setFinalAmount(calculatedFinalAmount);
        order.setOrderDetails(orderDetails);
        return orderRepository.save(order);
    }

    public Order getOrderDetailForView(long orderId, Account currentAccount, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (!isAdmin) {
            if (currentAccount == null || order.getAccount() == null ||
                    order.getAccount().getAccountId() != currentAccount.getAccountId()) {
                throw new SecurityException("You are not authorized to view this order.");
            }
        }
        return order;
    }

    public CheckoutPageDTO prepareCheckoutData(Account account) {
        List<CartItemDTO> selectedItems = cartService.getCartItems(account)
                .stream().filter(CartItemDTO::isSelected).toList();

        if (selectedItems.isEmpty()) {
            throw new IllegalStateException("No chosen products to checkout.");
        }

        double total = selectedItems.stream().mapToDouble(CartItemDTO::getSubtotal).sum();

        List<Address> addresses = addressService.getAddressesForAccount(account);
        Address defaultAddr = addressService.getDefaultAddress(account)
                .orElse(addresses.isEmpty() ? null : addresses.get(0));

        CheckoutDTO dto = new CheckoutDTO();
        dto.setShippingMethod("Giao hàng tận nơi");
        if (defaultAddr != null) {
            dto.setShippingFullName(defaultAddr.getFullName());
            dto.setShippingPhone(defaultAddr.getPhone());
            dto.setShippingAddress(defaultAddr.getAddress());
        }

        return new CheckoutPageDTO(dto, selectedItems, total, addresses, defaultAddr);
    }

    @Transactional
    public Order processCheckout(Account account, CheckoutDTO checkoutDTO) {
        Map<Integer, CartItem> checkoutMap = cartService.getCartMapForCheckout(account);
        if (checkoutMap.isEmpty()) {
            throw new IllegalStateException("Please choose products!");
        }
        Map<Integer, Product> lockedProducts = lockProductsForCheckout(checkoutMap);
        return createOrder(account, checkoutMap, checkoutDTO, lockedProducts);
    }

    private Map<Integer, Product> lockProductsForCheckout(Map<Integer, CartItem> checkoutMap) {
        Map<Integer, Product> lockedProducts = new HashMap<>();
        checkoutMap.keySet().stream()
                .sorted()
                .forEach(productId -> {
                    Product product = productRepository.findByProductIdForUpdate(productId)
                            .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
                    lockedProducts.put(productId, product);
                });
        return lockedProducts;
    }

    private void rollBackInventory(Order order) {
        List<OrderDetail> details = orderDetailRepository.findByOrder(order);
        for (OrderDetail detail : details) {
            Product product = detail.getProduct();
            if (product != null && product.getInventoryQuantity() != null) {
                product.setInventoryQuantity(product.getInventoryQuantity() + detail.getQuantity());
                productRepository.save(product);
            }
        }
    }

    private void reserveInventory(Product product, int quantity) {
        Integer inventoryQuantity = product.getInventoryQuantity();
        if (!product.isSellableOnStorefront()) {
            throw new IllegalStateException("Product is unavailable: " + product.getProductName());
        }
        if (inventoryQuantity == null) {
            return;
        }
        if (quantity > inventoryQuantity) {
            throw new IllegalStateException("Not enough inventory for product: " + product.getProductName());
        }
        product.setInventoryQuantity(inventoryQuantity - quantity);
        productRepository.save(product);
    }

    @Transactional
    public void cancelOrderFromPaymentId(long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("No payment found: " + paymentId));
        Order order = payment.getOrder();

        if (OrderStatus.PENDING_PAYMENT.equals(order.getStatus()) && PaymentStatus.PENDING.equals(payment.getStatus())) {
            payment.setStatus(PaymentStatus.CANCELLED);
            order.setStatus(OrderStatus.CANCELLED);
            order.setPaymentStatus(OrderPaymentStatus.CANCELLED);

            rollBackInventory(order);

            try {
                if (payment.getOrderCode() != null) {
                    payOS.paymentRequests().cancel(payment.getOrderCode(), "Cancelled by customer or out of payment time");
                }
            } catch (RuntimeException e) {
                log.warn("Failed to cancel PayOS payment link for payment {}", paymentId, e);
            }

            paymentRepository.save(payment);
            orderRepository.save(order);
        }
    }

    public List<Order> getOrdersByAccount(Account account) {
        return orderRepository.findByAccount(account);
    }

    public List<OrderDetail> getOrderDetails(long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        return orderDetailRepository.findByOrder(order);
    }

    public List<Order> findAllOrdersForAdmin() {
        return orderRepository.findAll();
    }


    public List<Order> getOrdersByPhoneNumberForWarranty(String phoneNumber) {
        return orderRepository.findWarrantyEligibleOrdersByPhoneNumber(
                phoneNumber,
                OrderPaymentStatus.PAID,
                OrderStatus.COMPLETED
        );
    }

    @Transactional(readOnly = true)
    public List<WarrantyDetailDTO> getWarrantyDetailsByOrderId(long orderId) {
        List<OrderDetail> details = orderDetailRepository.findByOrder_OrderIdWithAssociations(orderId);
        LocalDate today = LocalDate.now();

        return details.stream()
                .map(detail -> {
                    Order order = detail.getOrder();
                    Product product = detail.getProduct();
                    if (order == null || !isWarrantyEligibleOrder(order) || product == null) {
                        return null;
                    }
                    Category category = (product.getCategories() != null && !product.getCategories().isEmpty())
                            ? product.getCategories().get(0) : null;

                    Date createdDate = order.getCreatedDate();
                    if (category == null || createdDate == null) {
                        return null;
                    }
                    int categoryId = category.getCategoryId();
                    int warrantyMonths = WARRANTY_MONTHS_BY_CATEGORY.getOrDefault(categoryId, 0);

                    LocalDate orderLocalDate = new java.util.Date(createdDate.getTime())
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    LocalDate expiryDate = orderLocalDate.plusMonths(warrantyMonths);

                    String warrantyStatus;
                    long daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate);

                    if (daysUntilExpiry < 0) warrantyStatus = "Expired";
                    else if (daysUntilExpiry <= 7) warrantyStatus = "Expiring Soon";
                    else warrantyStatus = "Active";

                    return new WarrantyDetailDTO(
                            order.getOrderId(),
                            product.getProductName(),
                            createdDate,
                            warrantyMonths,
                            expiryDate,
                            warrantyStatus
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean isWarrantyEligibleOrder(Order order) {
        return OrderPaymentStatus.PAID.equals(order.getPaymentStatus())
                || OrderStatus.COMPLETED.equals(order.getStatus());
    }

    @Transactional(readOnly = true)
    public List<Order> getShippingQueueOrders() {
        return orderRepository.findShippingManagementOrders(
                OrderStatus.SHIPPING_MANAGEMENT_STATUSES,
                OrderStatus.CANCELLED
        );
    }


    @Transactional
    public String processShippingStatusUpdate(long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElse(null);

        if (order == null) {
            return "Order not found.";
        }

        if (order.getStatus().equals(newStatus)) {
            return "No change detected for Order #" + orderId;
        }

        updateShippingStatus(orderId, newStatus);
        return "Success: Order #" + orderId + " updated to " + newStatus;
    }

    @Transactional
    public void updateShippingStatus(long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        String currentStatus = order.getStatus();
        boolean isValidTransition = false;
        Date now = new Date();

        if (OrderStatus.READY_TO_SHIP.equals(currentStatus)
                && List.of(OrderStatus.DELIVERING, OrderStatus.CANCELLED).contains(newStatus)) {
            isValidTransition = true;
            if (OrderStatus.DELIVERING.equals(newStatus)) {
                order.setShipmentReceivedDate(now);
            }
            if (OrderStatus.CANCELLED.equals(newStatus) && order.getReadyToShipDate() == null) {
                order.setReadyToShipDate(now);
            }
        } else if (OrderStatus.DELIVERING.equals(currentStatus)
                && List.of(OrderStatus.COMPLETED, OrderStatus.CANCELLED, OrderStatus.DELIVERY_FAILED).contains(newStatus)) {
            isValidTransition = true;
        } else if (OrderStatus.DELIVERY_FAILED.equals(currentStatus)
                && List.of(OrderStatus.READY_TO_SHIP, OrderStatus.DELIVERING, OrderStatus.CANCELLED).contains(newStatus)) {
            isValidTransition = true;
            if (OrderStatus.DELIVERING.equals(newStatus)) {
                order.setShipmentReceivedDate(now);
            }
        } else if (OrderStatus.COMPLETED.equals(currentStatus)) {
            throw new IllegalArgumentException("Completed orders are final and cannot be updated from Shipping Management.");
        }

        if (!isValidTransition) {
            throw new IllegalArgumentException("Invalid transition from " + currentStatus + " to " + newStatus);
        }
        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    @Transactional
    public void completePickupOrder(long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        if (!OrderStatus.READY_FOR_PICKUP.equals(order.getStatus())) {
            throw new IllegalArgumentException("Only ready-for-pickup orders can be completed from this action.");
        }

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
    }
}
