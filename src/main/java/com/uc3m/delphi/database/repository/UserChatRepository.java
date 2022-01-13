package com.uc3m.delphi.database.repository;


import com.uc3m.delphi.database.model.User;
import com.uc3m.delphi.database.model.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserChatRepository extends JpaRepository<UserChat, Long> {
    List<UserChat> findAllByUser1OrUser2Is(User user1, User user2);
    Optional<UserChat> findByUser1AndUser2(User user1, User user2);
}