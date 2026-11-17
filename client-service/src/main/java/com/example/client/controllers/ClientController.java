package com.example.client.controllers;
import com.example.client.entities.Client;
import com.example.client.services.ClientService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/client")
public class ClientController {
    private final ClientService service;
    public ClientController(ClientService service) { this.service = service; }
    @GetMapping public List<Client> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public Client findById(@PathVariable Long id) { return service.findById(id); }
    @PostMapping public ResponseEntity<Client> save(@RequestBody Client client) { return ResponseEntity.status(HttpStatus.CREATED).body(service.addClient(client)); }
}
