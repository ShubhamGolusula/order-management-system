package com.shubham.service;

import com.shubham.dto.*;
import com.shubham.enums.OrderStatus;
import com.shubham.model.*;
import com.shubham.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import com.shubham.dto.OrderRequest;
import com.shubham.model.OrderEvent;
import com.shubham.model.Item;
import com.shubham.repository.ItemRepository;
import com.shubham.repository.OrderRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;


    @Transactional
    public OrderEvent createOrder(OrderRequest orderRequest) {
        if (orderRequest == null || CollectionUtils.isEmpty(orderRequest.getItems())) {
            throw new IllegalArgumentException("Order request cannot be null or empty.");
        }

        String orderId = UUID.randomUUID().toString();
        List<Item> items = orderRequest.getItems().stream()
                .map(this::createItem)
                .peek(e->e.setOrderId(orderId))
                .collect(Collectors.toList());

        List<Item> savedItems = itemRepository.saveAll(items);

        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(orderId)
                .items(savedItems)
                .customerId(orderRequest.getCustomerId())
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        OrderEvent orderEvent1 = orderRepository.save(orderEvent);

        savedItems.forEach(item -> {
            Product product = item.getProduct();
            int newStock = product.getStock() - item.getQuantity();
            if (newStock < 0) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getId());
            }
            product.setStock(newStock);
            productRepository.save(product);
        });

        return orderEvent1;
    }

    public Item createItem(ItemRequest itemRequest) {
        if (itemRequest == null) {
            throw new IllegalArgumentException("ItemRequest cannot be null");
        }
        if (itemRequest.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found for id: " + itemRequest.getProductId()));

        if (itemRequest.getQuantity() > product.getStock()) {
            throw new IllegalArgumentException("Quantity exceeds the stock");
        }

        return Item.builder()
                .quantity(itemRequest.getQuantity())
                .product(product)
                .build();
    }

    public List<OrderDTO> getOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        List<OrderEvent> orders = orderRepository.getByOrderId(orderId);
        if (CollectionUtils.isEmpty(orders))
        {
            throw new IllegalArgumentException("Order with given Order ID not found");
        }
        return orders.stream().map(this::toDto).toList();
    }

    public OrderDTO toDto(OrderEvent order) {
        OrderDTO orderDTO = OrderDTO.builder()
                .id(order.getId())
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
        if (order.getItems() != null && !order.getItems().isEmpty()){
            orderDTO.setItems(order.getItems().stream()
                    .map(item -> ItemDTO.builder()
                            .id(item.getId())
                            .quantity(item.getQuantity())
                            .product(item.getProduct().getName())
                            .build())
                    .collect(Collectors.toList()));
        }
        return orderDTO;
    }


    public List<OrderDTO> getAllOrders(OrderStatus status) {
        return (status != null) ? orderRepository.getOrderByStatus(status.name()).stream().map(this::toDto).toList() : orderRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public OrderEvent cancelOrder(String orderId) {
        return updateOrderEventStatus(orderId, OrderStatus.CANCELLED);

    }

    @Transactional
    public void updatePendingToProcessing() {
        List<OrderEvent> pendingOrderEvents = orderRepository.getOrderByStatus(OrderStatus.PENDING.name());
        if (CollectionUtils.isEmpty(pendingOrderEvents)) {
            return;
        }

        for (OrderEvent orderEvent : pendingOrderEvents) {
            OrderEvent newEvent = OrderEvent.builder()
                    .orderId(orderEvent.getOrderId())
                    .customerId(orderEvent.getCustomerId())
                    .status(OrderStatus.PROCESSING)
                    .updatedAt(LocalDateTime.now()).build();
            orderRepository.save(newEvent);
        }
    }

    @Transactional
    public OrderEvent updateOrderEventStatus(String  orderId, OrderStatus newStatus) {
        List<OrderEvent> orderEvents = orderRepository.getByOrderId(orderId);
        if (orderEvents.isEmpty()) {
            throw new NoSuchElementException("Order not found with id: " + orderId);
        }
        boolean isEventFound = orderEvents.stream().anyMatch(e->e.getStatus().equals(newStatus));

        if (isEventFound){
            throw new NoSuchElementException("Order already has been" + newStatus);
        }
        String customerId = orderEvents.getFirst().getCustomerId();

        OrderEvent newEvent = OrderEvent.builder()
                .orderId(orderId)
                .customerId(customerId)
                .status(newStatus)
                .updatedAt(LocalDateTime.now()).build();

        return orderRepository.save(newEvent);

    }
}
