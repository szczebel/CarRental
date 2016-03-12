package webgui.controller;


import common.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Rest {

    @Autowired TestService testService;

    @RequestMapping(value = "/testconnection", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public TestResponse serverInfo(@RequestBody TestRequest param) {
        System.out.println("param: "+param);
        return new TestResponse("unknown", testService.getServerInfo(), param.getHandshake());
    }
}

