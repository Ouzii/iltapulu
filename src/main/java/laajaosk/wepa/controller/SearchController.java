package laajaosk.wepa.controller;

import laajaosk.wepa.service.NewsService;
import laajaosk.wepa.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SearchController {
    
    @Autowired
    private SearchService searchService;
    @Autowired
    private NewsService newsService;

    @PostMapping("/search")
    public String searchFor(@RequestParam String searchWord, RedirectAttributes redirectAttribute) {
        redirectAttribute.addFlashAttribute("news", searchService.searchForNews(searchWord));
        return "redirect:/news/list/" + searchWord;
    }

    @GetMapping("/news/list/{searchWord}")
    public String makeViewForSearchHits(@PathVariable String searchWord, Model model) {
        model.addAttribute("title", searchWord);
        model = newsService.addFooterAndHeaderData(model);
        return "searchHits";
    }
}
