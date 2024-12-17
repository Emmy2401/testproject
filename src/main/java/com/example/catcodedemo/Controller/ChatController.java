package com.example.catcodedemo.Controller;

import com.example.catcodedemo.Dto.ChatDTO;
import com.example.catcodedemo.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping
    public List<ChatDTO> getAllChats() {
        return chatService.getAllChats();
    }

    @GetMapping("/{id}")
    public ChatDTO getChatById(@PathVariable Long id) {
        return chatService.getChatById(id);
    }

    @PostMapping
    public ChatDTO createChat(@RequestBody ChatDTO chatDTO) {
        return chatService.createChat(chatDTO);
    }

    @PutMapping("/{id}")
    public ChatDTO updateChat(@PathVariable Long id, @RequestBody ChatDTO chatDTO) {
        return chatService.updateChat(id, chatDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteChat(@PathVariable Long id) {
        chatService.deleteChatById(id);
    }
}
