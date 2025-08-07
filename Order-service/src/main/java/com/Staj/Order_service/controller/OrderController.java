package com.Staj.Order_service.controller;

import com.Staj.Order_service.dto.OrderRequestDto;
import com.Staj.Order_service.dto.OrderResponseDto;
import com.Staj.Order_service.model.Order;
import com.Staj.Order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    
    // Yeni sipariş oluştur
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequest) {
        try {
            log.info("Sipariş oluşturma isteği alındı: {}", orderRequest);
            OrderResponseDto createdOrder = orderService.createOrder(orderRequest);
            log.info("Sipariş başarıyla oluşturuldu: {}", createdOrder.getOrderNumber());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (RuntimeException e) {
            log.error("Sipariş oluşturma hatası: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Beklenmeyen hata: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ID'ye göre sipariş getir
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        Optional<OrderResponseDto> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Sipariş numarasına göre sipariş getir
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponseDto> getOrderByOrderNumber(@PathVariable String orderNumber) {
        Optional<OrderResponseDto> order = orderService.getOrderByOrderNumber(orderNumber);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Müşteri ID'sine göre siparişleri getir
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByCustomerId(@PathVariable Long customerId) {
        List<OrderResponseDto> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }
    
    // Tüm siparişleri getir
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        List<OrderResponseDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    // Duruma göre siparişleri getir
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        List<OrderResponseDto> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    
    // Sipariş durumunu güncelle
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long id, 
            @RequestParam Order.OrderStatus status) {
        Optional<OrderResponseDto> updatedOrder = orderService.updateOrderStatus(id, status);
        return updatedOrder.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 