package com.uc3m.delphi.rest.v1.miscelaneous;

import com.uc3m.delphi.database.model.Language;
import com.uc3m.delphi.database.repository.DelphiProcessRepository;
import com.uc3m.delphi.database.repository.LanguageRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/${v1API}/delphi")
public class DelphiController {

    private final DelphiProcessRepository delphiProcessRepository;
    private final LanguageRepository languageRepository;

    public DelphiController(DelphiProcessRepository delphiProcessRepository, LanguageRepository languageRepository) {
        this.delphiProcessRepository = delphiProcessRepository;
        this.languageRepository = languageRepository;
    }


    @RequestMapping(method = RequestMethod.GET, path = "/langs")
    public List<Language> getAvailableLangs() {
        return this.languageRepository.getAllByAvailableIsTrue();
    }


}
