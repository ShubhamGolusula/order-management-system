package com.shubham.entity;

import com.shubham.dto.OrderRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "orders")
@Schema(description = "Represents an order placed by a customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The unique identifier for the order", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer is required")
    @Schema(description = "The customer who placed the order")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "The items included in the order")
    private List<OrderItem> orderItems;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "The total amount of the order", example = "1200.50")
    private BigDecimal totalAmount;

    @Column(nullable = false)
    @Schema(description = "The status of the order", example = "PENDING")
    private String status; // e.g., PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED

    public Order(OrderRequest orderRequest){

    }
}