package webgui.controller;


import common.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Rest {

    @Autowired TestService testService;

    @RequestMapping(value = "/what")
    public String rest() {
        return "What? What? " + testService.getServerInfo();
    }
}
