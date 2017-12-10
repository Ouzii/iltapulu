package laajaosk.wepa.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
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

/**
 * Kontrolleri, joka huolehtii kirjautumista vaativista toiminnoista, eli uutisen luominen/muokkaaminen yms.
 * @author oce
 */
@Controller
@Transactional
public class ModeratorController {

    @Autowired
    private WriterRepository writerRepository;
    @Autowired
    private ModeratorService moderatorService;

    /**
     * Luo moderaattorisivun.
     * @param model
     * @return
     */
    @GetMapping("/moderator")
    public String list(Model model) {
        moderatorService.addCategoriesAndWriters(model);
        return "moderator";
    }

    /**
     * Luo uutisen moderatorServicen avulla.
     * @param session
     * @param redirectAttribute
     * @param model
     * @param title
     * @param ingress
     * @param text
     * @param img
     * @param writers
     * @param categories
     * @return
     * @throws IOException
     */
    @PostMapping("/moderator/news")
    public String add(HttpSession session, RedirectAttributes redirectAttribute, Model model, @RequestParam String title, @RequestParam String ingress, @RequestParam String text, @RequestParam("img") MultipartFile img, @RequestParam(value = "writers", required = false) List<Long> writers, @RequestParam(value = "categories", required = false) List<Long> categories) throws IOException {
        List<String> errors = moderatorService.addNews(session, title, ingress, text, writers, categories, img);
        if (!errors.isEmpty()) {
            redirectAttribute.addFlashAttribute("messages", errors);
            moderatorService.addCategoriesAndWriters(redirectAttribute);
            return "redirect:/moderator";
        }

        List<String> messages = moderatorService.addToMessages("Uutinen " + title + " luotu!", new ArrayList<>());
        redirectAttribute.addFlashAttribute("messages", messages);
        return "redirect:/moderator";
    }

    /**
     * Poistaa uutisen.
     * @param session
     * @param redirectAttribute
     * @param id
     * @return
     */
    @DeleteMapping("/news/{id}")
    public String delete(HttpSession session, RedirectAttributes redirectAttribute, @PathVariable Long id) {
        String title = moderatorService.deleteNews(session, id);
        List<String> messages = new ArrayList<>();
        if (title.equals("Et ole kirjautuneena!")) {
            messages = moderatorService.addToMessages(title, new ArrayList<>());
            redirectAttribute.addFlashAttribute("messages", messages);
            return "redirect:/";
        }
        messages = moderatorService.addToMessages("\"" + title + "\" on poistettu onnistuneesti!", new ArrayList<>());
        redirectAttribute.addFlashAttribute("messages", messages);
        return "redirect:/";
    }

    /**
     * Luo uutisen muokkaussivun.
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/news/{id}/modify")
    public String modify(Model model, @PathVariable Long id) {
        moderatorService.addaNewToModel(model, id);
        moderatorService.addCategoriesAndWriters(model);
        return "modify";
    }

    /**
     * Muokkaa uutista moderatorServicen avulla.
     * @param session
     * @param redirectAttribute
     * @param id
     * @param title
     * @param ingress
     * @param text
     * @param img
     * @param writers
     * @param categories
     * @return
     * @throws IOException
     */
    @PostMapping("/moderator/news/{id}")
    public String postModify(HttpSession session, RedirectAttributes redirectAttribute, @PathVariable Long id, @RequestParam String title, @RequestParam String ingress, @RequestParam String text, @RequestParam("img") MultipartFile img, @RequestParam(value = "writers", required = false) List<Long> writers, @RequestParam(value = "categories", required = false) List<Long> categories) throws IOException {
        List<String> errors = moderatorService.ModifyNews(session, id, title, ingress, text, writers, categories, img);

        if (!errors.isEmpty()) {
            redirectAttribute.addFlashAttribute("messages", errors);
            moderatorService.addCategoriesAndWriters(redirectAttribute);
            return "redirect:/news/" + id;
        }

        List<String> messages = moderatorService.addToMessages("Muokkaus onnistui!", new ArrayList<>());
        redirectAttribute.addFlashAttribute("messages", messages);

        return "redirect:/news/" + id;
    }

}
