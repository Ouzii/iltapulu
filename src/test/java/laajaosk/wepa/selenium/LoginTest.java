package laajaosk.wepa.selenium;

import javax.transaction.Transactional;
import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.WriterRepository;
import org.fluentlenium.adapter.junit.FluentTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class LoginTest extends FluentTest {
    
    @LocalServerPort
    private Integer port;
    
    @Autowired
    private WriterRepository writerRepository;
    
    @Test
    public void testLogin() {
        goTo("http://localhost:" + port);
        assertEquals("Iltapulu", window().title());
        
        Writer user = new Writer();
        user.setName("tunnus");
        user.setPassword("salasana");
        writerRepository.save(user);
        
        goTo("http://localhost:" + port +"/login");
        find("#username").fill().with("tunnus");
        find("#password").fill().with("salasana");
        find("form").first().submit();
        
        assertTrue(pageSource().contains("Kirjaudu sisään"));
        
    }
}
