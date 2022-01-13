package com.uc3m.delphi.rest.v1.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${v1API}/admin/panel")
public class AdminController {

    @RequestMapping(method = RequestMethod.GET, path = "/versions")
    private String getAllVersions() {
        return "ALL_VERSIONS_SHOULD_B_HERE";
    }
}
