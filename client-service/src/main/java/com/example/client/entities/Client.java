package com.example.client.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Client {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank private String nom;
 @NotNull @PositiveOrZero private Float age;
}
