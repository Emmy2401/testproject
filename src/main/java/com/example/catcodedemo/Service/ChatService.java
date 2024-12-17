package com.example.catcodedemo.Service;

import com.example.catcodedemo.Dto.ChatDTO;
import com.example.catcodedemo.Entity.Chat;
import com.example.catcodedemo.Repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    private ChatDTO convertToDTO(Chat chat) {
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setId(chat.getId());
        chatDTO.setNom(chat.getNom());
        chatDTO.setDateNaissance(chat.getDateNaissance());
        chatDTO.setCouleur(chat.getCouleur());
        chatDTO.setGenre(chat.getGenre());
        return chatDTO;
    }

    private Chat convertToEntity(ChatDTO chatDTO) {
        Chat chat = new Chat();
        chat.setId(chatDTO.getId());
        chat.setNom(chatDTO.getNom());
        chat.setDateNaissance(chatDTO.getDateNaissance());
        chat.setCouleur(chatDTO.getCouleur());
        chat.setGenre(chatDTO.getGenre());
        return chat;
    }

    public List<ChatDTO> getAllChats() {
        return chatRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ChatDTO getChatById(Long id) {
        return chatRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(()-> new RuntimeException("Le chat n'existe pas"));
    }

    public ChatDTO createChat(ChatDTO chatDTO) {
        Chat chat = convertToEntity(chatDTO);
        Chat chatSauvegarde = chatRepository.save(chat);
        return convertToDTO(chatSauvegarde);
    }

    public ChatDTO updateChat(Long id, ChatDTO chatDTO) {
        Chat chat = chatRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Le chat n'existe pas"));

        chat.setNom(chatDTO.getNom());
        chat.setDateNaissance(chatDTO.getDateNaissance());
        chat.setCouleur(chatDTO.getCouleur());
        chat.setGenre(chatDTO.getGenre());

        Chat chatUpdate = chatRepository.save(chat);
        return convertToDTO(chatUpdate);
    }

    public void deleteChatById(Long id) {
        Chat chat = chatRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Le chat n'existe pas"));
        chatRepository.deleteById(chat.getId());
    }
}
