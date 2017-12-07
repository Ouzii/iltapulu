package laajaosk.wepa.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.WriterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private WriterRepository writerRepository;
    @Autowired
    private NewsController newsController;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model = newsController.addFooterAndHeaderData(model);
        return "login";
    }

    @PostMapping("/login")
    public String logIn(Model model, @RequestParam String username, @RequestParam String password, HttpSession session) {
        List<String> messages = new ArrayList<>();
        Writer user = writerRepository.findByName(username);
        if (user != null) {
            if (user.getPassword().equals(password) && user.getName().equals(username)) {
                session.setAttribute("user", user);
                messages.add("Kirjauduttu! Tervetuloa " + username);
                model.addAttribute("messages", messages);
                model = newsController.addFooterAndHeaderData(model);
                model = newsController.makeIndexModel(model);
                return "index";
            } else {
                messages.add("Väärä käyttäjätunnus tai salasana!");
                model.addAttribute("messages", messages);
                model = newsController.addFooterAndHeaderData(model);
                return "login";
            }
        } else {
            messages.add("Väärä käyttäjätunnus tai salasana!");
            model.addAttribute("messages", messages);
            model = newsController.addFooterAndHeaderData(model);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logOut(Model model, HttpSession session) {
        List<String> messages = new ArrayList<>();
        session.setAttribute("user", null);
        messages.add("Kirjauduttu ulos");
        model.addAttribute("messages", messages);
        model = newsController.makeIndexModel(model);
        model = newsController.addFooterAndHeaderData(model);
        return "index";
    }
}
