package com.example.car.clients;

import com.example.car.models.Client;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ClientDirectoryTest {
    private final RestTemplate http = new RestTemplate();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(http).build();
    private final ClientDirectory directory = new ClientDirectory(http, "http://clients/api/client/");

    @Test
    void loadsOwnerFromConfiguredEndpoint() {
        server.expect(requestTo("http://clients/api/client/7"))
                .andRespond(withSuccess("{\"id\":7,\"nom\":\"Salma\",\"age\":24}", MediaType.APPLICATION_JSON));
        Client owner = directory.lookup(7L);
        assertNotNull(owner);
        server.verify();
    }

    @Test
    void unavailableServiceAllowsPartialResponse() {
        server.expect(requestTo("http://clients/api/client/7")).andRespond(withServerError());
        assertNull(directory.lookup(7L));
        server.verify();
    }

    @Test
    void missingOwnerDoesNotTriggerHttpRequest() {
        assertNull(directory.lookup(null));
        server.verify();
    }
}
