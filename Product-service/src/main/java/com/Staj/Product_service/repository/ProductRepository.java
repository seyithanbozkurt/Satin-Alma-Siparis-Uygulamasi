package com.Staj.Product_service.repository;

import com.Staj.Product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Aktif ürünleri getir
    List<Product> findByIsActiveTrue();
    
    // İsme göre ürün ara
    List<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
    
    // Fiyat aralığına göre ürün ara
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
    List<Product> findByPriceBetween(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    // Stokta olan ürünleri getir
    List<Product> findByStockQuantityGreaterThanAndIsActiveTrue(Integer quantity);
    
    // İsme göre tek ürün bul
    Optional<Product> findByNameAndIsActiveTrue(String name);
} 