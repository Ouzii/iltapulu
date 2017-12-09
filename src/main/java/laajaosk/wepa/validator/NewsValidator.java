package laajaosk.wepa.validator;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Uutisen validaattori.
 * @author oce
 */
public class NewsValidator {

    /**
     * Ajetaan kaikki eri validaatiometodit yhdestä metodista.
     * @param title
     * @param ingress
     * @param text
     * @param categories
     * @param writers
     * @param img
     * @return
     */
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
    
    /**
     * Ajetaan kaikki paitsi kuvan validoiva metodi yhdestä metodista.
     * @param title
     * @param ingress
     * @param text
     * @param categories
     * @param writers
     * @return
     */
    public List<String> runValidationsNoImg(String title, String ingress, String text, List<Long> categories, List<Long> writers) {
        List<String> errors = new ArrayList<>();
        errors = validateTitle(title, errors);
        errors = validateIngress(ingress, errors);
        errors = validateText(text, errors);
        errors = validateCategories(categories, errors);
        errors = validateWriters(writers, errors);

        return errors;

    }

    /**
     * Validoidaan otsikon pituus.
     * @param title
     * @param errors
     * @return
     */
    public List<String> validateTitle(String title, List<String> errors) {
        if (title.length() > 60 || title.isEmpty()) {
            errors.add("Otsikon täytyy olla 1-60 merkkiä pitkä!");
        }
        return errors;
    }

    /**
     * Validoidaan ingressin pituus.
     * @param ingress
     * @param errors
     * @return
     */
    public List<String> validateIngress(String ingress, List<String> errors) {

        if (ingress.length() > 120 || ingress.isEmpty()) {
            errors.add("Ingressin täytyy olla 1-120 merkkiä pitkä!");
        }
        return errors;
    }

    /**
     * Validoidaan leipätekstin pituus.
     * @param text
     * @param errors
     * @return
     */
    public List<String> validateText(String text, List<String> errors) {

        if (text.isEmpty()) {
            errors.add("Uutisella täytyy olla leipäteksti!");
        }
        
        if (text.length() > 255) {
            errors.add("Uutisen leipätekstin maksimikoko on 255 merkkiä...");
        }
        
        return errors;
    }

    /**
     * Validoidaan kuvan koko ja muoto.
     * @param img
     * @param errors
     * @return
     */
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

    /**
     * Validoidaan uutisen kategoria-yhteydet.
     * @param categories
     * @param errors
     * @return
     */
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

    /**
     * Validoidaan uutisen kirjoittaja-yhteydet.
     * @param writers
     * @param errors
     * @return
     */
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
