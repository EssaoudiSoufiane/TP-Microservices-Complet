package com.example.car.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Car {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank private String marque;
 @NotBlank private String modele;
 @NotBlank private String matricule;
 @NotNull @Positive private Long clientId;
}
