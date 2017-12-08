package laajaosk.wepa.mockMvc;

import laajaosk.wepa.service.CategoryService;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NewsTest {

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    public void statusOk() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }


    @Test
    public void responseTypeHTML() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    public void responseContainsTextIltapulu() throws Exception {
        MvcResult res = mockMvc.perform(get("/"))
                .andReturn();

        String content = res.getResponse().getContentAsString();
        Assert.assertTrue(content.contains("Iltapulu"));
    }
    
    @Test
    public void loginPageOk() throws Exception {
        MvcResult res = mockMvc.perform(get("/login")).andExpect(status().isOk()).andReturn();
        
        String content = res.getResponse().getContentAsString();
        assertTrue(content.contains("Käyttäjätunnus"));
        assertTrue(content.contains("Salasana"));
    }
}
