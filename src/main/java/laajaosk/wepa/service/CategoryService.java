package laajaosk.wepa.service;

import java.util.ArrayList;
import java.util.List;
import laajaosk.wepa.domain.Category;
import laajaosk.wepa.domain.News;
import laajaosk.wepa.repository.CategoryRepository;
import laajaosk.wepa.repository.NewsRepository;
import laajaosk.wepa.validator.CategoryValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Palvelu kategorioihin liittyvälle logiikalle.
 * @author oce
 */
@Service
public class CategoryService {

    private CategoryValidator categoryValidator = new CategoryValidator();
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private NewsRepository newsRepository;

    /**
     * Lisää merkkijono listalle ja palauta lista.
     * @param message
     * @param messages
     * @return
     */
    public List<String> addToMessages(String message, List<String> messages) {
        messages.add(message);
        return messages;
    }

    /**
     * Kategorian lisääminen. Kutsuu validaattoria tarkastamaan annetut arvot. Jos virheitä löytyy, palautetaan lista virheistä.
     * Muuten luodaan uusi kategoria ja tallennetaan se tietokantaan.
     * @param name
     * @return
     */
    public List<String> addCategory(String name) {
        List<String> errors = categoryValidator.validateCategory(name, categoryRepository.findAll());
        if (!errors.isEmpty()) {
            return errors;
        }
        Category c = new Category();
        c.setName(name);
        categoryRepository.save(c);

        return errors;
    }

    /**
     * Kategorian poistaminen. Poistetaan riippuvuudet kategorialta ja kategoriaan. Palautetaan poistetun kategorian nimi.
     * @param id
     * @return
     */
    public String deleteCategory(Long id) {
        Category category = categoryRepository.getOne(id);
        String name = "";
        if (category != null) {
            name = category.getName();
            List<Category> categories = new ArrayList<>();
            categories.add(category);
            List<News> news = newsRepository.findByCategories(categories);
            if (!news.isEmpty()) {
                for (News aNew : news) {
                    aNew.getCategories().remove(category);
                    newsRepository.save(aNew);
                }
            }
            categoryRepository.delete(category);
        }

        return name;
    }
}
