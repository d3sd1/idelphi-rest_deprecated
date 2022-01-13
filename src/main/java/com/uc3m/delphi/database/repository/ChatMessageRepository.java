package com.uc3m.delphi.database.repository;


import com.uc3m.delphi.database.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}