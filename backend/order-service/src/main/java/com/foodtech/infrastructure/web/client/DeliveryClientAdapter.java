package com.foodtech.order.infrastructure.web.client;

import com.foodtech.order.domain.port.output.DeliveryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class DeliveryClientAdapter implements DeliveryClient {

    private final RestTemplate restTemplate;

    @Value("${delivery.service.url:http://localhost:8080}")
    private String deliveryServiceUrl;

    @Override
    public DeliveryAssignmentResponse assign(DeliveryAssignmentRequest request) {
        String url = deliveryServiceUrl + "/delivery";
        try {
            DeliveryAssignmentResponse response = restTemplate.postForObject(
                    url, request, DeliveryAssignmentResponse.class);
            if (response == null) {
                throw new RuntimeException("El servicio de delivery no devolvió respuesta");
            }
            return response;
        } catch (RestClientResponseException e) {
            throw new RuntimeException(
                    "Error en delivery-service [" + e.getStatusCode() + "]: " + e.getResponseBodyAsString(), e);
        }
    }
}
