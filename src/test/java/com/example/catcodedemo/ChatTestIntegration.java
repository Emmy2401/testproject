package com.example.catcodedemo;

import com.example.catcodedemo.Dto.ChatDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//les tests d'intégration se  font sur le controller
public class ChatTestIntegration {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateChat() throws Exception {
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setNom("Miaou");
        chatDTO.setCouleur("Blanc");
        chatDTO.setGenre(true);

        mockMvc.perform(post("/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Miaou"))
                .andExpect(jsonPath("$.couleur").value("Blanc"))
                .andExpect(jsonPath("$.genre").value(true));
    }

    @Test
    public void testGetAllChats() throws Exception {
        mockMvc.perform(get("/chats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // NB pour les tests : mettre un globalgestionhandler 
    @Test
    public void testGetChatById_NotFound() throws Exception {
        // Configuration : nous savons que l'ID 999 n'existe pas

        // Simulation de l'appel au point de terminaison
        mockMvc.perform(get("/chats/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Le chat n'existe pas")); // Test que le message correspond à celui attendu
    }


}
