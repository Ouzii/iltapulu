package laajaosk.wepa.service;

import java.util.ArrayList;
import java.util.List;
import laajaosk.wepa.domain.News;
import laajaosk.wepa.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    @Autowired
    private NewsRepository newsRepository;
    
    
    public List<News> searchForNews(String searchWord) {
        List<News> news = newsRepository.findByTitleContaining(searchWord);
        news.addAll(newsRepository.findByIngressContaining(searchWord));
        news.addAll(newsRepository.findByTextContaining(searchWord));
        return news;
    }
}
