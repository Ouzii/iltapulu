package laajaosk.wepa.controller;

import java.util.ArrayList;
import java.util.List;
import laajaosk.wepa.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Kontrolleri, joka huolehtii kategorioiden luomisesta ja poistamisesta.
 * @author oce
 */
@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Kategorian luominen.
     * @param redirectAttribute
     * @param name
     * @return
     */
    @PostMapping("/moderator/category")
    public String addCategory(RedirectAttributes redirectAttribute, @RequestParam String name) {
        List<String> errors = categoryService.addCategory(name);
        if (!errors.isEmpty()) {
            redirectAttribute.addFlashAttribute("messages", errors);
            return "redirect:/moderator";
        }
        List<String> messages = categoryService.addToMessages("Kategoria " + name + " luotu!", new ArrayList<>());
        redirectAttribute.addFlashAttribute("messages", messages);
        return "redirect:/moderator";
    }

    /**
     * Kategorian poistaminen.
     * @param redirectAttribute
     * @param id
     * @return
     */
    @DeleteMapping("/moderator/category/{id}")
    public String deleteCategory(RedirectAttributes redirectAttribute, @PathVariable Long id) {
        String name = categoryService.deleteCategory(id);
        List<String> messages = categoryService.addToMessages("Kategoria " + name + " poistettu!", new ArrayList<>());
        redirectAttribute.addFlashAttribute("messages", messages);
        return "redirect:/moderator";
    }
}
