/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laajaosk.wepa.validator;

import laajaosk.wepa.validator.CategoryValidator;
import java.util.ArrayList;
import java.util.List;
import laajaosk.wepa.domain.Category;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class CategoryValidatorTest {

    private CategoryValidator categoryValidator;

    @Before
    public void setUp() {
        this.categoryValidator = new CategoryValidator();
    }

    @Test
    public void testValidateCategory() {
        List<String> errors = categoryValidator.validateCategory("Kategoria", new ArrayList<>());
        assertEquals(0, errors.size());

        errors = categoryValidator.validateCategory("Liian pitkä nimi kategorialle on yli 12 merkkiä pitkä", new ArrayList<>());
        assertEquals(errors.get(0), "Kategorian nimen pituus on oltava 1-12 merkkiä!");

        errors = categoryValidator.validateCategory("", new ArrayList<>());
        assertEquals(errors.get(0), "Kategorian nimen pituus on oltava 1-12 merkkiä!");
        
        List<Category> categories = new ArrayList<>();
        Category category = new Category();
        category.setName("Kategoria");
        categories.add(category);
        errors = categoryValidator.validateCategory("Kategoria", categories);
        assertEquals(errors.get(0), "Kategoria on jo olemassa!");

        categories.add(new Category());
        categories.add(new Category());
        categories.add(new Category());
        categories.add(new Category());
        categories.add(new Category());
        errors = categoryValidator.validateCategory("Ok", categories);
        assertEquals(errors.get(0), "Kategorioiden maksimimäärä on 6! Poista vanha kategoria ennen uuden luomista.");
        categories.add(new Category());
        errors = categoryValidator.validateCategory("Roger", categories);
        assertEquals(errors.get(0), "Kategorioiden maksimimäärä on 6! Poista vanha kategoria ennen uuden luomista.");
    }
}
