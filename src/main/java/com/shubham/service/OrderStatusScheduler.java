package com.shubham.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class OrderStatusScheduler {
    private final OrderService orderService;

    public OrderStatusScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void updatePendingOrders() {
        orderService.updatePendingToProcessing();
    }
}

