package com.example.car.clients;

import com.example.car.models.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class ClientDirectory {
    private static final Logger log = LoggerFactory.getLogger(ClientDirectory.class);
    private final RestTemplate http;
    private final String endpoint;

    public ClientDirectory(RestTemplate http, @Value("${services.clients.url}") String endpoint) {
        this.http = http;
        this.endpoint = endpoint.replaceAll("/+$", "");
    }

    public Client lookup(Long clientId) {
        if (clientId == null) {
            return null;
        }
        try {
            return http.getForObject(endpoint + "/{id}", Client.class, clientId);
        } catch (RestClientException exception) {
            log.warn("Propriétaire {} indisponible : {}", clientId, exception.getMessage());
            return null;
        }
    }
}
