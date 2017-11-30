package laajaosk.wepa.controller;

import java.io.IOException;
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
import org.springframework.ui.Model;
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
    public String index(Model model) {
        model.addAttribute("writers", writerRepository.findAll());
        return "index";
    }

    @GetMapping("/news")
    public String list(Model model) {
        model.addAttribute("news", newsRepository.findAll());
        return "news";
    }

    @PostMapping("/")
    public String add(@RequestParam String title, @RequestParam String ingress, @RequestParam String text, @RequestParam String category, @RequestParam("img") MultipartFile file, @RequestParam List<Long> writers) throws IOException {
        News news = new News();
        news.setTitle(title);
        news.setIngress(ingress);
        news.setText(text);
        Category category2 = new Category();
        category2.setName(category);

        for (Long writer : writers) {
            news.getWriters().add(writerRepository.getOne(writer));
            writerRepository.getOne(writer).getNews().add(news);
        }
        news.getCategories().add(category2);
        categoryRepository.save(category2);
        newsRepository.save(news);
        FileObject fo = new FileObject();

        fo.setName(file.getOriginalFilename());
        fo.setContent(file.getBytes());
        fo.setContentLength(file.getSize());
        fo.setContentType(file.getContentType());
        fo.setNews(news);
        
        fileRepository.save(fo);

        return "redirect:/";
    }
    
    @GetMapping(path = "/news/{id}/img", produces = "image/jpeg")
    @ResponseBody
    public byte[] jpegContent(@PathVariable Long id) {
        return fileRepository.findByNews(newsRepository.getOne(id)).getContent();
    }
}
