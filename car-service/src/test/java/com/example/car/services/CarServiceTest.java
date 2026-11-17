package com.example.car.services;
import com.example.car.entities.Car;
import com.example.car.models.Client;
import com.example.car.repositories.CarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class CarServiceTest {
 private final CarRepository repository=mock(CarRepository.class);
 private final RestTemplate http=mock(RestTemplate.class);
 private final CarService service=new CarService(repository,http);
 @Test void enrichitVoitureAvecClient() {
  when(repository.findById(1L)).thenReturn(Optional.of(new Car(1L,"Renault","Clio","A-123",2L)));
  when(http.getForObject("http://SERVICE-CLIENT/api/client/{id}",Client.class,2L)).thenReturn(new Client(2L,"Soufiane",22f));
  assertEquals("Soufiane",service.findById(1L).client().nom());
 }
 @Test void voitureAbsenteRetourne404() {
  when(repository.findById(99L)).thenReturn(Optional.empty());
  assertEquals(404,assertThrows(ResponseStatusException.class,()->service.findById(99L)).getStatusCode().value());
  verifyNoInteractions(http);
 }
 @Test void serviceClientIndisponibleRetourne503() {
  when(repository.findById(1L)).thenReturn(Optional.of(new Car(1L,"Renault","Clio","A-123",2L)));
  when(http.getForObject("http://SERVICE-CLIENT/api/client/{id}",Client.class,2L)).thenThrow(new ResourceAccessException("Connexion refusée"));
  assertEquals(503,assertThrows(ResponseStatusException.class,()->service.findById(1L)).getStatusCode().value());
 }
}
