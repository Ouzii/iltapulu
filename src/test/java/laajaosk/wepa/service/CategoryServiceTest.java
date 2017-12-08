package laajaosk.wepa.service;

import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import laajaosk.wepa.domain.Category;
import laajaosk.wepa.repository.CategoryRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testAddCategory() {
        categoryService.addCategory("Kategoria");
        assertEquals(1, categoryRepository.findAll().size());
        categoryService.addCategory("Kategoria");
        assertEquals(1, categoryRepository.findAll().size());
    }

    @Test
    public void testDeleteCategory() {
        Category category = new Category();
        category.setName("Kategoria");
        categoryRepository.save(category);
        List<Category> categories = categoryRepository.findAll();
        assertTrue(categories.contains(category));
        categoryService.deleteCategory(categoryRepository.findByName("Kategoria").getId());
        categories = categoryRepository.findAll();
        assertFalse(categories.contains(category));
    }
    
    @Test
    public void testAddToMessages() {
        List<String> messages = categoryService.addToMessages("Viesti", new ArrayList<>());
        assertEquals(1, messages.size());
        assertEquals("Viesti", messages.get(0));
    }

}
