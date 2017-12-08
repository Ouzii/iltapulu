package laajaosk.wepa.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import laajaosk.wepa.domain.Category;
import laajaosk.wepa.domain.News;
import laajaosk.wepa.domain.View;
import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.CategoryRepository;
import laajaosk.wepa.repository.NewsRepository;
import laajaosk.wepa.repository.ViewRepository;
import laajaosk.wepa.repository.WriterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ViewRepository viewRepository;
    @Autowired
    private WriterRepository writerRepository;

    public Model addAll(Model model, int index, String listedBy) {
        List<News> news = newsRepository.findAll();
        if (listedBy.equals("viewsLastWeek")) {
            setViewsForLastWeek(news);
        }
        if (news.size() > 0) {
            Pageable pageable = PageRequest.of(index - 1, 10, Sort.Direction.DESC, listedBy);
            model.addAttribute("news", newsRepository.findAll(pageable));
        } else {
            model.addAttribute("news", newsRepository.findAll());
        }
        return model;
    }

    public Model addFromCategory(Model model, String category, int index, String listedBy) {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(categoryRepository.findByName(category));
        List<News> news = newsRepository.findByCategories(categories);

        if (listedBy.equals("viewsLastWeek")) {
            setViewsForLastWeek(news);
        }
        if (news.size() > 0) {
            Pageable pageable = PageRequest.of(index - 1, 10, Sort.Direction.DESC, listedBy);
            model.addAttribute("news", newsRepository.findByCategories(categories, pageable));
        } else {
            model.addAttribute("news", newsRepository.findByCategories(categories));
        }
        return model;
    }

    private void setViewsForLastWeek(List<News> news) {
        for (News aNew : news) {
            aNew.setViewsLastWeek(viewRepository.findByANewAndDateTimeAfter(aNew, new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7))).size());
        }
    }

    public Model addFooterAndHeaderData(Model model) {
        List<News> news = newsRepository.findAll();
        model.addAttribute("categories", categoryRepository.findAll());
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "published");
        model.addAttribute("newsByDate", newsRepository.findAll(pageable));
        setViewsForLastWeek(news);
        Pageable pageable2 = PageRequest.of(0, 5, Sort.Direction.DESC, "viewsLastWeek");
        model.addAttribute("newsByViews", newsRepository.findAll(pageable2));
        return model;
    }

    public Model findCategoryListSize(Model model, String categoryName) {
        if (categoryName.equals("Uutiset")) {
            model.addAttribute("newsCount", newsRepository.findAll().size());
        } else {
            ArrayList<Category> categories = new ArrayList<>();
            categories.add(categoryRepository.findByName(categoryName));
            model.addAttribute("newsCount", categories.size());
        }
        return model;
    }

    public Model makeIndexModel(Model model) {
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "published");
        model.addAttribute("news", newsRepository.findAll(pageable));
        model = addFooterAndHeaderData(model);
        return model;
    }

    public News addViewForaNew(Long id) {
        News aNew = newsRepository.getOne(id);
        View view = new View(aNew, new Date());
        viewRepository.save(view);
        aNew.getViews().add(view);
        newsRepository.save(aNew);
        return aNew;
    }

    public void initiateTestData() {
        Category viihde = new Category();
        viihde.setName("Viihde");
        Category urheilu = new Category();
        urheilu.setName("Urheilu");
        Category uutiset = new Category();
        uutiset.setName("Uutiset");
        if (categoryRepository.findByName(viihde.getName()) == null && categoryRepository.findByName(urheilu.getName()) == null) {
            categoryRepository.save(viihde);
            categoryRepository.save(urheilu);
            categoryRepository.save(uutiset);
        }

        Writer pekka = new Writer();
        pekka.setName("Pekka");
        Writer jarkko = new Writer();
        jarkko.setName("Jarkko");
        if (writerRepository.findByName(pekka.getName()) == null && writerRepository.findByName(jarkko.getName()) == null) {
            writerRepository.save(pekka);
            writerRepository.save(jarkko);
        }
    }
}
