package laajaosk.wepa.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import laajaosk.wepa.ModelMock;
import laajaosk.wepa.MultipartFileMock;
import laajaosk.wepa.SessionMock;
import laajaosk.wepa.domain.Category;
import laajaosk.wepa.domain.News;
import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.CategoryRepository;
import laajaosk.wepa.repository.NewsRepository;
import laajaosk.wepa.repository.WriterRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
public class NewsServiceTest {

    @Autowired
    private NewsService newsService;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private ModeratorService moderatorService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private WriterRepository writerRepository;

    private MultipartFileMock img;
    private ModelMock model;
    private SessionMock session;

    @Before
    public void setUp() {
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
    public void testAddViewForaNew() throws IOException {
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
        assertEquals(0, aNew.getViews().size());
        newsService.addViewForaNew(aNew.getId());
        assertEquals(1, aNew.getViews().size());
    }
    
    @Test
    public void testAddAll() {
        this.model = (ModelMock) newsService.addAll(this.model, 1, "published");
        assertTrue(this.model.getAttributes().get("news") != null);
    }
    
    @Test
    public void testAddFromCategory() {
        this.model = (ModelMock) newsService.addFromCategory(this.model, "Kategoria", 1, "published");
        assertTrue(this.model.getAttributes().get("news") != null);
    }
    
    @Test
    public void testAddFooterAndHeaderData() {
        this.model = (ModelMock) newsService.addFooterAndHeaderData(this.model);
        assertTrue(this.model.containsAttribute("categories"));
        assertTrue(this.model.containsAttribute("newsByDate"));
        assertTrue(this.model.containsAttribute("newsByViews"));
    }
    
    @Test
    public void testMakeIndexModel() {
        this.model = (ModelMock) newsService.makeIndexModel(this.model);
        assertTrue(this.model.containsAttribute("news"));
    }
    
    @Test
    public void testFindCategoryListSize() {
        this.model = (ModelMock) newsService.findCategoryListSize(this.model, "Uutiset");
        assertTrue(this.model.containsAttribute("newsCount"));
        assertEquals(1, this.model.getAttributes().get("newsCount"));
        this.model = new ModelMock();
        assertFalse(this.model.containsAttribute("newsCount"));
        this.model = (ModelMock) newsService.findCategoryListSize(this.model, "Kategoria");
        assertTrue(this.model.containsAttribute("newsCount"));
        assertEquals(1, this.model.getAttributes().get("newsCount"));
    }

}
