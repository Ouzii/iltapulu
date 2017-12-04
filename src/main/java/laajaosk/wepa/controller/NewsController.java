package laajaosk.wepa.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import laajaosk.wepa.domain.Category;
import laajaosk.wepa.domain.FileObject;
import laajaosk.wepa.domain.News;
import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.CategoryRepository;
import laajaosk.wepa.repository.FileObjectRepository;
import laajaosk.wepa.repository.NewsRepository;
import laajaosk.wepa.repository.WriterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class NewsController {

    @Autowired
    @Qualifier("newsRepository")
    private NewsRepository newsRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FileObjectRepository fileRepository;
    @Autowired
    private WriterRepository writerRepository;

    @GetMapping("/")
    public String index(Model model) {
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "published");
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("news", newsRepository.findAll(pageable));
        return "index";
    }

    @GetMapping("/news/{id}")
    public String show(Model model, @PathVariable Long id) {
        News aNew = newsRepository.getOne(id);
        aNew.setViews(aNew.getViews()+1);
        newsRepository.save(aNew);
        model.addAttribute("aNew", aNew);
        model.addAttribute("categories", categoryRepository.findAll());
        return "article";
    }

    @GetMapping("/news")
    public String list(Model model) {
        model.addAttribute("news", newsRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("title", "uutiset");
        return "news";
    }

    @GetMapping("/news/categories/{name}")
    public String listByCategory(Model model, @PathVariable String name) {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(categoryRepository.findByName(name));
        model.addAttribute("news", newsRepository.findByCategories(categories));
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("title", name);
        return "news";
    }

    @GetMapping("/news/{title}/listByDate")
    public String listByDate(Model model, @PathVariable String title) {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(categoryRepository.findByName(title.toLowerCase()));
        model.addAttribute("news", newsRepository.findByCategories(categories, new Sort(Sort.Direction.DESC, "published")));
        model.addAttribute("categories", categoryRepository.findAll());
        return "news";
    }

    @GetMapping("/news/{title}/listByViews")
    public String listByViews(Model model, @PathVariable String title) {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(categoryRepository.findByName(title.toLowerCase()));
        model.addAttribute("news", newsRepository.findByCategories(categories, new Sort(Sort.Direction.DESC, "views")));
        model.addAttribute("categories", categoryRepository.findAll());
        return "news";
    }

    @PostMapping("/moderator/news")
    public String add(@RequestParam String title, @RequestParam String ingress, @RequestParam String text, @RequestParam("img") MultipartFile file, @RequestParam List<Long> writers, @RequestParam List<Long> categories) throws IOException {
        News news = new News();
        news.setTitle(title);
        news.setIngress(ingress);
        news.setText(text);

        for (Long writer : writers) {
            news.getWriters().add(writerRepository.getOne(writer));
            writerRepository.getOne(writer).getNews().add(news);
        }
        for (Long category : categories) {
            news.getCategories().add(categoryRepository.getOne(category));
            categoryRepository.getOne(category).getNews().add(news);
        }

        newsRepository.save(news);
        FileObject fo = new FileObject();

        fo.setName(file.getOriginalFilename());
        fo.setContent(file.getBytes());
        fo.setContentLength(file.getSize());
        fo.setContentType(file.getContentType());
        fo.setNews(news);

        fileRepository.save(fo);

        return "redirect:/moderator";
    }

    @GetMapping(path = "/news/{id}/img", produces = "image/jpeg")
    @ResponseBody
    public ResponseEntity<byte[]> jpegContent(@PathVariable Long id) {
        try {
            News aNew = newsRepository.getOne(id);
            FileObject img = fileRepository.findByNews(aNew);
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(img.getContentType()));
            headers.setContentLength(img.getContentLength());
            headers.add("Content-Disposition", "attachment; filename=" + img.getName());
            
            return new ResponseEntity<>(img.getContent(), headers, HttpStatus.CREATED);
        } catch (Exception e) {
            return null;
        }
    }
    
    
    @DeleteMapping("/news/{id}")
    public String delete(@PathVariable Long id) {
        News aNew = newsRepository.getOne(id);
        
        for (Category category : aNew.getCategories()) {
            Category c = categoryRepository.getOne(category.getId());
            List<News> n = c.getNews();
            n.remove(aNew);
            c.setNews(n);
        }
        for (Writer writer : aNew.getWriters()) {
            Writer w = writerRepository.getOne(writer.getId());
            List<News> n = w.getNews();
            n.remove(aNew);
            w.setNews(n);
        }
        fileRepository.delete(fileRepository.findByNews(aNew));
        newsRepository.delete(aNew);
        return "redirect:/";
    }
}
