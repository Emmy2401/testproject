package com.example.catcodedemo;

import com.example.catcodedemo.Dto.ChatDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//les tests d'intégration se  font sur le controller
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
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

    @Test
    public void testDeleteChat_NotFound() throws Exception {
        // prendre l'id qui n'existe pas exemple : 999
        //simuler l'appel
        mockMvc.perform(delete("/chats/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Le chat n'existe pas"));
    }

    @Test
    public void testUpdateChat_NotFound() throws Exception {
        // On teste une mise à jour pour un ID qui n'existe pas, par exemple 999
        Long idInexistant = 999L;

        // Pas besoin de fournir un objet complet car l'erreur se produit avant
        // toute validation ou traitement des données du corps de la requête.
        mockMvc.perform(put("/chats/" + idInexistant)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Envoi d'un corps minimal valide
                .andExpect(status().isNotFound()) // On s'attend à un 404 Not Found
                .andExpect(content().string("Le chat n'existe pas")); // On vérifie le message d'erreur attendu
    }



}
