package webgui.json;


import common.service.ServerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Json {

    @Autowired
    ServerInfoService serverInfoService;

    @RequestMapping(value = "/testconnection", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public TestResponse testConnection(@RequestBody TestRequest param) {
        return new TestResponse(SecurityContextHolder.getContext().getAuthentication().getName(), serverInfoService.getServerInfo().toString(), param.getHandshake());
    }
}

