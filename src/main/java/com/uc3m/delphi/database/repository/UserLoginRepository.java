package com.uc3m.delphi.database.repository;


import com.uc3m.delphi.database.model.User;
import com.uc3m.delphi.database.model.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {
    List<UserLogin> findByUserAndEnabledIsTrue(User user);

    Optional<UserLogin> findByJwt(String jwt);
}