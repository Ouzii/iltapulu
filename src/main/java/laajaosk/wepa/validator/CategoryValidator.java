package laajaosk.wepa.validator;

import java.util.ArrayList;
import java.util.List;
import laajaosk.wepa.domain.Category;

public class CategoryValidator {
    
    
    public List<String> validateCategory(String name, List<Category> categories) {
        List<String> errors = new ArrayList<>();

        if (categories.size() >= 6) {
            errors.add("Kategorioiden maksimimäärä on 6! Poista vanha kategoria ennen uuden luomista.");
            return errors;
        }
        if (name.isEmpty() || name.length() > 12) {
            errors.add("Kategorian nimen pituus on oltava 1-12 merkkiä!");
        }
        
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }
        if (categoryNames.contains(name)) {
            errors.add("Kategoria on jo olemassa!");
        }
        
        return errors;
    }
}
