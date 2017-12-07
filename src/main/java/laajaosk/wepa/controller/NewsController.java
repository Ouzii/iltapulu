package laajaosk.wepa.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Transactional
public class NewsController {

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private WriterRepository writerRepository;
    @Autowired
    private ViewRepository viewRepository;

    @GetMapping("/")
    public String index(Model model) {
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

        model = makeIndexModel(model);
        return "index";
    }
    
    public Model makeIndexModel(Model model) {
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "published");
        model.addAttribute("news", newsRepository.findAll(pageable));
        model = addFooterAndHeaderData(model);
        return model;
    }

    @GetMapping("/news/{id}")
    public String show(Model model, @PathVariable Long id) {
        News aNew = newsRepository.getOne(id);
        View view = new View(aNew, new Date());
        viewRepository.save(view);
        aNew.getViews().add(view);
        newsRepository.save(aNew);
        model.addAttribute("aNew", aNew);
        model = addFooterAndHeaderData(model);
        return "article";
    }

//    @GetMapping("/news/list/{index}")
//    public String list(Model model, @PathVariable int index) {
//        List<News> news = newsRepository.findAll();
//        if (news.size() > 0) {
//            Pageable pageable = PageRequest.of(index - 1, 10);
//            model.addAttribute("news", newsRepository.findAll(pageable));
//        } else {
//            model.addAttribute("news", newsRepository.findAll());
//        }
//        model.addAttribute("title", "Uutiset");
//        model.addAttribute("index", index);
//        model.addAttribute("newsCount", news.size());
//        model = addFooterAndHeaderData(model);
//        return "news";
//    }
    @GetMapping("/news/{name}/list/{index}/{listedBy}")
    public String listByCategory(Model model, @PathVariable String name, @PathVariable int index, @PathVariable String listedBy) {

        if (name.equals("Uutiset")) {
            model = addAll(model, index, listedBy);
        } else {
            model = addFromCategory(model, name, index, listedBy);
        }
        model.addAttribute("title", name);
        model.addAttribute("index", index);
        model.addAttribute("listedBy", listedBy);
        model = findCategoryListSize(model, name);
        model = addFooterAndHeaderData(model);
        return "news";
    }
    

    private Model addAll(Model model, int index, String listedBy) {
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

    private Model addFromCategory(Model model, String category, int index, String listedBy) {
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

//    @GetMapping("/news/{name}/list/{index}/listByDate")
//    public String listByDate(Model model, @PathVariable String name, @PathVariable int index) {
//        ArrayList<Category> categories = new ArrayList<>();
//        categories.add(categoryRepository.findByName(name));
//        model.addAttribute("news", newsRepository.findByCategories(categories, new Sort(Sort.Direction.DESC, "published")));
//        model.addAttribute("index", index);
//        model.addAttribute("title", name);
//        model = findCategoryListSize(model, name);
//        model = addFooterAndHeaderData(model);
//        return "news";
//    }
//
//    @GetMapping("/news/{name}/list/{index}/listByViews")
//    public String listByViews(Model model, @PathVariable String name, @PathVariable int index) {
//        ArrayList<Category> categories = new ArrayList<>();
//
//        categories.add(categoryRepository.findByName(name));
//        model.addAttribute("news", newsRepository.findByCategoriesAndPublishedAfter(categories, new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)), new Sort(Sort.Direction.DESC, "views")));
//        model.addAttribute("index", index);
//        model.addAttribute("title", name);
//        model = findCategoryListSize(model, name);
//        model = addFooterAndHeaderData(model);
//        return "news";
//    }
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

    private Model findCategoryListSize(Model model, String categoryName) {
        if (categoryName.equals("Uutiset")) {
            model.addAttribute("newsCount", newsRepository.findAll().size());
        } else {
            ArrayList<Category> categories = new ArrayList<>();
            categories.add(categoryRepository.findByName(categoryName));
            model.addAttribute("newsCount", categories.size());
        }
        return model;
    }
    
}
