package com.Staj.Order_service.dto;

import com.Staj.Order_service.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private String orderNumber;
    private Order.OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;
    private List<OrderItemResponseDto> items;
} 