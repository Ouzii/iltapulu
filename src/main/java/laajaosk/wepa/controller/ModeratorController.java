package laajaosk.wepa.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.WriterRepository;
import laajaosk.wepa.service.ModeratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Transactional
public class ModeratorController {

    @Autowired
    private WriterRepository writerRepository;
    @Autowired
    private ModeratorService moderatorService;

    @GetMapping("/moderator")
    public String list(Model model) {
        moderatorService.addCategoriesAndWriters(model);
        return "moderator";
    }

    @PostMapping("/moderator/writer")
    public String addWriter(@RequestParam String name) {
        if (writerRepository.findByName(name) == null) {
            Writer w = new Writer();
            w.setName(name);
            writerRepository.save(w);
        }

        return "redirect:/moderator";
    }



    @PostMapping("/moderator/news")
    public String add(RedirectAttributes redirectAttribute, Model model, @RequestParam String title, @RequestParam String ingress, @RequestParam String text, @RequestParam("img") MultipartFile img, @RequestParam(value = "writers", required = false) List<Long> writers, @RequestParam(value = "categories", required = false) List<Long> categories) throws IOException {
        List<String> errors = moderatorService.addNews(title, ingress, text, writers, categories, img);
        if (!errors.isEmpty()) {
            redirectAttribute.addFlashAttribute("messages", errors);
            moderatorService.addCategoriesAndWriters(redirectAttribute);
            return "redirect:/moderator";
        }

        List<String> messages = moderatorService.addToMessages("Uutinen " + title + " luotu!", new ArrayList<>());
        redirectAttribute.addFlashAttribute("messages", messages);
        return "redirect:/moderator";
    }

    @DeleteMapping("/news/{id}")
    public String delete(RedirectAttributes redirectAttribute, @PathVariable Long id) {
        String title = moderatorService.deleteNews(id);
        List<String> messages = moderatorService.addToMessages("\"" + title + "\" on poistettu onnistuneesti!", new ArrayList<>());
        redirectAttribute.addFlashAttribute("messages", messages);
        return "redirect:/";
    }

    @GetMapping("/news/{id}/modify")
    public String modify(Model model, @PathVariable Long id) {
        moderatorService.addNewsToModel(model, id);
        moderatorService.addCategoriesAndWriters(model);
        return "modify";
    }

    @PostMapping("/moderator/news/{id}")
    public String postModify(RedirectAttributes redirectAttribute, @PathVariable Long id, @RequestParam String title, @RequestParam String ingress, @RequestParam String text, @RequestParam("img") MultipartFile img, @RequestParam(value = "writers", required = false) List<Long> writers, @RequestParam(value = "categories", required = false) List<Long> categories) throws IOException {
        List<String> errors = moderatorService.ModifyNews(id, title, ingress, text, writers, categories, img);

        if (!errors.isEmpty()) {
            redirectAttribute.addFlashAttribute("messages", errors);
            moderatorService.addCategoriesAndWriters(redirectAttribute);
            return "redirect:/news/" + id;
        }

        moderatorService.ModifyNews(id, title, ingress, text, writers, categories, img);

        List<String> messages = moderatorService.addToMessages("Muokkaus onnistui!", new ArrayList<>());
        redirectAttribute.addFlashAttribute("messages", messages);

        return "redirect:/news/" + id;
    }

}
