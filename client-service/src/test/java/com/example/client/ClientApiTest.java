package com.example.client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties="eureka.client.enabled=false") @AutoConfigureMockMvc @ActiveProfiles("demo")
class ClientApiTest {
 @Autowired MockMvc mvc;
 @Test void creationLectureEtValidation() throws Exception {
  String json=mvc.perform(post("/api/client").contentType(MediaType.APPLICATION_JSON).content("{\"nom\":\"Soufiane\",\"age\":22}"))
   .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber()).andReturn().getResponse().getHeader("Location");
  mvc.perform(get(json)).andExpect(status().isOk()).andExpect(jsonPath("$.nom").value("Soufiane"));
  mvc.perform(get("/api/client/999999")).andExpect(status().isNotFound());
  mvc.perform(post("/api/client").contentType(MediaType.APPLICATION_JSON).content("{\"nom\":\"\",\"age\":-1}"))
   .andExpect(status().isBadRequest());
 }
}
