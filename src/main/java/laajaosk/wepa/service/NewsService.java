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

/**
 * Palvelu uutisten näyttämisessä käytettävälle logiikalle.
 * @author oce
 */
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

    /**
     * Lisää kaikki olemassaolevat uutiset modelille. Tarkastaa minkä kentän mukaan uutiset järjestetään.
     * @param model
     * @param index
     * @param listedBy
     * @return
     */
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

    /**
     * Lisätään modelille kaikki haluttuun kategoriaan kuuluvat uutiset. Tarkastetaan myös minkä perusteella
     * uutiset järjestetään.
     * @param model
     * @param category
     * @param index
     * @param listedBy
     * @return
     */
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

    /**
     * Haetaan uutisen "ViewsLastWeek" -attribuutille arvo tietokannasta. Toistetaan kaikille halutuille uutisille.
     */
    private void setViewsForLastWeek(List<News> news) {
        for (News aNew : news) {
            aNew.setViewsLastWeek(viewRepository.findByANewAndDateTimeAfter(aNew, new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7))).size());
        }
    }

    /**
     * Lisätään headerissa ja footerissa käytettävä data, esim. kaikki kategoriat ja eri lailla listatut 5 uutista.
     * @param model
     * @return
     */
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

    /**
     * Hae tiettyyn kategoriaan liittyvien uutisten määrä. Käytetään sivuttamisessa.
     * @param model
     * @param categoryName
     * @return
     */
    public Model findCategoryListSize(Model model, String categoryName) {
        if (categoryName.equals("Uutiset")) {
            model.addAttribute("newsCount", newsRepository.findAll().size());
        } else {
            ArrayList<Category> categories = new ArrayList<>();
            categories.add(categoryRepository.findByName(categoryName));
            List<News> news = newsRepository.findByCategories(categories);
            model.addAttribute("newsCount", news.size());
        }
        return model;
    }

    /**
     * Luo etusivun model, eli lisää 5 tuoreinta uutista modelille.
     * @param model
     * @return
     */
    public Model makeIndexModel(Model model) {
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "published");
        model.addAttribute("news", newsRepository.findAll(pageable));
        model = addFooterAndHeaderData(model);
        return model;
    }

    /**
     * Lisää halutulle uutiselle uusi katselukerta.
     * @param id
     * @return
     */
    public News addViewForaNew(Long id) {
        News aNew = newsRepository.getOne(id);
        View view = new View(aNew, new Date());
        viewRepository.save(view);
        aNew.getViews().add(view);
        newsRepository.save(aNew);
        return aNew;
    }
}
