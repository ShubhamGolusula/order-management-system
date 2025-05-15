package com.shubham.dto;

import com.shubham.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private String orderId;
    private String customerId;
    private OrderStatus status;
    private List<ItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

