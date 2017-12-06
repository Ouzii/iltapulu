package laajaosk.wepa.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import laajaosk.wepa.domain.Category;
import laajaosk.wepa.domain.News;
import laajaosk.wepa.repository.CategoryRepository;
import laajaosk.wepa.repository.FileObjectRepository;
import laajaosk.wepa.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Transactional
public class NewsController {

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FileObjectRepository fileRepository;

    @GetMapping("/")
    public String index(Model model) {
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "published");
        model.addAttribute("news", newsRepository.findAll(pageable));
        model = addFooterAndHeaderData(model);
        return "index";
    }

    @GetMapping("/news/{id}")
    public String show(Model model, @PathVariable Long id) {
        News aNew = newsRepository.getOne(id);
        aNew.setViews(aNew.getViews() + 1);
        newsRepository.save(aNew);
        model.addAttribute("aNew", aNew);
        model = addFooterAndHeaderData(model);
        return "article";
    }

    @GetMapping("/news")
    public String list(Model model) {
        model.addAttribute("news", newsRepository.findAll());
        model.addAttribute("title", "uutiset");
        model = addFooterAndHeaderData(model);
        return "news";
    }

    @GetMapping("/news/categories/{name}")
    public String listByCategory(Model model, @PathVariable String name) {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(categoryRepository.findByName(name));
        model.addAttribute("news", newsRepository.findByCategories(categories));
        model.addAttribute("title", name);
        model = addFooterAndHeaderData(model);
        return "news";
    }

    @GetMapping("/news/{title}/listByDate")
    public String listByDate(Model model, @PathVariable String title) {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(categoryRepository.findByName(title.toLowerCase()));
        model.addAttribute("news", newsRepository.findByCategories(categories, new Sort(Sort.Direction.DESC, "published")));
        model = addFooterAndHeaderData(model);
        return "news";
    }

    @GetMapping("/news/{title}/listByViews")
    public String listByViews(Model model, @PathVariable String title) {
        ArrayList<Category> categories = new ArrayList<>();
        
        categories.add(categoryRepository.findByName(title.toLowerCase()));
        model.addAttribute("news", newsRepository.findByCategoriesAndPublishedAfter(categories, new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)), new Sort(Sort.Direction.DESC, "views")));
        model = addFooterAndHeaderData(model);
        return "news";
    }

    private Model addFooterAndHeaderData(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "published");
        model.addAttribute("newsByDate", newsRepository.findAll(pageable));
        Pageable pageable2 = PageRequest.of(0, 5, Sort.Direction.DESC, "views");
        model.addAttribute("newsByViews", newsRepository.findAll(pageable2));
        return model;
    }

}
