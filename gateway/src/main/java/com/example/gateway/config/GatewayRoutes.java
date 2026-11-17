package com.example.gateway.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
@Configuration
public class GatewayRoutes {
 @Bean RouteLocator routes(RouteLocatorBuilder builder) {
  return builder.routes()
   .route("clients", r -> r.path("/SERVICE-CLIENT/**").filters(f -> f.stripPrefix(1)).uri("lb://SERVICE-CLIENT"))
   .route("cars", r -> r.path("/SERVICE-CAR/**").filters(f -> f.stripPrefix(1)).uri("lb://SERVICE-CAR"))
   .build();
 }
}
