package laajaosk.wepa.service;

import javax.transaction.Transactional;
import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.WriterRepository;
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
public class LoginServiceTest {
   
    @Autowired
    private LoginService loginService;
    @Autowired
    private WriterRepository writerRepository;
    
    @Before
    public void setUp() {
        Writer user = new Writer();
        user.setName("Tunnus");
        user.setPassword("Salasana");
    }

    
    @Test
    public void testLogin() {
        assertTrue(true);
    }
    
    @Test
    public void testLogout() {
        assertTrue(true);
    }
}
