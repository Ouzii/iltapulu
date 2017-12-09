package laajaosk.wepa.service;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.WriterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Palvelu sisäänkirjautumisen logiikalle.
 * @author oce
 */
@Service
public class LoginService {

    @Autowired
    private NewsService newsService;
    @Autowired
    private WriterRepository writerRepository;

    /**
     * Lisää headerissa ja footerissa tarvittavat tiedot.
     * @param model
     * @return
     */
    public Model addFooterAndHeaderData(Model model) {
        model = newsService.addFooterAndHeaderData(model);
        return model;
    }

    /**
     * Uloskirjautuminen. Asettaa sessiolle "user"-arvoksi null.
     * @param session
     * @param redirectAttribute
     * @return
     */
    public RedirectAttributes logout(HttpSession session, RedirectAttributes redirectAttribute) {
        List<String> messages = new ArrayList<>();
        session.setAttribute("user", null);
        messages.add("Kirjauduttu ulos");
        redirectAttribute.addFlashAttribute("messages", messages);
        return redirectAttribute;
    }

    /**
     * Sisäänkirjautuminen. Jos käyttäjätunnus ja salasana täsmäävät tietokannasta löytyviin, asettaa session
     * "user"-arvoksi käyttäjän.
     * @param session
     * @param username
     * @param password
     * @param model
     * @return
     */
    public Boolean login(HttpSession session, String username, String password, Model model) {
        Writer user = writerRepository.findByName(username);
        if (user == null) {
            return false;
        }

        if (user.getPassword().equals(password) && user.getName().equals(username)) {
            session.setAttribute("user", user);
            return true;
        } else {
            return false;
        }
    }
    
}
