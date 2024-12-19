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

import java.util.Date;

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

    @Test
    public void testGetChatByIDExist() throws Exception {
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setNom("Thaike");
        chatDTO.setCouleur("siamois");
        chatDTO.setGenre(false);
        chatDTO.setDateNaissance(new Date());
        String response = mockMvc.perform(post("/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //recupérer le chat
        Long idChat = objectMapper.readValue(response,ChatDTO.class).getId();
        // Requête GET pour vérifier le chat créé
        mockMvc.perform(get("/chats/" + idChat)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Vérification du statut
                .andExpect(jsonPath("$.id").value(idChat)) // Vérifie que l'ID correspond
                .andExpect(jsonPath("$.nom").value(chatDTO.getNom())) // Vérifie que le nom correspond
                .andExpect(jsonPath("$.couleur").value(chatDTO.getCouleur())) // Vérifie que la couleur correspond
                .andExpect(jsonPath("$.genre").value(chatDTO.getGenre())); // Vérifie que le genre correspond
    }
    @Test public void testDeleteChat() throws Exception {
        // créer un chat pour le mettre en bdd via post
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setNom("Milou");
        chatDTO.setCouleur("blanc");
        chatDTO.setGenre(true);
        chatDTO.setDateNaissance(new Date());
        String response = mockMvc.perform(post("/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //recupérer le chat créer
        Long idChat = objectMapper.readValue(response,ChatDTO.class).getId();

        //Supprimer le chat MILOU avec requête delete puis vérifier qu'il n'existe plus
        mockMvc.perform(delete("/chats/" + idChat)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/chats/" + idChat)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Le chat n'existe pas"));
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

    @Test
    public void testUpdateChat() throws Exception {
        // 1. Créer un chat initialement
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setNom("Bastet");
        chatDTO.setCouleur("Noir");
        chatDTO.setGenre(false);
        chatDTO.setDateNaissance(new Date());

        String response = mockMvc.perform(post("/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 2. Récupérer l'ID du chat créé
        Long idChat = objectMapper.readValue(response, ChatDTO.class).getId();

        // 3. Modifier les informations du chat
        ChatDTO updatedChatDTO = new ChatDTO();
        updatedChatDTO.setNom("Garfield");
        updatedChatDTO.setCouleur("Roux");
        updatedChatDTO.setGenre(true);
        updatedChatDTO.setDateNaissance(new Date());

        // 4. Effectuer une requête PUT pour mettre à jour le chat
        mockMvc.perform(put("/chats/" + idChat)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedChatDTO)))
                .andExpect(status().isOk()); // Vérification que la mise à jour est acceptée

        // 5. Effectuer une requête GET pour vérifier les nouvelles valeurs
        mockMvc.perform(get("/chats/" + idChat)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idChat)) // Vérifie que l'ID n'a pas changé
                .andExpect(jsonPath("$.nom").value(updatedChatDTO.getNom())) // Vérifie le nouveau nom
                .andExpect(jsonPath("$.couleur").value(updatedChatDTO.getCouleur())) // Vérifie la nouvelle couleur
                .andExpect(jsonPath("$.genre").value(updatedChatDTO.getGenre())); // Vérifie le nouveau genre
    }

}
