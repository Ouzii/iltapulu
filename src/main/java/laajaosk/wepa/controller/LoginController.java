package laajaosk.wepa.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import laajaosk.wepa.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model = loginService.addFooterAndHeaderData(model);
        return "login";
    }

    @PostMapping("/login")
    public String logIn(RedirectAttributes redirectAttribute, @RequestParam String username, @RequestParam String password, HttpSession session) {
        List<String> messages = new ArrayList<>();
        if (loginService.login(session, username, password, redirectAttribute)) {
            messages.add("Kirjauduttu! Tervetuloa " + username);
            redirectAttribute.addFlashAttribute("messages", messages);
            return "redirect:/";
        } else {
            messages.add("Väärä käyttäjätunnus tai salasana!");
            redirectAttribute.addFlashAttribute("messages", messages);
            return "redirect:/login";
        }
    }

    @PostMapping("/logout")
    public String logOut(RedirectAttributes redirectAttribute, HttpSession session) {
        loginService.logout(session, redirectAttribute);
        return "redirect:/";
    }
}
