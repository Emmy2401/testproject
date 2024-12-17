package com.example.catcodedemo.Repository;

import com.example.catcodedemo.Entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
