package com.shubham.service;

import com.shubham.dto.ItemRequest;
import com.shubham.dto.OrderDTO;
import com.shubham.dto.OrderRequest;
import com.shubham.enums.OrderStatus;
import com.shubham.model.*;
import com.shubham.repository.ItemRepository;
import com.shubham.repository.OrderRepository;
import com.shubham.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder_Success() {
        ItemRequest itemRequest = new ItemRequest(1L, 2);
        Product product = Product.builder().id(1L).stock(5).build();
        Item item = Item.builder().quantity(2).product(product).build();
        OrderRequest orderRequest = new OrderRequest("cust-123", List.of(itemRequest));
        List<Item> items = List.of(item);
        List<Item> savedItems = new ArrayList<>(items);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(itemRepository.saveAll(anyList())).thenReturn(savedItems);
        when(orderRepository.save(any(OrderEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderEvent orderEvent = orderService.createOrder(orderRequest);

        assertNotNull(orderEvent);
        assertEquals(OrderStatus.PENDING, orderEvent.getStatus());
        verify(productRepository).save(product);
    }

    @Test
    void testCreateOrder_InvalidInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(null));
    }

    @Test
    void testCreateItem_Success() {
        Product product = Product.builder().id(1L).stock(10).build();
        ItemRequest request = new ItemRequest(1L, 2);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Item item = orderService.createItem(request);

        assertNotNull(item);
        assertEquals(2, item.getQuantity());
        assertEquals(product, item.getProduct());
    }

    @Test
    void testCreateItem_InvalidQuantity() {
        ItemRequest request = new ItemRequest(1L, 0);
        assertThrows(IllegalArgumentException.class, () -> orderService.createItem(request));
    }

    @Test
    void testGetOrder_Success() {
        String orderId = "order-123";
        List<OrderEvent> orderList = List.of(OrderEvent.builder().orderId(orderId).build());

        when(orderRepository.getByOrderId(orderId)).thenReturn(orderList);

        List<OrderDTO> result = orderService.getOrder(orderId);

        assertEquals(1, result.size());
        verify(orderRepository).getByOrderId(orderId);
    }

    @Test
    void testGetOrder_NotFound_ThrowsException() {
        when(orderRepository.getByOrderId("invalid")).thenReturn(Collections.emptyList());
        assertThrows(IllegalArgumentException.class, () -> orderService.getOrder("invalid"));
    }

    @Test
    void testGetAllOrders_WithStatus() {
        OrderStatus status = OrderStatus.PENDING;
        List<OrderEvent> list = List.of(OrderEvent.builder().status(status).build());

        when(orderRepository.getOrderByStatus(status.name())).thenReturn(list);

        List<OrderDTO> result = orderService.getAllOrders(status);

        assertEquals(1, result.size());
        assertEquals(status, result.get(0).getStatus());
    }

    @Test
    void testUpdatePendingToProcessing() {
        List<OrderEvent> pendingOrders = List.of(OrderEvent.builder()
                .orderId("order-1").customerId("cust-1").status(OrderStatus.PENDING).build());

        when(orderRepository.getOrderByStatus(OrderStatus.PENDING.name())).thenReturn(pendingOrders);

        orderService.updatePendingToProcessing();

        verify(orderRepository, times(1)).save(any(OrderEvent.class));
    }

    @Test
    void testUpdateOrderEventStatus_Success() {
        OrderEvent event = OrderEvent.builder().id(1L).orderId("1L").status(OrderStatus.PENDING).build();

        when(orderRepository.getByOrderId("1L")).thenReturn(List.of(event));
        when(orderRepository.save(any(OrderEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderEvent updated = orderService.updateOrderEventStatus("1L", OrderStatus.PROCESSING);

        assertEquals(OrderStatus.PROCESSING, updated.getStatus());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void testUpdateOrderEventStatus_NotFound_ThrowsException() {
        when(orderRepository.getByOrderId("999L")).thenReturn(new ArrayList<>());
        assertThrows(NoSuchElementException.class, () -> orderService.updateOrderEventStatus("999L", OrderStatus.DELIVERED));
    }
}
