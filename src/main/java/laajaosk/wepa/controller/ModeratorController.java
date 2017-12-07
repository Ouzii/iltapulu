package laajaosk.wepa.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import laajaosk.wepa.domain.Category;
import laajaosk.wepa.domain.FileObject;
import laajaosk.wepa.domain.News;
import laajaosk.wepa.domain.View;
import laajaosk.wepa.domain.Writer;
import laajaosk.wepa.repository.CategoryRepository;
import laajaosk.wepa.repository.FileObjectRepository;
import laajaosk.wepa.repository.NewsRepository;
import laajaosk.wepa.repository.ViewRepository;
import laajaosk.wepa.repository.WriterRepository;
import laajaosk.wepa.validators.NewsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @Autowired
    private ViewRepository viewRepository;

    private NewsValidator newsValidator = new NewsValidator();

    @GetMapping("/moderator")
    public String list(Model model) {
        model.addAttribute("writers", writerRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "moderator";
    }

    @PostMapping("/moderator/writer")
    public String addWriter(@RequestParam String name) {
        if (writerRepository.findByName(name) == null) {
            Writer w = new Writer();
            w.setName(name);
            writerRepository.save(w);
        }

        return "redirect:/moderator";
    }

    @PostMapping("/moderator/category")
    public String addCategory(@RequestParam String name) {
        if (categoryRepository.findByName(name) == null) {
            Category c = new Category();
            c.setName(name.toLowerCase());
            categoryRepository.save(c);
        }

        return "redirect:/moderator";
    }

    @PostMapping("/moderator/news")
    @Transactional
    public String add(RedirectAttributes ra, Model model, @RequestParam String title, @RequestParam String ingress, @RequestParam String text, @RequestParam("img") MultipartFile img, @RequestParam(value = "writers", required = false) List<Long> writers, @RequestParam(value = "categories", required = false) List<Long> categories) throws IOException {
        List<String> errors = newsValidator.runValidations(title, ingress, text, categories, writers, img);
        if (!errors.isEmpty()) {
            ra.addFlashAttribute("messages", errors);
            ra.addFlashAttribute("writers", writerRepository.findAll());
            ra.addFlashAttribute("categories", categoryRepository.findAll());
            return "redirect:/moderator";
        }
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

        fo.setName(img.getOriginalFilename());
        fo.setContent(img.getBytes());
        fo.setContentLength(img.getSize());
        fo.setContentType(img.getContentType());

        fo.setNews(news);
        fileRepository.save(fo);

        List<String> messages = new ArrayList<>();
        messages.add("Uutinen " + title + " luotu!");
        ra.addFlashAttribute("messages", messages);
        return "redirect:/moderator";
    }

    @DeleteMapping("/news/{id}")
    public String delete(RedirectAttributes redirectAttribute, @PathVariable Long id) {
        News aNew = newsRepository.getOne(id);
        String title = aNew.getTitle();

        for (Category category : aNew.getCategories()) {
            Category category2 = categoryRepository.getOne(category.getId());
            List<News> n = category2.getNews();
            n.remove(aNew);
            category2.setNews(n);
            categoryRepository.save(category2);
        }
        for (Writer writer : aNew.getWriters()) {
            Writer writer2 = writerRepository.getOne(writer.getId());
            List<News> n = writer2.getNews();
            n.remove(aNew);
            writer2.setNews(n);
            writerRepository.save(writer2);
        }
        for (View view : aNew.getViews()) {
            viewRepository.delete(view);
        }
        fileRepository.delete(fileRepository.findByNews(aNew));
        newsRepository.delete(aNew);
        List<String> messages = new ArrayList<>();
        messages.add("\"" + title + "\" on poistettu onnistuneesti!");
        redirectAttribute.addFlashAttribute("messages", messages);
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
    public String postModify(RedirectAttributes redirectAttribute, @PathVariable Long id, @RequestParam String title, @RequestParam String ingress, @RequestParam String text, @RequestParam("img") MultipartFile img, @RequestParam(value = "writers", required = false) List<Long> writers, @RequestParam(value = "categories", required = false) List<Long> categories) throws IOException {
        List<String> errors = new ArrayList<>();
        if (!img.isEmpty()) {
            errors = newsValidator.runValidations(title, ingress, text, categories, writers, img);
        } else {
            errors = newsValidator.runValidationsNoImg(title, ingress, text, categories, writers);
        }
        if (!errors.isEmpty()) {
            redirectAttribute.addFlashAttribute("messages", errors);
            redirectAttribute.addFlashAttribute("writers", writerRepository.findAll());
            redirectAttribute.addFlashAttribute("categories", categoryRepository.findAll());
            return "redirect:/news/" + id;
        }
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

        if (img != null && !img.isEmpty()) {
            FileObject old = fileRepository.findByNews(aNew);
            FileObject fo = new FileObject();

            fo.setName(img.getOriginalFilename());
            fo.setContent(img.getBytes());
            fo.setContentLength(img.getSize());
            fo.setContentType(img.getContentType());

            fo.setNews(aNew);
            fileRepository.save(fo);
            fileRepository.delete(old);
        }

        List<String> messages = new ArrayList<>();
        messages.add("Muokkaus onnistui!");
        redirectAttribute.addFlashAttribute("messages", messages);

        return "redirect:/news/" + id;
    }
}
