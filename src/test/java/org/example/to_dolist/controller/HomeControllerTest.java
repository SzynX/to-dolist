package org.example.to_dolist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class) // Ez a annotáció a HomeController tesztelésére konfigurálja a Spring kontextust
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc; // Injectáljuk a MockMvc objektumot

    @Test
    void indexPageShouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/")) // GET kérést szimulálunk a "/" útvonalra
                .andExpect(status().isOk()) // Ellenőrizzük, hogy a HTTP státusz 200 OK
                .andExpect(view().name("index")); // Ellenőrizzük, hogy a visszaadott nézet neve "index"
    }
}