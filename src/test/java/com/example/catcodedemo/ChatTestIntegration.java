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
        chatDTO.setDateNaissance(new java.util.Date());
        chatDTO.setGenre(true);

        mockMvc.perform(post("/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Miaou"))
                .andExpect(jsonPath("$.couleur").value("Blanc"));
    }

    @Test
    public void testGetAllChats() throws Exception {
        mockMvc.perform(get("/chats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

//    @Test
//    public void testGetChatById_NotFound() throws Exception {
//        mockMvc.perform(get("/chats/999")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }


}
