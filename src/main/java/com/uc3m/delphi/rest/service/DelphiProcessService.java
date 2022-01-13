package com.uc3m.delphi.rest.service;

import com.uc3m.delphi.database.model.DelphiProcessRound;
import org.springframework.stereotype.Service;

@Service
public class DelphiProcessService {
    public void closeRound(DelphiProcessRound delphiProcessRound) {
//TODO 2: cerrar ronda actual. para ello, pasamos la roda actual a finishedRounds y cogemos la siguiente de incomingRounds pasandola a actualRound.
        //TODO 3 en cas de que no existan incomingRounds, actualRound es null y se marca el delphiprocess como processFinished=true
        //TODO 3 cont. notificar mediante websocket a coordinador que se ha cerrado la ronda, que checkee el estado de la misma
        //TODO CONT 3 notificar mediante websocket a experto que se ha cerrado la ronda (y ya)
        delphiProcessRound.setFinished(true);
        // this.delphiProcessRoundRepository.save(delphiProcessRound);
    }
}
