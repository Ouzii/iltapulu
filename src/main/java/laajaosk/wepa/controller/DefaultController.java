package laajaosk.wepa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DefaultController {


    @GetMapping("*")
    public String list(Model model) {
        return "index";
    }

}
