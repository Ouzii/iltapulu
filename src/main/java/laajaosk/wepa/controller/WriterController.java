/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laajaosk.wepa.controller;

import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.WriterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WriterController {
    
    @Autowired
    private WriterRepository writerRepository;
    
    @GetMapping("/writers")
    public String list(Model model) {
        model.addAttribute("writers", writerRepository.findAll());
        return "writers";
    } 
    
    @PostMapping("writers")
    public String add(@RequestParam String name) {
        Writer w = new Writer();
        w.setName(name);
        writerRepository.save(w);
        
        return "redirect:/writers";
    }
}
