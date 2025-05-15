package com.shubham.dto;

import lombok.*;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
public class OrderRequest {
    private String customerId;
    private List<ItemRequest> items;
}

