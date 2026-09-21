package com.pedidos360.msorders.client;

import com.pedidos360.msorders.exception.CatalogConflictException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CatalogClient {

    private final RestClient restClient;

    public CatalogClient(@Value("${catalog.service.url:http://localhost:8081}") String catalogUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(catalogUrl)
                .build();
    }

    public void decreaseStock(Long productId, Integer quantity) {
        restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/{id}/stock/decrease")
                        .queryParam("quantity", quantity)
                        .build(productId))
                .retrieve()
                .onStatus(status -> status.value() == 409, (request, response) -> {
                    throw new CatalogConflictException("Conflict updating stock for product: " + productId);
                })
                .toBodilessEntity();
    }
}
