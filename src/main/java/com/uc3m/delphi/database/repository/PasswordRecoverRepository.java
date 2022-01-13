package com.uc3m.delphi.database.repository;


import com.uc3m.delphi.database.model.PasswordRecover;
import com.uc3m.delphi.database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordRecoverRepository extends JpaRepository<PasswordRecover, Long> {
    Optional<PasswordRecover> findByUserAndAndRecoverCode(User user, Long recoverCode);
}