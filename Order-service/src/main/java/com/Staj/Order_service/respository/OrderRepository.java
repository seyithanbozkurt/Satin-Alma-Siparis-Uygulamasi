package com.Staj.Order_service.respository;

import com.Staj.Order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Müşteri ID'sine göre siparişleri getir
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);
    
    // Sipariş numarasına göre sipariş bul
    Optional<Order> findByOrderNumber(String orderNumber);
    
    // Duruma göre siparişleri getir
    List<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status);
    
    // Müşteri ID'si ve duruma göre siparişleri getir
    List<Order> findByCustomerIdAndStatusOrderByOrderDateDesc(Long customerId, Order.OrderStatus status);
    
    // Tüm siparişleri tarihe göre sıralayarak getir
    List<Order> findAllByOrderByOrderDateDesc();
} 