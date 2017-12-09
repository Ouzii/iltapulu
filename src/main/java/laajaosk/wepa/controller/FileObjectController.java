package laajaosk.wepa.controller;

import laajaosk.wepa.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import laajaosk.wepa.repository.ImageRepository;

/**
 * Kontrolleri, joka huolehtii kuvan näyttämisestä.
 * @author oce
 */
@Controller
@Transactional
public class FileObjectController {

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private NewsRepository newsRepository;
    
    /**
     * Hakee ja palauttaa kuvan tietokannasta.
     * @param id
     * @return
     */
    @GetMapping(path = "/images/{id}", produces = "image/jpeg")
    @ResponseBody
    public byte[] jpegContent(@PathVariable Long id) {
        return imageRepository.findByNews(newsRepository.getOne(id)).getContent();

    }
}
