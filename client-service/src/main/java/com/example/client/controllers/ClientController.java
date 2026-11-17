package com.example.client.controllers;
import com.example.client.entities.Client;
import com.example.client.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.util.List;
@RestController @RequestMapping("/api/client")
public class ClientController {
 private final ClientService service;
 public ClientController(ClientService service) { this.service=service; }
 @GetMapping public List<Client> findAll() { return service.findAll(); }
 @GetMapping("/{id}") public Client findById(@PathVariable Long id) { return service.findById(id); }
 @PostMapping public ResponseEntity<Client> save(@Valid @RequestBody Client client) {
  Client saved=service.addClient(client);
  return ResponseEntity.created(URI.create("/api/client/"+saved.getId())).body(saved);
 }
}
