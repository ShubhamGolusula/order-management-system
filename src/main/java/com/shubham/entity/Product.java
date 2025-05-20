package com.shubham.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "products")
@Schema(description = "Represents a product in the system")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The unique identifier for the product", example = "1")
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, unique = true)
    @Schema(description = "The name of the product", example = "Laptop")
    private String name;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    @Schema(description = "A detailed description of the product", example = "High-performance laptop with 16GB RAM and 512GB SSD.")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "The price of the product", example = "1200.50")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Column(nullable = false)
    @Schema(description = "The quantity of the product in stock", example = "100")
    private Integer quantity;
}
