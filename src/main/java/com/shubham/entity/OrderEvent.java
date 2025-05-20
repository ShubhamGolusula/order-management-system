package com.shubham.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "order_events")
@Schema(description = "Represents an order event placed by a customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The unique identifier for the order event", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer is required")
    @Schema(description = "The customer who placed the order")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Order is required")
    @Schema(description = "The customer who placed the order")
    private Order order;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "The total amount of the order", example = "1200.50")
    private BigDecimal totalAmount;

    @Column(nullable = false)
    @Schema(description = "The status of the order", example = "PENDING")
    private String status; // e.g., PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED

    public OrderEvent(Order order) {
        this.customer = order.getCustomer();
        this.status = order.getStatus();
        this.totalAmount = order.getTotalAmount();
        this.order = order;
    }

}
