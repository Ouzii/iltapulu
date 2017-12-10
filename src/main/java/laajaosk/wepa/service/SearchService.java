package laajaosk.wepa.service;

import java.util.ArrayList;
import java.util.List;
import laajaosk.wepa.domain.News;
import laajaosk.wepa.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Palvelu hakutoiminnon logiikalle.
 * @author oce
 */
@Service
public class SearchService {

    @Autowired
    private NewsRepository newsRepository;
    
    /**
     * Hakee uutiset tietokannasta annetulla hakusanalla.
     * @param searchWord
     * @return
     */
    public List<News> searchForNews(String searchWord) {
        List<News> news = newsRepository.findByTitleContainingIgnoreCase(searchWord);
        news.addAll(newsRepository.findByIngressContainingIgnoreCase(searchWord));
        news.addAll(newsRepository.findByTextContainingIgnoreCase(searchWord));
        return news;
    }
}
