package com.shubham.dto;

import lombok.*;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private Long productId;
    private String orderId;
    private Integer quantity;

    public ItemRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
