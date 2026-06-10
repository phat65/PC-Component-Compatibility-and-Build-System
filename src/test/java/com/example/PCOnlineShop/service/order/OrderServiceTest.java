package com.example.PCOnlineShop.service.order;

import com.example.PCOnlineShop.constant.OrderStatus;
import com.example.PCOnlineShop.dto.order.CheckoutDTO;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.model.cart.CartItem;
import com.example.PCOnlineShop.model.order.Order;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.model.product.ProductLifecycleStatus;
import com.example.PCOnlineShop.repository.order.OrderDetailRepository;
import com.example.PCOnlineShop.repository.order.OrderRepository;
import com.example.PCOnlineShop.repository.payment.PaymentRepository;
import com.example.PCOnlineShop.repository.product.ProductRepository;
import com.example.PCOnlineShop.service.address.AddressService;
import com.example.PCOnlineShop.service.cart.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.payos.PayOS;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PayOS payOS;
    @Mock
    private CartService cartService;
    @Mock
    private AddressService addressService;

    private OrderService orderService;
    private Account account;
    private CheckoutDTO checkoutDTO;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                orderRepository,
                orderDetailRepository,
                productRepository,
                paymentRepository,
                payOS,
                cartService,
                addressService
        );

        account = new Account();
        account.setAccountId(10);

        checkoutDTO = new CheckoutDTO();
        checkoutDTO.setShippingMethod("Giao hàng tận nơi");
        checkoutDTO.setShippingFullName("Nguyen Van A");
        checkoutDTO.setShippingPhone("0900000000");
        checkoutDTO.setShippingAddress("HCMC");
    }

    @Test
    void processCheckoutLocksProductsInStableOrderAndReservesLockedInventory() {
        Product staleProductTwo = product(2, "GPU", 10);
        Product staleProductOne = product(1, "CPU", 10);
        Product lockedProductOne = product(1, "CPU", 5);
        Product lockedProductTwo = product(2, "GPU", 5);

        Map<Integer, CartItem> checkoutMap = new LinkedHashMap<>();
        checkoutMap.put(2, cartItem(staleProductTwo, 2));
        checkoutMap.put(1, cartItem(staleProductOne, 1));

        when(cartService.getCartMapForCheckout(account)).thenReturn(checkoutMap);
        when(productRepository.findByProductIdForUpdate(1)).thenReturn(Optional.of(lockedProductOne));
        when(productRepository.findByProductIdForUpdate(2)).thenReturn(Optional.of(lockedProductTwo));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.processCheckout(account, checkoutDTO);

        InOrder lockOrder = inOrder(productRepository);
        lockOrder.verify(productRepository).findByProductIdForUpdate(1);
        lockOrder.verify(productRepository).findByProductIdForUpdate(2);

        assertThat(lockedProductOne.getInventoryQuantity()).isEqualTo(4);
        assertThat(lockedProductTwo.getInventoryQuantity()).isEqualTo(3);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(order.getOrderDetails())
                .extracting(detail -> detail.getProduct().getProductId())
                .containsExactly(2, 1);
        verify(productRepository).save(lockedProductOne);
        verify(productRepository).save(lockedProductTwo);
    }

    @Test
    void processCheckoutRejectsWhenLockedInventoryIsNoLongerEnough() {
        Product staleProduct = product(1, "CPU", 10);
        Product lockedProduct = product(1, "CPU", 1);

        Map<Integer, CartItem> checkoutMap = new LinkedHashMap<>();
        checkoutMap.put(1, cartItem(staleProduct, 2));

        when(cartService.getCartMapForCheckout(account)).thenReturn(checkoutMap);
        when(productRepository.findByProductIdForUpdate(1)).thenReturn(Optional.of(lockedProduct));

        assertThatThrownBy(() -> orderService.processCheckout(account, checkoutDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Not enough inventory for product: CPU");

        verify(orderRepository, never()).save(any());
    }

    private Product product(int id, String name, Integer inventoryQuantity) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(name);
        product.setPrice(100);
        product.setStatus(true);
        product.setLifecycleStatus(ProductLifecycleStatus.SELLING);
        product.setInventoryQuantity(inventoryQuantity);
        return product;
    }

    private CartItem cartItem(Product product, int quantity) {
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setSelected(true);
        return item;
    }
}
