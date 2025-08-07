package com.Staj.Order_service.client;

import com.Staj.Order_service.dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ProductClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public ProductDto getProductById(Long id){
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/api/products/" + id)
                .retrieve()
                .bodyToMono(ProductDto.class)
                .block();
    }
}
