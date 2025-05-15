package com.shubham.controller;

import com.shubham.dto.OrderDTO;
import com.shubham.dto.OrderRequest;
import com.shubham.enums.OrderStatus;
import com.shubham.model.OrderEvent;
import com.shubham.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Validated
@RequiredArgsConstructor
@Tag(name = "Order API", description = "APIs for creating, retrieving, updating, and deleting orders")
class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(
            summary = "Create a new order",
            requestBody = @RequestBody(
                    description = "Order details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order created",
                            content = @Content(schema = @Schema(implementation = OrderEvent.class)))
            }
    )
    public OrderEvent createOrder(@org.springframework.web.bind.annotation.RequestBody OrderRequest newOrder) {
        return orderService.createOrder(newOrder);
    }

    @GetMapping("/{orderId}")
    @Operation(
            summary = "Get an order by ID",
            parameters = @Parameter(name = "orderId", description = "Unique ID of the order"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order found",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderEvent.class))))
            }
    )
    public ResponseEntity<List<OrderDTO>> getOrder(@PathVariable String orderId) {
        return new ResponseEntity<>(orderService.getOrder(orderId), HttpStatus.OK);
    }

    @GetMapping
    @Operation(
            summary = "Get all orders, optionally filtered by status",
            parameters = @Parameter(name = "status", description = "Filter by order status"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders retrieved",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderEvent.class))))
            }
    )
    public List<OrderDTO> getAllOrders(@RequestParam(required = false) OrderStatus status) {
        return orderService.getAllOrders(status != null ? status : OrderStatus.PENDING);
    }

    @DeleteMapping("/{orderId}")
    @Operation(
            summary = "Cancel an order by ID",
            parameters = @Parameter(name = "orderId", description = "ID of the order to cancel"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order cancelled"),
                    @ApiResponse(responseCode = "400", description = "Invalid order ID")
            }
    )
    public ResponseEntity<OrderEvent> cancelOrder(@PathVariable String orderId) {
        OrderEvent updatedOrderEvent = orderService.updateOrderEventStatus(orderId, OrderStatus.CANCELLED);
        return ResponseEntity.ok(updatedOrderEvent);
    }

    @PutMapping("/{orderId}/status")
    @Operation(
            summary = "Update the status of an order",
            parameters = {
                    @Parameter(name = "orderId", description = "ID of the order to update"),
                    @Parameter(name = "newStatus", description = "New status for the order")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order status updated",
                            content = @Content(schema = @Schema(implementation = OrderEvent.class)))
            }
    )
    public ResponseEntity<OrderEvent> updateOrderEventStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus newStatus) {
        OrderEvent updatedOrderEvent = orderService.updateOrderEventStatus(orderId, newStatus);
        return ResponseEntity.ok(updatedOrderEvent);
    }
}
