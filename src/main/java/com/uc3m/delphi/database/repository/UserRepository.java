package com.uc3m.delphi.database.repository;


import com.uc3m.delphi.database.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);

    Page<User> findAllByEmailContains(String email, Pageable page);
}
