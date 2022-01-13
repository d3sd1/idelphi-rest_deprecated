package com.uc3m.delphi.database.repository;


import com.uc3m.delphi.database.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    List<Language> getAllByAvailableIsTrue();
}