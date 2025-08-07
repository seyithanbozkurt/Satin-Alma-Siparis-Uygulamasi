package com.Staj.Product_service.service;

import com.Staj.Product_service.model.Product;
import com.Staj.Product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    // Tüm aktif ürünleri getir
    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }
    
    // ID'ye göre ürün getir
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    // Yeni ürün oluştur
    public Product createProduct(Product product) {
        product.setIsActive(true);
        return productRepository.save(product);
    }
    
    // Ürün güncelle
    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productDetails.getName());
                    existingProduct.setDescription(productDetails.getDescription());
                    existingProduct.setPrice(productDetails.getPrice());
                    existingProduct.setStockQuantity(productDetails.getStockQuantity());
                    return productRepository.save(existingProduct);
                });
    }
    
    // Ürün sil (soft delete)
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setIsActive(false);
                    productRepository.save(product);
                    return true;
                })
                .orElse(false);
    }
    
    // İsme göre ürün ara
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name);
    }
    
    // Fiyat aralığına göre ürün ara
    public List<Product> searchProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    // Stokta olan ürünleri getir
    public List<Product> getProductsInStock(Integer minQuantity) {
        return productRepository.findByStockQuantityGreaterThanAndIsActiveTrue(minQuantity);
    }
    
    // Stok güncelle
    public Optional<Product> updateStock(Long id, Integer newQuantity) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setStockQuantity(newQuantity);
                    return productRepository.save(product);
                });
    }
} 