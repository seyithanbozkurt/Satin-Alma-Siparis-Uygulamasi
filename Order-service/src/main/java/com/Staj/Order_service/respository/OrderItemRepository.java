package com.Staj.Order_service.respository;

import com.Staj.Order_service.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // Sipariş ID'sine göre sipariş kalemlerini getir
    List<OrderItem> findByOrderId(Long orderId);
} 