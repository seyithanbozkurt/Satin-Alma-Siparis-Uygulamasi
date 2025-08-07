package com.Staj.Customer_service.respository;

import com.Staj.Customer_service.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    // Aktif müşterileri getir
    List<Customer> findByIsActiveTrue();
    
    // Sipariş yetkisi olan müşterileri getir
    List<Customer> findByHasOrderPermissionTrueAndIsActiveTrue();
    
    // Email'e göre müşteri bul
    Optional<Customer> findByEmailAndIsActiveTrue(String email);
    
    // İsme göre müşteri ara
    List<Customer> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
    
    // Email'e göre müşteri var mı kontrol et
    boolean existsByEmailAndIsActiveTrue(String email);
} 