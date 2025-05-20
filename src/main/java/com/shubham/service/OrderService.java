package com.shubham.service;

import com.shubham.dto.OrderItemRequest;
import com.shubham.dto.OrderRequest;
import com.shubham.entity.*;
import com.shubham.repository.OrderEventRepository;
import com.shubham.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventRepository orderEventRepository;
    private final ProductService productService;
    private final CustomerService customerService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll().stream().filter(e->!e.getStatus().equalsIgnoreCase("CANCELLED")).toList();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .filter(e->!e.getStatus().equalsIgnoreCase("CANCELLED"))
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
    }


    @Transactional
    public void createOrderEvent(Order order) {
        OrderEvent orderEvent = new OrderEvent(order);
        orderEventRepository.save(orderEvent);
    }

    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        Order newOrder = new Order();
        BigDecimal totalAmount = BigDecimal.ZERO;

        Customer customer = customerService.getCustomerById(orderRequest.getCustomerId());
        newOrder.setCustomer(customer);
        newOrder.setOrderItems(new ArrayList<>());

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            OrderItem orderItem = new OrderItem();
            Product product = productService.getProductById(itemRequest.getProductId());
            if (product.getQuantity() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
            orderItem.setProduct(product);
            orderItem.setPrice(product.getPrice());
            orderItem.setOrder(newOrder);
            orderItem.setQuantity(itemRequest.getQuantity());

            newOrder.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            productService.updateProductQuantity(product.getId(), product.getQuantity() - orderItem.getQuantity());
        }
        newOrder.setTotalAmount(totalAmount);
        newOrder.setCreatedDate(LocalDateTime.now());
        newOrder.setStatus("PENDING");
        return orderRepository.save(newOrder);
    }

    public Order updateOrder(Long id, Order order) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        existingOrder.setCustomer(order.getCustomer());
        existingOrder.setOrderItems(order.getOrderItems());
        existingOrder.setTotalAmount(order.getTotalAmount());
        existingOrder.setStatus(order.getStatus());

        return orderRepository.save(existingOrder);
    }

    public Order deleteOrder(Long id) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        existingOrder.setStatus("CANCELLED");
        return orderRepository.save(existingOrder);
    }

    @Transactional
    public void updatePendingToProcessing() {
        List<Order> pendingOrders = orderRepository.findByStatus("PENDING");
        if (CollectionUtils.isEmpty(pendingOrders)) {
            return;
        }

        for (Order order : pendingOrders) {
            order.setStatus("PROCESSING");
            orderRepository.save(order);
            OrderEvent orderEvent = new OrderEvent(order);
            orderEventRepository.save(orderEvent);
        }
    }
}