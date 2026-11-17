package com.example.car.controllers;
import com.example.car.entities.Car;
import com.example.car.models.CarResponse;
import com.example.car.services.CarService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.util.List;
@RestController @RequestMapping("/api/car")
public class CarController {
 private final CarService service;
 public CarController(CarService service) { this.service=service; }
 @GetMapping public List<CarResponse> findAll() { return service.findAll(); }
 @GetMapping("/{id}") public CarResponse findById(@PathVariable Long id) { return service.findById(id); }
 @PostMapping public ResponseEntity<Car> save(@Valid @RequestBody Car car) {
  Car saved=service.save(car);
  return ResponseEntity.created(URI.create("/api/car/"+saved.getId())).body(saved);
 }
}
