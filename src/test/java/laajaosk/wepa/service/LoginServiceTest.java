/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laajaosk.wepa.service;

import javax.transaction.Transactional;
import laajaosk.wepa.RedirectAttributesMock;
import laajaosk.wepa.SessionMock;
import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.WriterRepository;
import static org.apache.maven.wagon.PathUtils.password;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author oce
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class LoginServiceTest {

    @Autowired
    private LoginService loginService;
    @Autowired
    private WriterRepository writerRepository;
    
    private SessionMock session;
    private RedirectAttributesMock ram;

    @Before
    public void setUp() {
        this.session = new SessionMock();
        this.session.setAttribute("user", new Writer());
        this.ram = new RedirectAttributesMock();
    }
    
    @Test
    public void testLogoutAndLogin() {
        assertNotNull(this.session.getAttribute("user"));
        loginService.logout(session, ram);
        assertNull(this.session.getAttribute("user"));
        String username = "user";
        String password = "pass";
        Writer user = new Writer(null, username, password);
        writerRepository.save(user);
        loginService.login(session, username, password, ram);
        assertNotNull(this.session.getAttribute("user"));
    }
}
