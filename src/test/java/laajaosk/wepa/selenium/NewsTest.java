package laajaosk.wepa.selenium;

import javax.transaction.Transactional;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.fluentlenium.adapter.junit.FluentTest;
import org.junit.Test;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class NewsTest extends FluentTest {

    @LocalServerPort
    private Integer port;


    @Test
    public void indexOpensOk() {
        goTo("http://localhost:" + port);
        assertEquals("Iltapulu", window().title());
    }
}
