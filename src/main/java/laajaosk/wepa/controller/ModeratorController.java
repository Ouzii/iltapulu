package laajaosk.wepa.controller;

import laajaosk.wepa.domain.Category;
import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.CategoryRepository;
import laajaosk.wepa.repository.WriterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ModeratorController {
    
    @Autowired
    private WriterRepository writerRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    
    @GetMapping("/moderator")
    public String list(Model model) {
        model.addAttribute("writers", writerRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "moderator";
    } 
    
    @PostMapping("/moderator/writer")
    public String addWriter(@RequestParam String name) {
        Writer w = new Writer();
        w.setName(name.toLowerCase());
        writerRepository.save(w);
        
        return "redirect:/moderator";
    }
    
    @PostMapping("/moderator/category")
    public String addCategory(@RequestParam String name) {
        Category c = new Category();
        c.setName(name.toLowerCase());
        categoryRepository.save(c);
        
        return "redirect:/moderator";
    }
}
