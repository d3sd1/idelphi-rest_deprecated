package com.uc3m.delphi.rest.v1.miscelaneous;

import com.uc3m.delphi.rest.Dto.VersionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/${v1API}/version")
public class VersionController {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @RequestMapping(method = RequestMethod.GET, path = "/current")
    private VersionDto getCurrentVersion() {
        return new VersionDto("1.0.1", false, this.activeProfile);
    }

    @Secured({"ADMIN"})
    @RequestMapping(method = RequestMethod.GET, path = "/all")
    private String getAllVersions() {
        return "ALL_VERSIONS_SHOULD_B_HERE";
    }

}
