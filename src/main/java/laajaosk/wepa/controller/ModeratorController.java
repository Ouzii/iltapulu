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
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@Transactional
public class ModeratorController {

    @Autowired
    private WriterRepository writerRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private FileObjectRepository fileRepository;

    @GetMapping("/moderator")
    public String list(Model model) {
        model.addAttribute("writers", writerRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "moderator";
    }

    @PostMapping("/moderator/writer")
    public String addWriter(@RequestParam String name) {
        Writer w = new Writer();
        w.setName(name);
        writerRepository.save(w);

        return "redirect:/moderator";
    }

    @PostMapping("/moderator/category")
    public String addCategory(@RequestParam String name) {
        Category c = new Category();
        c.setName(name.toLowerCase());
        categoryRepository.save(c);

        return "redirect:/moderator";
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

    @GetMapping("/news/{id}/modify")
    public String modify(Model model, @PathVariable Long id) {
        model.addAttribute("aNew", newsRepository.getOne(id));
        model.addAttribute("writers", writerRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "modify";
    }

    @PostMapping("/moderator/news/{id}")
    public String postModify(@PathVariable Long id, @RequestParam String title, @RequestParam String ingress, @RequestParam String text, @RequestParam("img") MultipartFile file, @RequestParam List<Long> writers, @RequestParam List<Long> categories) throws IOException {
        News aNew = newsRepository.getOne(id);
        aNew.setTitle(title);
        aNew.setIngress(ingress);
        aNew.setText(text);

        aNew.setWriters(new ArrayList<>());
        for (Long writer : writers) {
            aNew.getWriters().add(writerRepository.getOne(writer));
            writerRepository.getOne(writer).getNews().add(aNew);
        }
        aNew.setCategories(new ArrayList<>());
        for (Long category : categories) {
            aNew.getCategories().add(categoryRepository.getOne(category));
            categoryRepository.getOne(category).getNews().add(aNew);
        }
        newsRepository.save(aNew);

        if (file != null && !file.isEmpty()) {
            FileObject old = fileRepository.findByNews(aNew);
            FileObject fo = new FileObject();

            fo.setName(file.getOriginalFilename());
            fo.setContent(file.getBytes());
            fo.setContentLength(file.getSize());
            fo.setContentType(file.getContentType());

            fo.setNews(aNew);
            fileRepository.save(fo);
            fileRepository.delete(old);
        }

        return "redirect:/news/" + id;
    }
}
