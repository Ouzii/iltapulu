package laajaosk.wepa.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import laajaosk.wepa.ModelMock;
import laajaosk.wepa.MultipartFileMock;
import laajaosk.wepa.RedirectAttributesMock;
import laajaosk.wepa.SessionMock;
import laajaosk.wepa.domain.Category;
import laajaosk.wepa.domain.News;
import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.CategoryRepository;
import laajaosk.wepa.repository.NewsRepository;
import laajaosk.wepa.repository.WriterRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ModeratorServiceTest {

    @Autowired
    private ModeratorService moderatorService;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private WriterRepository writerRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private MultipartFileMock img;
    private ModelMock model;
    private SessionMock session;
    private RedirectAttributesMock ram;

    @Before
    public void setUp() {
        this.ram = new RedirectAttributesMock();
        this.session = new SessionMock();
        this.session.setAttribute("user", new Writer());
        this.model = new ModelMock();
        this.model.addAttribute("Test", new ArrayList<>());
        this.img = new MultipartFileMock("Kuva", "Kuva.jpg", "image/jpg", new Long(120), new byte[0]);
        Writer writer = new Writer();
        writer.setName("Kirjoittaja");
        writerRepository.save(writer);
        List<Writer> writers = new ArrayList<>();
        writers.add(writer);
        Category category = new Category();
        category.setName("Kategoria");
        categoryRepository.save(category);
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        News aNew = new News();
        aNew.setTitle("Otsikko");
        aNew.setIngress("Ingressi");
        aNew.setText("Leipäteksti");
        aNew.setCategories(categories);
        List<News> news = new ArrayList<>();
        news.add(aNew);
        category.setNews(news);
        categoryRepository.save(category);
        aNew.setWriters(writers);
        writer.setNews(news);
        writerRepository.save(writer);
        newsRepository.save(aNew);
    }

    @Test
    public void testAddToMessages() {
        List<String> messages = moderatorService.addToMessages("Viesti", new ArrayList<>());
        assertEquals(1, messages.size());
        assertEquals("Viesti", messages.get(0));
    }

    @Test
    public void testAddNews() throws IOException {
        assertEquals(1, newsRepository.findAll().size());
        List<Writer> writers = writerRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        List<Long> writerIds = new ArrayList<>();
        List<Long> categoryIds = new ArrayList<>();
        for (Category category : categories) {
            categoryIds.add(category.getId());
        }
        for (Writer writer : writers) {
            writerIds.add(writer.getId());
        }
        moderatorService.addNews(session, "Otsikko2", "Ingressi", "Leipäteksti", writerIds, categoryIds, this.img);
        assertEquals(2, newsRepository.findAll().size());
        assertEquals("Leipäteksti", newsRepository.findByTitle("Otsikko2").getText());
        session.setAttribute("user", null);
        moderatorService.addNews(session, "Otsikko3", "Ingressi", "Leipäteksti", writerIds, categoryIds, this.img);
        News aNew = newsRepository.findByTitle("Otsikko3");
        assertTrue(aNew == null);
    }

    @Test
    public void testModifyNews() throws IOException {
        assertEquals(1, newsRepository.findAll().size());
        List<Writer> writers = writerRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        List<Long> writerIds = new ArrayList<>();
        List<Long> categoryIds = new ArrayList<>();
        for (Category category : categories) {
            categoryIds.add(category.getId());
        }
        for (Writer writer : writers) {
            writerIds.add(writer.getId());
        }
        moderatorService.addNews(session, "Otsikko2", "Ingressi", "Leipäteksti", writerIds, categoryIds, this.img);
        News aNew = newsRepository.findByTitle("Otsikko2");
        moderatorService.ModifyNews(session, aNew.getId(),
                "Uusi otsikko", "Uusi ingressi", "Uusi teksti", writerIds, categoryIds, this.img);
        assertTrue(newsRepository.findByTitle("Uusi otsikko") != null);
        this.session.setAttribute("user", null);
        moderatorService.ModifyNews(session, aNew.getId(),
                "Uusi otsikko2", "Uusi ingressi", "Uusi teksti", writerIds, categoryIds, this.img);
        assertTrue(newsRepository.findByTitle("Uusi otsikko2") == null);
    }

    @Test
    public void testDeleteNews() throws IOException {
        assertEquals(1, newsRepository.findAll().size());
        List<Writer> writers = writerRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        List<Long> writerIds = new ArrayList<>();
        List<Long> categoryIds = new ArrayList<>();
        for (Category category : categories) {
            categoryIds.add(category.getId());
        }
        for (Writer writer : writers) {
            writerIds.add(writer.getId());
        }
        moderatorService.addNews(session, "Otsikko2", "Ingressi", "Leipäteksti", writerIds, categoryIds, this.img);
        News aNew = newsRepository.findByTitle("Otsikko2");
        assertTrue(aNew != null);
        moderatorService.deleteNews(session, aNew.getId());
        aNew = newsRepository.findByTitle("Otsikko2");
        assertTrue(aNew == null);
        
        moderatorService.addNews(session, "Otsikko2", "Ingressi", "Leipäteksti", writerIds, categoryIds, this.img);
        aNew = newsRepository.findByTitle("Otsikko2");
        assertTrue(aNew != null);
        this.session.setAttribute("user", null);
        moderatorService.deleteNews(session, aNew.getId());
        aNew = newsRepository.findByTitle("Otsikko2");
        assertTrue(aNew != null);

    }

    @Test
    public void testAddaNewToModel() throws IOException {
        assertEquals(1, newsRepository.findAll().size());
        List<Writer> writers = writerRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        List<Long> writerIds = new ArrayList<>();
        List<Long> categoryIds = new ArrayList<>();
        for (Category category : categories) {
            categoryIds.add(category.getId());
        }
        for (Writer writer : writers) {
            writerIds.add(writer.getId());
        }
        moderatorService.addNews(session, "Otsikko2", "Ingressi", "Leipäteksti", writerIds, categoryIds, this.img);
        News aNew = newsRepository.findByTitle("Otsikko2");

        this.model = (ModelMock) moderatorService.addaNewToModel(this.model, aNew.getId());
        assertTrue(this.model.containsAttribute("aNew"));
    }

    @Test
    public void testAddCategoriesAndWritersModel() {
        this.model = (ModelMock) moderatorService.addCategoriesAndWriters(this.model);
        assertTrue(this.model.containsAttribute("categories"));
        assertTrue(this.model.containsAttribute("writers"));
    }

    @Test
    public void testAddCategoriesAndWritersRedirectAttributes() {
        this.ram = (RedirectAttributesMock) moderatorService.addCategoriesAndWriters(ram);
        assertTrue(this.ram.containsAttribute("categories"));
        assertTrue(this.ram.containsAttribute("writers"));
    }

}
