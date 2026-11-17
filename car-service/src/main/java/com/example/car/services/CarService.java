package com.example.car.services;
import com.example.car.entities.Car;
import com.example.car.models.CarResponse;
import com.example.car.clients.ClientDirectory;
import com.example.car.repositories.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
@Service
public class CarService {
    private final CarRepository repository;
    private final ClientDirectory clients;

    public CarService(CarRepository repository, ClientDirectory clients) {
        this.repository = repository;
        this.clients = clients;
    }
    public List<CarResponse> findAll() { return repository.findAll().stream().map(this::toResponse).toList(); }
    public CarResponse findById(Long id) { return toResponse(repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voiture non trouvée : " + id))); }
    public Car save(Car car) { car.setId(null); return repository.save(car); }
    private CarResponse toResponse(Car car) {
        return CarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .matricule(car.getMatricule())
                .client(clients.lookup(car.getClient_id()))
                .build();
    }
}
