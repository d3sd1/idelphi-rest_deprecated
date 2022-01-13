package com.uc3m.delphi.cron;

import com.uc3m.delphi.database.model.DelphiProcess;
import com.uc3m.delphi.database.model.DelphiProcessRound;
import com.uc3m.delphi.database.model.User;
import com.uc3m.delphi.database.repository.DelphiProcessRepository;
import com.uc3m.delphi.database.repository.DelphiProcessRoundRepository;
import com.uc3m.delphi.rest.exception.DelphiProcessException;
import com.uc3m.delphi.ws.model.WsUpdate;
import com.uc3m.delphi.ws.model.WsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CloseRoundCron {
    @Autowired
    SimpMessagingTemplate template;
    private final DelphiProcessRoundRepository delphiProcessRoundRepository;
    private final DelphiProcessRepository delphiProcessRepository;

    public CloseRoundCron(DelphiProcessRoundRepository delphiProcessRoundRepository, DelphiProcessRepository delphiProcessRepository) {
        this.delphiProcessRoundRepository = delphiProcessRoundRepository;
        this.delphiProcessRepository = delphiProcessRepository;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    @Async
    public void runCloseRoundCron() throws DelphiProcessException {
        List<DelphiProcessRound> delphiProcessRounds = this.delphiProcessRoundRepository.findDelphiProcessRoundsByEndTimeBeforeAndStartedIsTrueAndFinishedIsFalse(ZonedDateTime.now());
        for(DelphiProcessRound delphiProcessRound:delphiProcessRounds) {
            delphiProcessRound.setFinished(true);
            delphiProcessRound.setStarted(true);
            delphiProcessRound = this.delphiProcessRoundRepository.save(delphiProcessRound);
            Optional<DelphiProcess> delphiProcessOpt = this.delphiProcessRepository.findByCurrentRound(delphiProcessRound);
            if(delphiProcessOpt.isEmpty()) {
                return;
            }
            DelphiProcess delphiProcess = delphiProcessOpt.get();
            if(delphiProcess.getRounds() == null) {
                delphiProcess.setRounds(new ArrayList<>());
            }
            delphiProcess.getRounds().add(delphiProcessRound);
            delphiProcess.setCurrentRound(null);
            delphiProcess = this.delphiProcessRepository.save(delphiProcess);

            // Other process users
            for (User user : Stream.concat(delphiProcess.getExperts().stream(), delphiProcess.getCoordinators().stream())
                    .collect(Collectors.toList())) {
                template.convertAndSendToUser(String.valueOf(user.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                        .mode(WsUpdate.MODIFY).data(delphiProcess).build());
            }
        }
    }
}
