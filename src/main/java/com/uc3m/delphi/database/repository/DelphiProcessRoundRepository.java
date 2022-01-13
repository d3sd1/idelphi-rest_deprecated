package com.uc3m.delphi.database.repository;


import com.uc3m.delphi.database.model.DelphiProcessRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface DelphiProcessRoundRepository extends JpaRepository<DelphiProcessRound, Long> {
    List<DelphiProcessRound> findDelphiProcessRoundByFinishedIsFalse();
    List<DelphiProcessRound> findDelphiProcessRoundsByEndTimeBeforeAndStartedIsTrueAndFinishedIsFalse(ZonedDateTime endTime);
}