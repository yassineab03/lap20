package com.example.client.services;
import com.example.client.entities.Client;
import com.example.client.repositories.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
@Service
public class ClientService {
    private final ClientRepository repository;
    public ClientService(ClientRepository repository) { this.repository = repository; }
    public List<Client> findAll() { return repository.findAll(); }
    public Client findById(Long id) { return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client non trouvé : " + id)); }
    public Client addClient(Client client) { client.setId(null); return repository.save(client); }
}
