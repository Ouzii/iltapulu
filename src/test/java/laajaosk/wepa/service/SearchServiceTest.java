package laajaosk.wepa.service;

import java.util.List;
import javax.transaction.Transactional;
import laajaosk.wepa.domain.News;
import laajaosk.wepa.repository.NewsRepository;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class SearchServiceTest {
    
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private SearchService searchService;
    
    @Test
    public void testSearchForNews() {
        
        News aNew = new News();
        aNew.setTitle("TestA");
        aNew.setText("TestB");
        aNew.setIngress("TestC");
        
        newsRepository.save(aNew);
        
        List<News> news = searchService.searchForNews("A");
        assertEquals(1, news.size());
        List<News> news2 = searchService.searchForNews("B");
        assertEquals(1, news2.size());
        List<News> news3 = searchService.searchForNews("c");
        assertEquals(1, news3.size());
        List<News> newsEmpty = searchService.searchForNews("Ö");
        assertEquals(0, newsEmpty.size());
    }
}
