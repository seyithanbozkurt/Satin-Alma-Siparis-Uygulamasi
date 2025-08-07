package com.Staj.Order_service.client;

import com.Staj.Order_service.dto.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CustomerClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public CustomerDTO getCustomerById(Long id){
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/customers/" + id)
                .retrieve()
                .bodyToMono(CustomerDTO.class)
                .block();
    }
    
    public Boolean hasOrderPermission(Long customerId) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/customers/" + customerId + "/has-order-permission")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
}

