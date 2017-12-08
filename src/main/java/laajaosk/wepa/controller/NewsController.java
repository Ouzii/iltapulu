package laajaosk.wepa.controller;

import laajaosk.wepa.domain.News;
import laajaosk.wepa.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Transactional
public class NewsController {
    
    @Autowired
    private NewsService newsService;

    @GetMapping("/")
    public String index(Model model) {
        newsService.initiateTestData();
        model = newsService.makeIndexModel(model);
        return "index";
    }


    @GetMapping("/news/{id}")
    public String show(Model model, @PathVariable Long id) {
        News aNew = newsService.addViewForaNew(id);
        model.addAttribute("aNew", aNew);
        model = newsService.addFooterAndHeaderData(model);
        return "article";
    }

    @GetMapping("/news/{name}/list/{index}/{listedBy}")
    public String listByCategory(Model model, @PathVariable String name, @PathVariable int index, @PathVariable String listedBy) {

        if (name.equals("Uutiset")) {
            model = newsService.addAll(model, index, listedBy);
        } else {
            model = newsService.addFromCategory(model, name, index, listedBy);
        }
        model.addAttribute("title", name);
        model.addAttribute("index", index);
        model.addAttribute("listedBy", listedBy);
        model = newsService.findCategoryListSize(model, name);
        model = newsService.addFooterAndHeaderData(model);
        return "news";
    }  
}