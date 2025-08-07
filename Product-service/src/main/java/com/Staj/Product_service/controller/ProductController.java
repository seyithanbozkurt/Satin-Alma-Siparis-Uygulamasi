package com.Staj.Product_service.controller;

import com.Staj.Product_service.model.Product;
import com.Staj.Product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
    
    private final ProductService productService;
    
    // Tüm aktif ürünleri getir
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(products);
    }
    
    // ID'ye göre ürün getir
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Yeni ürün oluştur
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
    
    // Ürün güncelle
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Optional<Product> updatedProduct = productService.updateProduct(id, product);
        return updatedProduct.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Ürün sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    // İsme göre ürün ara
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }
    
    // Fiyat aralığına göre ürün ara
    @GetMapping("/search/price")
    public ResponseEntity<List<Product>> searchProductsByPriceRange(
            @RequestParam(required = false) Double minPrice, 
            @RequestParam(required = false) Double maxPrice) {
        List<Product> products = productService.searchProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }
    
    // Stokta olan ürünleri getir
    @GetMapping("/in-stock")
    public ResponseEntity<List<Product>> getProductsInStock(@RequestParam(defaultValue = "0") Integer minQuantity) {
        List<Product> products = productService.getProductsInStock(minQuantity);
        return ResponseEntity.ok(products);
    }
    
    // Stok güncelle - Query parameter ile
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        Optional<Product> updatedProduct = productService.updateStock(id, quantity);
        return updatedProduct.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Stok güncelle - JSON body ile (alternatif)
    @PatchMapping("/{id}/stock/body")
    public ResponseEntity<Product> updateStockWithBody(@PathVariable Long id, @RequestBody StockUpdateRequest request) {
        Optional<Product> updatedProduct = productService.updateStock(id, request.getQuantity());
        return updatedProduct.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Stock update request DTO
    public static class StockUpdateRequest {
        private Integer quantity;
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
} 