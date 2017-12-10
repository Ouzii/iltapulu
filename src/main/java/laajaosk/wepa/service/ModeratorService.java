package laajaosk.wepa.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import laajaosk.wepa.validator.NewsValidator;
import laajaosk.wepa.domain.*;
import laajaosk.wepa.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static org.thymeleaf.spring5.util.FieldUtils.errors;

/**
 * Palvelu kirjautumista vaativien toimintojen logiikalle.
 *
 * @author oce
 */
@Service
public class ModeratorService {

    @Autowired
    private WriterRepository writerRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ViewRepository viewRepository;

    private final NewsValidator newsValidator = new NewsValidator();

    /**
     * Lisää viestin listaan ja palauttaa listan.
     *
     * @param message
     * @param messages
     * @return
     */
    public List<String> addToMessages(String message, List<String> messages) {
        messages.add(message);
        return messages;
    }

    /**
     * Muokkaa uutista. Kutsuu ensin validaattoria sen mukaan, ollaanko kuvaa
     * vaihtamassa. Jos validaattori palauttaa virheitä, palautetaan virheet
     * kontrollerille. Muuten suoritetaan muutokset kutsumalla yksityisiä
     * metodeja, jotka hoitavat muutosten tekemisen ja tietokantaan
     * tallentamisen.
     *
     * @param session
     * @param id
     * @param title
     * @param ingress
     * @param text
     * @param writers
     * @param categories
     * @param img
     * @return
     * @throws IOException
     */
    public List<String> ModifyNews(HttpSession session, Long id, String title, String ingress, String text, List<Long> writers, List<Long> categories, MultipartFile img) throws IOException {
        List<String> errors = new ArrayList<>();
        if (session.getAttribute("user") == null) {
            errors.add("Et ole kirjautuneena!");
            return errors;
        }
        if (!img.isEmpty()) {
            errors = newsValidator.runValidations(title, ingress, text, categories, writers, img);
        } else {
            errors = newsValidator.runValidationsNoImg(title, ingress, text, categories, writers);
        }
        if (!errors.isEmpty()) {
            return errors;
        }

        News aNew = doModificationsToNews(id, title, ingress, text, writers, categories);

        if (img != null && !img.isEmpty()) {
            doModificationsToImg(aNew, img);
        }

        return errors;
    }

    /**
     * Korvaa vanhan kuvan uudella kuvalla.
     */
    private void doModificationsToImg(News aNew, MultipartFile img) throws IOException {
        Image old = imageRepository.findByNews(aNew);
        createEntityOutOfImg(aNew, img);
        imageRepository.delete(old);
    }

    /**
     * Luo annetusta MultipartFile -oliosta Image-olion.
     */
    private void createEntityOutOfImg(News aNew, MultipartFile img) throws IOException {
        Image fo = new Image();

        fo.setName(img.getOriginalFilename());
        fo.setContent(img.getBytes());
        fo.setContentLength(img.getSize());
        fo.setContentType(img.getContentType());

        fo.setNews(aNew);
        imageRepository.save(fo);
    }

    /**
     * Hakee halutun uutisen ja kutsuu muita metodeja suorittamaan muutokset.
     */
    private News doModificationsToNews(Long id, String title, String ingress, String text, List<Long> writers, List<Long> categories) {
        News aNew = newsRepository.getOne(id);
        setNewsAttributes(aNew, title, ingress, text);
        addToWritersAndCategories(aNew, writers, categories);
        newsRepository.save(aNew);
        return aNew;
    }

    /**
     * Asettaa uutiselle attribuutit.
     */
    private News setNewsAttributes(News aNew, String title, String ingress, String text) {
        aNew.setTitle(title);
        aNew.setIngress(ingress);
        aNew.setText(text);
        aNew.setWriters(new ArrayList<>());
        aNew.setCategories(new ArrayList<>());
        return aNew;
    }

    /**
     * Lisää uutiselle kirjoittajat ja kategoriat ja toisinpäin.
     */
    private void addToWritersAndCategories(News aNew, List<Long> writers, List<Long> categories) {
        for (Long writer : writers) {
            aNew.getWriters().add(writerRepository.getOne(writer));
            writerRepository.getOne(writer).getNews().add(aNew);
        }
        for (Long category : categories) {
            aNew.getCategories().add(categoryRepository.getOne(category));
            categoryRepository.getOne(category).getNews().add(aNew);
        }
    }

    /**
     * Uutisen lisääminen. Kutsuu validaattoria tarkistamaan syötteet. Jos virheitä löytyy, palautetaan virheet takaisin
     * kontrolleriin. Muuten kutsutaan muita yksityisiä metodeja luomaan ja tallentamaan uutinen tietokantaan.
     * @param session
     * @param title
     * @param ingress
     * @param text
     * @param writers
     * @param categories
     * @param img
     * @return
     * @throws IOException
     */
    public List<String> addNews(HttpSession session, String title, String ingress, String text, List<Long> writers, List<Long> categories, MultipartFile img) throws IOException {
        List<String> errors = newsValidator.runValidations(title, ingress, text, categories, writers, img);
        if (session.getAttribute("user") == null) {
            errors.add("Et ole kirjautuneena!");
            return errors;
        }
        if (!errors.isEmpty()) {
            return errors;
        }
        News aNew = setNewsAttributes(new News(), title, ingress, text);
        addToWritersAndCategories(aNew, writers, categories);
        newsRepository.save(aNew);
        createEntityOutOfImg(aNew, img);

        return errors;
    }

    /**
     * Uutisen poistaminen. Kutsutaan muita metodeja poistamaan riippuvuudet uutiselta/uutiseen. Lopuksi poistetaan
     * uutinen tietokannasta.
     * @param session
     * @param id
     * @return
     */
    public String deleteNews(HttpSession session, Long id) {
        if (session.getAttribute("user") == null) {
            return "Et ole kirjautuneena!";
        }
        News aNew = newsRepository.getOne(id);
        String title = aNew.getTitle();
        deleteCategoryRelationsFromNews(aNew);
        deleteWriterRelationsFromNews(aNew);
        for (View view : aNew.getViews()) {
            viewRepository.delete(view);
        }

        imageRepository.delete(imageRepository.findByNews(aNew));
        newsRepository.delete(aNew);
        return title;
    }

    /**
     * Poistetaan uutisen riippuvuudet kategorioihin ja toisin päin.
     */
    private void deleteCategoryRelationsFromNews(News aNew) {
        for (Category category : aNew.getCategories()) {
            Category category2 = categoryRepository.getOne(category.getId());
            List<News> n = category2.getNews();
            n.remove(aNew);
            category2.setNews(n);
            categoryRepository.save(category2);
        }
    }

    /**
     * Poistetaan uutisen riippuvuudet kirjoittajiin ja toisin päin.
     */
    private void deleteWriterRelationsFromNews(News aNew) {
        for (Writer writer : aNew.getWriters()) {
            Writer writer2 = writerRepository.getOne(writer.getId());
            List<News> n = writer2.getNews();
            n.remove(aNew);
            writer2.setNews(n);
            writerRepository.save(writer2);
        }
    }

    /**
     * Lisätään redirectAttribuutille kentät, jotka sisältävät kaikki olemassaolevat kategoriat ja kirjoittajat.
     * @param redirectAttribute
     * @return
     */
    public RedirectAttributes addCategoriesAndWriters(RedirectAttributes redirectAttribute) {
        redirectAttribute.addFlashAttribute("writers", writerRepository.findAll());
        redirectAttribute.addFlashAttribute("categories", categoryRepository.findAll());
        return redirectAttribute;
    }

    /**
     * Lisätään modelille kentät, jotka sisältävät kaikki olemassaolevat kategoriat ja kirjoittajat.
     * @param model
     * @return
     */
    public Model addCategoriesAndWriters(Model model) {
        model.addAttribute("writers", writerRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return model;
    }

    /**
     * Lisätään uutinen attribuuttina modelille.
     * @param model
     * @param id
     * @return
     */
    public Model addaNewToModel(Model model, Long id) {
        model.addAttribute("aNew", newsRepository.getOne(id));
        return model;
    }
}
