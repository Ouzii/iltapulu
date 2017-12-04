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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
    private NewsRepository newsRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FileObjectRepository fileRepository;
    @Autowired
    private WriterRepository writerRepository;

    @GetMapping("/")
    @Transactional
    public String index(Model model) {
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "published");
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("news", newsRepository.findAll(pageable));
        model.addAttribute("newsByDate", newsRepository.findAll(pageable));
        Pageable pageable2 = PageRequest.of(0, 5, Sort.Direction.DESC, "views");
        model.addAttribute("newsByViews", newsRepository.findAll(pageable2));
        return "index";
    }

    @GetMapping("/news/{id}")
    @Transactional
    public String show(Model model, @PathVariable Long id) {
        News aNew = newsRepository.getOne(id);
        aNew.setViews(aNew.getViews() + 1);
        newsRepository.save(aNew);
        model.addAttribute("aNew", aNew);
        model.addAttribute("categories", categoryRepository.findAll());
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "published");
        model.addAttribute("newsByDate", newsRepository.findAll(pageable));
        Pageable pageable2 = PageRequest.of(0, 5, Sort.Direction.DESC, "views");
        model.addAttribute("newsByViews", newsRepository.findAll(pageable2));
        return "article";
    }

    @GetMapping("/news")
    @Transactional
    public String list(Model model) {
        model.addAttribute("news", newsRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("title", "uutiset");
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "published");
        model.addAttribute("newsByDate", newsRepository.findAll(pageable));
        Pageable pageable2 = PageRequest.of(0, 5, Sort.Direction.DESC, "views");
        model.addAttribute("newsByViews", newsRepository.findAll(pageable2));
        return "news";
    }

    @GetMapping("/news/categories/{name}")
    @Transactional
    public String listByCategory(Model model, @PathVariable String name) {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(categoryRepository.findByName(name));
        model.addAttribute("news", newsRepository.findByCategories(categories));
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("title", name);
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "published");
        model.addAttribute("newsByDate", newsRepository.findAll(pageable));
        Pageable pageable2 = PageRequest.of(0, 5, Sort.Direction.DESC, "views");
        model.addAttribute("newsByViews", newsRepository.findAll(pageable2));
        return "news";
    }

    @GetMapping("/news/{title}/listByDate")
    @Transactional
    public String listByDate(Model model, @PathVariable String title) {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(categoryRepository.findByName(title.toLowerCase()));
        model.addAttribute("news", newsRepository.findByCategories(categories, new Sort(Sort.Direction.DESC, "published")));
        model.addAttribute("categories", categoryRepository.findAll());
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "published");
        model.addAttribute("newsByDate", newsRepository.findAll(pageable));
        Pageable pageable2 = PageRequest.of(0, 5, Sort.Direction.DESC, "views");
        model.addAttribute("newsByViews", newsRepository.findAll(pageable2));
        return "news";
    }

    @GetMapping("/news/{title}/listByViews")
    @Transactional
    public String listByViews(Model model, @PathVariable String title) {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(categoryRepository.findByName(title.toLowerCase()));
        model.addAttribute("news", newsRepository.findByCategories(categories, new Sort(Sort.Direction.DESC, "views")));
        model.addAttribute("categories", categoryRepository.findAll());
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "published");
        model.addAttribute("newsByDate", newsRepository.findAll(pageable));
        Pageable pageable2 = PageRequest.of(0, 5, Sort.Direction.DESC, "views");
        model.addAttribute("newsByViews", newsRepository.findAll(pageable2));
        return "news";
    }

    @PostMapping("/moderator/news")
    @Transactional
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

    @GetMapping(path = "/images/{id}", produces = "image/jpeg")
    @Transactional
    @ResponseBody
    public byte[] jpegContent(@PathVariable Long id) {
        return fileRepository.findByNews(newsRepository.getOne(id)).getContent();

    }
//    @GetMapping("/images/{id}")
//    @Transactional
//    public ResponseEntity<byte[]> jpegContent(@PathVariable Long id) {
//        try {
//            News aNew = newsRepository.getOne(id);
//            FileObject img = fileRepository.findByNews(aNew);
//            final HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.parseMediaType(img.getContentType()));
//            headers.setContentLength(img.getContentLength());
//            headers.add("Content-Disposition", "attachment; filename=" + img.getName());
//            
//            return new ResponseEntity<>(img.getContent(), headers, HttpStatus.CREATED);
//        } catch (Exception e) {
//            return null;
//        }
//    }

    @DeleteMapping("/news/{id}")
    @Transactional
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
