package com.Staj.Order_service.service;

import com.Staj.Order_service.client.CustomerClient;
import com.Staj.Order_service.client.ProductClient;
import com.Staj.Order_service.dto.*;
import com.Staj.Order_service.model.Order;
import com.Staj.Order_service.model.OrderItem;
import com.Staj.Order_service.respository.OrderItemRepository;
import com.Staj.Order_service.respository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    
    // Sipariş oluştur
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequest) {
        log.info("Sipariş oluşturma başladı. Müşteri ID: {}", orderRequest.getCustomerId());
        
        // Items null kontrolü
        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            log.error("Sipariş kalemleri boş olamaz. Items: {}", orderRequest.getItems());
            throw new RuntimeException("Sipariş kalemleri boş olamaz. En az bir ürün seçmelisiniz.");
        }
        
        // Müşterinin sipariş yetkisi var mı kontrol et
        log.info("Müşteri yetkisi kontrol ediliyor...");
        Boolean hasPermission = customerClient.hasOrderPermission(orderRequest.getCustomerId());
        log.info("Müşteri yetkisi sonucu: {}", hasPermission);
        
        if (hasPermission == null || !hasPermission) {
            log.error("Müşterinin sipariş yetkisi bulunmamaktadır. Müşteri ID: {}", orderRequest.getCustomerId());
            throw new RuntimeException("Müşterinin sipariş yetkisi bulunmamaktadır. Müşteri ID: " + orderRequest.getCustomerId());
        }
        
        // Müşteri bilgilerini al
        log.info("Müşteri bilgileri alınıyor...");
        CustomerDTO customer = customerClient.getCustomerById(orderRequest.getCustomerId());
        if (customer == null) {
            log.error("Müşteri bulunamadı. Müşteri ID: {}", orderRequest.getCustomerId());
            throw new RuntimeException("Müşteri bulunamadı. Müşteri ID: " + orderRequest.getCustomerId());
        }
        log.info("Müşteri bulundu: {}", customer.getName());
        
        // Sipariş numarası oluştur
        String orderNumber = generateOrderNumber();
        log.info("Sipariş numarası oluşturuldu: {}", orderNumber);
        
        // Sipariş oluştur
        Order order = new Order();
        order.setCustomerId(orderRequest.getCustomerId());
        order.setOrderNumber(orderNumber);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setOrderDate(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);
        log.info("Sipariş kaydedildi. Sipariş ID: {}", savedOrder.getId());
        
        // Sipariş kalemlerini oluştur
        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(itemRequest -> {
                    log.info("Ürün bilgileri alınıyor. Ürün ID: {}", itemRequest.getProductId());
                    
                    // Ürün bilgilerini al
                    ProductDto product = productClient.getProductById(itemRequest.getProductId());
                    if (product == null) {
                        log.error("Ürün bulunamadı. Ürün ID: {}", itemRequest.getProductId());
                        throw new RuntimeException("Ürün bulunamadı: " + itemRequest.getProductId());
                    }
                    log.info("Ürün bulundu: {} (Stok: {})", product.getName(), product.getStock());
                    
                    // Stok kontrolü
                    if (product.getStock() < itemRequest.getQuantity()) {
                        log.error("Yetersiz stok. Ürün: {}, İstenen: {}, Mevcut: {}", 
                                product.getName(), itemRequest.getQuantity(), product.getStock());
                        throw new RuntimeException("Yetersiz stok: " + product.getName() + 
                                " (İstenen: " + itemRequest.getQuantity() + ", Mevcut: " + product.getStock() + ")");
                    }
                    
                    // Sipariş kalemi oluştur
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(savedOrder);
                    orderItem.setProductId(product.getId());
                    orderItem.setProductName(product.getName());
                    orderItem.setQuantity(itemRequest.getQuantity());
                    orderItem.setUnitPrice(BigDecimal.valueOf(product.getPrice()));
                    orderItem.calculateTotalPrice();
                    
                    OrderItem savedItem = orderItemRepository.save(orderItem);
                    log.info("Sipariş kalemi oluşturuldu: {} x {} = {}", 
                            itemRequest.getQuantity(), product.getName(), savedItem.getTotalPrice());
                    
                    return savedItem;
                })
                .collect(Collectors.toList());
        
        // Toplam tutarı hesapla
        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Toplam tutarı güncelle
        savedOrder.setTotalAmount(totalAmount);
        savedOrder.setOrderItems(orderItems);
        orderRepository.save(savedOrder);
        
        log.info("Sipariş tamamlandı. Toplam tutar: {}", totalAmount);
        
        return convertToOrderResponseDto(savedOrder, customer.getName());
    }
    
    // Sipariş güncelle
    @Transactional
    public Optional<OrderResponseDto> updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(newStatus);
                    order.setUpdatedAt(LocalDateTime.now());
                    Order savedOrder = orderRepository.save(order);
                    
                    CustomerDTO customer = customerClient.getCustomerById(savedOrder.getCustomerId());
                    String customerName = customer != null ? customer.getName() : "Bilinmeyen Müşteri";
                    
                    return convertToOrderResponseDto(savedOrder, customerName);
                });
    }
    
    // ID'ye göre sipariş getir
    public Optional<OrderResponseDto> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(order -> {
                    CustomerDTO customer = customerClient.getCustomerById(order.getCustomerId());
                    String customerName = customer != null ? customer.getName() : "Bilinmeyen Müşteri";
                    return convertToOrderResponseDto(order, customerName);
                });
    }
    
    // Müşteri ID'sine göre siparişleri getir
    public List<OrderResponseDto> getOrdersByCustomerId(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
        CustomerDTO customer = customerClient.getCustomerById(customerId);
        String customerName = customer != null ? customer.getName() : "Bilinmeyen Müşteri";
        
        return orders.stream()
                .map(order -> convertToOrderResponseDto(order, customerName))
                .collect(Collectors.toList());
    }
    
    // Tüm siparişleri getir
    public List<OrderResponseDto> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
        
        return orders.stream()
                .map(order -> {
                    CustomerDTO customer = customerClient.getCustomerById(order.getCustomerId());
                    String customerName = customer != null ? customer.getName() : "Bilinmeyen Müşteri";
                    return convertToOrderResponseDto(order, customerName);
                })
                .collect(Collectors.toList());
    }
    
    // Duruma göre siparişleri getir
    public List<OrderResponseDto> getOrdersByStatus(Order.OrderStatus status) {
        List<Order> orders = orderRepository.findByStatusOrderByOrderDateDesc(status);
        
        return orders.stream()
                .map(order -> {
                    CustomerDTO customer = customerClient.getCustomerById(order.getCustomerId());
                    String customerName = customer != null ? customer.getName() : "Bilinmeyen Müşteri";
                    return convertToOrderResponseDto(order, customerName);
                })
                .collect(Collectors.toList());
    }
    
    // Sipariş numarasına göre sipariş getir
    public Optional<OrderResponseDto> getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(order -> {
                    CustomerDTO customer = customerClient.getCustomerById(order.getCustomerId());
                    String customerName = customer != null ? customer.getName() : "Bilinmeyen Müşteri";
                    return convertToOrderResponseDto(order, customerName);
                });
    }
    
    // Sipariş numarası oluştur
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD-" + timestamp;
    }
    
    // Order'ı OrderResponseDto'ya çevir
    private OrderResponseDto convertToOrderResponseDto(Order order, String customerName) {
        List<OrderItemResponseDto> itemDtos = order.getOrderItems().stream()
                .map(item -> new OrderItemResponseDto(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .collect(Collectors.toList());
        
        return new OrderResponseDto(
                order.getId(),
                order.getCustomerId(),
                customerName,
                order.getOrderNumber(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getOrderDate(),
                order.getUpdatedAt(),
                itemDtos
        );
    }
}
