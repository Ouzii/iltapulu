/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laajaosk.wepa.validators;

import java.util.ArrayList;
import java.util.List;
import laajaosk.wepa.domain.Category;
import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.CategoryRepository;
import laajaosk.wepa.repository.FileObjectRepository;
import laajaosk.wepa.repository.WriterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

public class NewsValidator {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private WriterRepository writerRepository;

    public List<String> runValidations(String title, String ingress, String text, List<Long> categories, List<Long> writers, MultipartFile img) {
        List<String> errors = new ArrayList<>();
        errors = validateTitle(title, errors);
        errors = validateIngress(ingress, errors);
        errors = validateText(text, errors);
        errors = validateCategories(categories, errors);
        errors = validateWriters(writers, errors);
        errors = validateImage(img, errors);

        return errors;

    }
    
    public List<String> runValidationsNoImg(String title, String ingress, String text, List<Long> categories, List<Long> writers) {
        List<String> errors = new ArrayList<>();
        errors = validateTitle(title, errors);
        errors = validateIngress(ingress, errors);
        errors = validateText(text, errors);
        errors = validateCategories(categories, errors);
        errors = validateWriters(writers, errors);

        return errors;

    }

    public List<String> validateTitle(String title, List<String> errors) {
        if (title.length() > 60 || title.isEmpty()) {
            errors.add("Otsikon täytyy olla 1-60 merkkiä pitkä!");
        }
        return errors;
    }

    public List<String> validateIngress(String ingress, List<String> errors) {

        if (ingress.length() > 120 || ingress.isEmpty()) {
            errors.add("Ingressin täytyy olla 1-120 merkkiä pitkä!");
        }
        return errors;
    }

    public List<String> validateText(String text, List<String> errors) {

        if (text.isEmpty()) {
            errors.add("Uutisella täytyy olla leipäteksti!");
        }
        return errors;
    }

    public List<String> validateImage(MultipartFile img, List<String> errors) {
        
        if (img == null) {
            errors.add("Uutinen tarvitsee kuvan!");
            return errors;
        }
        
        if (!img.getContentType().toString().equals("image/jpeg") && !img.getContentType().toString().equals("image/jpg")) {
            errors.add("Kuvan täytyy olla jpg-muodossa!");
        }

        if (img.getSize() > 1048576) {
            errors.add("Kuva on liian suuri. Max koko 1MB!");
        }

        if (img.getOriginalFilename().isEmpty()) {
            errors.add("Kuvalla täytyy olla jokin nimi!");
        }

        return errors;
    }

    public List<String> validateCategories(List<Long> categories, List<String> errors) {

        try {
            if (categories.isEmpty() || categories == null) {
                errors.add("Uutisella täytyy olla vähintään yksi kategoria!");
            }
        } catch (Exception e) {
            errors.add("Uutisella täytyy olla vähintään yksi kategoria!");
        }

        return errors;
    }

    public List<String> validateWriters(List<Long> writers, List<String> errors) {

        try {
            if (writers.isEmpty() || writers == null) {
                errors.add("Uutisella täytyy olla vähintään yksi kirjoittaja!");
            }
        } catch (Exception e) {
            errors.add("Uutisella täytyy olla vähintään yksi kirjoittaja!");
        }

        return errors;
    }
}
