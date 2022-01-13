package com.uc3m.delphi.database.repository;


import com.uc3m.delphi.database.model.DelphiProcess;
import com.uc3m.delphi.database.model.DelphiProcessRound;
import com.uc3m.delphi.database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DelphiProcessRepository extends JpaRepository<DelphiProcess, Long> {
    List<DelphiProcess> findAllByCoordinatorsIsContainingOrExpertsIsContaining(User coordinator, User expert);
    Optional<DelphiProcess> findByCurrentRound(DelphiProcessRound currentRound);
}