package com.example.car.services;
import com.example.car.entities.Car;
import com.example.car.models.*;
import com.example.car.repositories.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
@Service
public class CarService {
 private final CarRepository repository;
 private final RestTemplate restTemplate;
 public CarService(CarRepository repository, RestTemplate restTemplate) { this.repository=repository; this.restTemplate=restTemplate; }
 public List<CarResponse> findAll() { return repository.findAll().stream().map(this::response).toList(); }
 public CarResponse findById(Long id) { return response(repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Voiture non trouvée : "+id))); }
 public Car save(Car car) { client(car.getClientId()); car.setId(null); return repository.save(car); }
 private Client client(Long id) {
  try { return restTemplate.getForObject("http://SERVICE-CLIENT/api/client/{id}",Client.class,id); }
  catch (HttpClientErrorException.NotFound ex) { throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Client associé non trouvé : "+id,ex); }
  catch (RestClientException | IllegalStateException ex) { throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Service Client indisponible",ex); }
 }
 private CarResponse response(Car car) { return new CarResponse(car.getId(),car.getMarque(),car.getModele(),car.getMatricule(),client(car.getClientId())); }
}
