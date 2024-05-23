package core.SpringBootControllers;

import api.Android;
import core.managers.DriverManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
public class HelloController {

    @RequestMapping("/")
    public String index() {
        return "index.html";
    }
}