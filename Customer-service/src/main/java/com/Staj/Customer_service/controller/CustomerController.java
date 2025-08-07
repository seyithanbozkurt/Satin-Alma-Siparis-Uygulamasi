package com.Staj.Customer_service.controller;

import com.Staj.Customer_service.model.Customer;
import com.Staj.Customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {
    
    private final CustomerService customerService;
    
    // Tüm aktif müşterileri getir
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllActiveCustomers();
        return ResponseEntity.ok(customers);
    }
    
    // Sipariş yetkisi olan müşterileri getir
    @GetMapping("/with-order-permission")
    public ResponseEntity<List<Customer>> getCustomersWithOrderPermission() {
        List<Customer> customers = customerService.getCustomersWithOrderPermission();
        return ResponseEntity.ok(customers);
    }
    
    // ID'ye göre müşteri getir
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Email'e göre müşteri getir
    @GetMapping("/email/{email}")
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) {
        Optional<Customer> customer = customerService.getCustomerByEmail(email);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Yeni müşteri oluştur
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }
    
    // Müşteri güncelle
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        Optional<Customer> updatedCustomer = customerService.updateCustomer(id, customer);
        return updatedCustomer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Müşteri sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        boolean deleted = customerService.deleteCustomer(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    // Sipariş yetkisi ver
    @PatchMapping("/{id}/grant-order-permission")
    public ResponseEntity<Customer> grantOrderPermission(@PathVariable Long id) {
        Optional<Customer> customer = customerService.grantOrderPermission(id);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Sipariş yetkisini kaldır
    @PatchMapping("/{id}/revoke-order-permission")
    public ResponseEntity<Customer> revokeOrderPermission(@PathVariable Long id) {
        Optional<Customer> customer = customerService.revokeOrderPermission(id);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // İsme göre müşteri ara
    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        
        if (name != null && !name.isEmpty()) {
            List<Customer> customers = customerService.searchCustomersByName(name);
            return ResponseEntity.ok(customers);
        } else if (email != null && !email.isEmpty()) {
            Optional<Customer> customer = customerService.getCustomerByEmail(email);
            return customer.map(c -> ResponseEntity.ok(List.of(c)))
                    .orElse(ResponseEntity.ok(List.of()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // İsme göre müşteri ara (ayrı endpoint)
    @GetMapping("/search/name")
    public ResponseEntity<List<Customer>> searchCustomersByName(@RequestParam String name) {
        List<Customer> customers = customerService.searchCustomersByName(name);
        return ResponseEntity.ok(customers);
    }
    
    // Email'e göre müşteri ara (ayrı endpoint)
    @GetMapping("/search/email")
    public ResponseEntity<List<Customer>> searchCustomersByEmail(@RequestParam String email) {
        Optional<Customer> customer = customerService.getCustomerByEmail(email);
        return customer.map(c -> ResponseEntity.ok(List.of(c)))
                .orElse(ResponseEntity.ok(List.of()));
    }
    
    // Müşterinin sipariş yetkisi var mı kontrol et
    @GetMapping("/{id}/has-order-permission")
    public ResponseEntity<Boolean> hasOrderPermission(@PathVariable Long id) {
        boolean hasPermission = customerService.hasOrderPermission(id);
        return ResponseEntity.ok(hasPermission);
    }
} 