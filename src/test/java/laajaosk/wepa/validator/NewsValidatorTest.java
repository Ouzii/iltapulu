/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laajaosk.wepa.validator;

import laajaosk.wepa.validator.NewsValidator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author oce
 */
public class NewsValidatorTest {

    private NewsValidator newsValidator;
//    private News aNew;
    private List<Long> categories;
    private List<Long> writers;
//    
//    @Before
//    public void setUp() {
//        this.writers = new ArrayList<>();
//        this.categories = new ArrayList<>();
//        
//        this.writers.add(new Writer(null, "Pekka Pouta", "SalainenSana"));
//        this.writers.add(new Writer(null, "Chuck Norris", "SalaisinSana"));
//        
//        this.categories.add(new Category(null, "Viihde"));
//        this.categories.add(new Category(null, "Urheilu"));
//        
//        this.aNew = new News();
//        aNew.setTitle("Testiuutinen");
//        aNew.setIngress("JUnit testaus kasvattaa suosiotaan!");
//        aNew.setText("Springboottia käyttää yhä useampi kokematon devaaja. Tutkimusten mukaan Thymeleaf on alkanut sujua myös uusilta tulokkailta.");
//        aNew.setWriters(writers);
//        aNew.setCategories(categories);
//        aNew.
//    }

    @Before
    public void setUp() {
        this.newsValidator = new NewsValidator();
        this.writers = new ArrayList<>();
        this.categories = new ArrayList<>();

        categories.add(new Long(1));
        categories.add(new Long(2));
        
        writers.add(new Long(3));
        writers.add(new Long(4));
    }

    @Test
    public void testRunValidationsNoImg() {

        List<String> errors = newsValidator.runValidationsNoImg("Testiuutinen", "JUnit testit kasvattavat suosiotaan", "Pitkä, mutta maksimissaan 255 merkkiä pitkä uutisen leipäteksti, sillä herokun tietokantaan ei voi pidempiä merkkijonoja tallettaa.", this.categories, this.writers);
        assertEquals(0, errors.size());
        errors = newsValidator.runValidationsNoImg("LIIIIIIIIIIIIIIIIIIAN PITKÄ OTSIKKO ON LIIIIIIIIIIIAN PITKÄLIIIIIIIIIIIIIIIIIIAN PITKÄ OTSIKKO ON LIIIIIIIIIIIAN PITKÄ", "JUnit testit kasvattavat suosiotaan",
                "Pitkä, mutta maksimissaan 255 merkkiä pitkä uutisen leipäteksti, sillä herokun tietokantaan ei voi pidempiä merkkijonoja tallettaa.", this.categories, this.writers);
        assertEquals(1, errors.size());
        errors = newsValidator.runValidationsNoImg("Testiuutinen", "LIIIIIIIIIIIIIIIIIIAN PITKÄ INGRESSI ON LIIIIIIIIIIIAN PITKÄLIIIIIIIIIIIIIIIIIIAN PITKÄ INGRESSI ON LIIIIIIIIIIIAN PITKÄLIIIIIIIIIIIIIIIIIIAN PITKÄ INGRESSI ON LIIIIIIIIIIIAN PITKÄLIIIIIIIIIIIIIIIIIIAN PITKÄ INGRESSI ON LIIIIIIIIIIIAN PITKÄ",
                "Pitkä, mutta maksimissaan 255 merkkiä pitkä uutisen leipäteksti, sillä herokun tietokantaan ei voi pidempiä merkkijonoja tallettaa.", this.categories, this.writers);
        assertEquals(1, errors.size());
        errors = newsValidator.runValidationsNoImg("Testiuutinen", "JUnit testit kasvattavat suosiotaan", "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!"
                + "!LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!"
                + "!LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!"
                + "!LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!", this.categories, this.writers);
        assertEquals(1, errors.size());
        errors = newsValidator.runValidationsNoImg("Testiuutinen", "JUnit testit kasvattavat suosiotaan", "Pitkä, mutta maksimissaan 255 merkkiä pitkä uutisen leipäteksti, sillä herokun tietokantaan ei voi pidempiä merkkijonoja tallettaa.", null, this.writers);
        assertEquals(1, errors.size());
        errors = newsValidator.runValidationsNoImg("Testiuutinen", "JUnit testit kasvattavat suosiotaan", "Pitkä, mutta maksimissaan 255 merkkiä pitkä uutisen leipäteksti, sillä herokun tietokantaan ei voi pidempiä merkkijonoja tallettaa.", this.categories, null);
        assertEquals(1, errors.size());
    }
    
    @Test
    public void testValidateTitle() {
        List<String> errors = newsValidator.validateTitle("Toimiva otsikko", new ArrayList<>());
        assertEquals(0, errors.size());
        errors = newsValidator.validateTitle("LIIIIIIIIIIIIIIIIIIAN PITKÄ OTSIKKO ON LIIIIIIIIIIIAN PITKÄLIIIIIIIIIIIIIIIIIIAN PITKÄ OTSIKKO ON LIIIIIIIIIIIAN PITKÄ", new ArrayList<>());
        assertEquals(errors.get(0), "Otsikon täytyy olla 1-60 merkkiä pitkä!");
        errors = newsValidator.validateTitle("", new ArrayList<>());
        assertEquals(errors.get(0), "Otsikon täytyy olla 1-60 merkkiä pitkä!");
    }
    
    @Test
    public void testValidateIngress() {
        List<String> errors = newsValidator.validateIngress("Toimiva ingressi", new ArrayList<>());
        assertEquals(0, errors.size());
        errors = newsValidator.validateIngress("LIIIIIIIIIIIIIIIIIIAN PITKÄ INGRESSI ON LIIIIIIIIIIIAN PITKÄ"
                + "LIIIIIIIIIIIIIIIIIIAN PITKÄ INGRESSI ON LIIIIIIIIIIIAN PITKÄ"
                + "LIIIIIIIIIIIIIIIIIIAN PITKÄ INGRESSI ON LIIIIIIIIIIIAN PITKÄ"
                + "LIIIIIIIIIIIIIIIIIIAN PITKÄ INGRESSI ON LIIIIIIIIIIIAN PITKÄ", new ArrayList<>());
        assertEquals(errors.get(0), "Ingressin täytyy olla 1-120 merkkiä pitkä!");
        errors = newsValidator.validateIngress("", new ArrayList<>());
        assertEquals(errors.get(0), "Ingressin täytyy olla 1-120 merkkiä pitkä!");
    }
    
    @Test
    public void testValidateText() {
        List<String> errors = newsValidator.validateText("Toimiva leipäteksti on yli yhden merkin, mutta alle 255 merkin mittainen.", new ArrayList<>());
        assertEquals(0, errors.size());
        errors = newsValidator.validateText("LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!"
                + "LIIIIIIIIIIAN PITKÄ LEIPÄTEKSTI ON YLI 255 MERKKIÄ PITKÄ!!", new ArrayList<>());
        assertEquals(errors.get(0), "Uutisen leipätekstin maksimikoko on 255 merkkiä...");
        errors = newsValidator.validateText("", new ArrayList<>());
        assertEquals(errors.get(0), "Uutisella täytyy olla leipäteksti!");
    }
    
    @Test
    public void testValidateCategories() {
        List<String> errors = newsValidator.validateCategories(categories, new ArrayList<>());
        assertEquals(0, errors.size());
        errors = newsValidator.validateCategories(null, new ArrayList<>());
        assertEquals(errors.get(0), "Uutisella täytyy olla vähintään yksi kategoria!");
        errors = newsValidator.validateCategories(new ArrayList<>(), new ArrayList<>());
        assertEquals(errors.get(0), "Uutisella täytyy olla vähintään yksi kategoria!");
    }
    
    @Test
    public void testValidateWriters() {
        List<String> errors = newsValidator.validateWriters(writers, new ArrayList<>());
        assertEquals(0, errors.size());
        errors = newsValidator.validateWriters(null, new ArrayList<>());
        assertEquals(errors.get(0), "Uutisella täytyy olla vähintään yksi kirjoittaja!");
        errors = newsValidator.validateWriters(new ArrayList<>(), new ArrayList<>());
        assertEquals(errors.get(0), "Uutisella täytyy olla vähintään yksi kirjoittaja!");
    }
}
