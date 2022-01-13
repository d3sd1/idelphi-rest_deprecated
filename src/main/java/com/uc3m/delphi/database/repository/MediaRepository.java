package com.uc3m.delphi.database.repository;


import com.uc3m.delphi.database.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Long> {
}