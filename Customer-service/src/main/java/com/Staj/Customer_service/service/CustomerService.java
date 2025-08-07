package com.Staj.Customer_service.service;

import com.Staj.Customer_service.model.Customer;
import com.Staj.Customer_service.respository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    // Tüm aktif müşterileri getir
    public List<Customer> getAllActiveCustomers() {
        return customerRepository.findByIsActiveTrue();
    }
    
    // Sipariş yetkisi olan müşterileri getir
    public List<Customer> getCustomersWithOrderPermission() {
        return customerRepository.findByHasOrderPermissionTrueAndIsActiveTrue();
    }
    
    // ID'ye göre müşteri getir
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }
    
    // Email'e göre müşteri getir
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmailAndIsActiveTrue(email);
    }
    
    // Yeni müşteri oluştur
    public Customer createCustomer(Customer customer) {
        customer.setIsActive(true);
        customer.setHasOrderPermission(false); // Varsayılan olarak sipariş yetkisi yok
        return customerRepository.save(customer);
    }
    
    // Müşteri güncelle
    public Optional<Customer> updateCustomer(Long id, Customer customerDetails) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setName(customerDetails.getName());
                    existingCustomer.setEmail(customerDetails.getEmail());
                    existingCustomer.setPhone(customerDetails.getPhone());
                    existingCustomer.setAddress(customerDetails.getAddress());
                    return customerRepository.save(existingCustomer);
                });
    }
    
    // Müşteri sil (soft delete)
    public boolean deleteCustomer(Long id) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setIsActive(false);
                    customerRepository.save(customer);
                    return true;
                })
                .orElse(false);
    }
    
    // Sipariş yetkisi ver
    public Optional<Customer> grantOrderPermission(Long id) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setHasOrderPermission(true);
                    return customerRepository.save(customer);
                });
    }
    
    // Sipariş yetkisini kaldır
    public Optional<Customer> revokeOrderPermission(Long id) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setHasOrderPermission(false);
                    return customerRepository.save(customer);
                });
    }
    
    // İsme göre müşteri ara
    public List<Customer> searchCustomersByName(String name) {
        return customerRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name);
    }
    
    // Müşterinin sipariş yetkisi var mı kontrol et
    public boolean hasOrderPermission(Long customerId) {
        return customerRepository.findById(customerId)
                .map(customer -> customer.getHasOrderPermission() && customer.getIsActive())
                .orElse(false);
    }
} 