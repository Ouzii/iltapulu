package laajaosk.wepa.controller;

import java.util.ArrayList;
import java.util.List;
import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.NewsRepository;
import laajaosk.wepa.repository.WriterRepository;
import laajaosk.wepa.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author oce
 */
@Controller
@Transactional
public class RegistrationController {
    
    @Autowired
    private WriterRepository writerRepository;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private NewsService newsService;
    
    /**
     *
     * @param model
     * @return
     */
    @GetMapping("/register")
    public String register(Model model) {
        model = newsService.addFooterAndHeaderData(model);
        return "registering";
    }
    
    /**
     *
     * @param model
     * @param username
     * @param password
     * @param passwordConfirm
     * @return
     */
    @PostMapping("/register")
    public String registering(Model model, @RequestParam String username, @RequestParam String password, @RequestParam String passwordConfirm) {
        List<String> messages = new ArrayList<>();
        if (password.equals(passwordConfirm)) {
            Writer user = new Writer(null, username, password);
            writerRepository.save(user);
            messages.add("Onnistui!");
            model.addAttribute("messages", messages);
            model = newsService.addFooterAndHeaderData(model);
        } else {
            messages.add("Salasanat eivät täsmää!");
            model.addAttribute("messages", messages);
            model = newsService.addFooterAndHeaderData(model);
            return "registering";
        }
        
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "published");
        model.addAttribute("news", newsRepository.findAll(pageable));
        
        return "index";
    }
    
    
}
