package laajaosk.wepa.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import laajaosk.wepa.validator.NewsValidator;
import laajaosk.wepa.domain.*;
import laajaosk.wepa.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class ModeratorService {

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

    private final NewsValidator newsValidator = new NewsValidator();

    public List<String> addToMessages(String message, List<String> messages) {
        messages.add(message);
        return messages;
    }

    public List<String> ModifyNews(Long id, String title, String ingress, String text, List<Long> writers, List<Long> categories, MultipartFile img) throws IOException {
        List<String> errors = new ArrayList<>();
        if (!img.isEmpty()) {
            errors = newsValidator.runValidations(title, ingress, text, categories, writers, img);
        } else {
            errors = newsValidator.runValidationsNoImg(title, ingress, text, categories, writers);
        }
        if (!errors.isEmpty()) {
            return errors;
        }

        News aNew = doModificationsToNews(id, title, ingress, text, writers, categories);

        if (img != null && !img.isEmpty()) {
            doModificationsToImg(aNew, img);
        }

        return errors;
    }

    private void doModificationsToImg(News aNew, MultipartFile img) throws IOException {
        FileObject old = fileRepository.findByNews(aNew);
        createFileObjectOutOfImg(aNew, img);
        fileRepository.delete(old);
    }

    private void createFileObjectOutOfImg(News aNew, MultipartFile img) throws IOException {
        FileObject fo = new FileObject();

        fo.setName(img.getOriginalFilename());
        fo.setContent(img.getBytes());
        fo.setContentLength(img.getSize());
        fo.setContentType(img.getContentType());

        fo.setNews(aNew);
        fileRepository.save(fo);
    }

    private News doModificationsToNews(Long id, String title, String ingress, String text, List<Long> writers, List<Long> categories) {
        News aNew = newsRepository.getOne(id);
        setNewsAttributes(aNew, title, ingress, text);        
        addToWritersAndCategories(aNew, writers, categories);
        newsRepository.save(aNew);
        return aNew;
    }
    
    private News setNewsAttributes(News aNew, String title, String ingress, String text) {
        aNew.setTitle(title);
        aNew.setIngress(ingress);
        aNew.setText(text);
        aNew.setWriters(new ArrayList<>());
        aNew.setCategories(new ArrayList<>());
        return aNew;
    }

    private void addToWritersAndCategories(News aNew, List<Long> writers, List<Long> categories) {
        for (Long writer : writers) {
            aNew.getWriters().add(writerRepository.getOne(writer));
            writerRepository.getOne(writer).getNews().add(aNew);
        }
        for (Long category : categories) {
            aNew.getCategories().add(categoryRepository.getOne(category));
            categoryRepository.getOne(category).getNews().add(aNew);
        }
    }

    public List<String> addNews(String title, String ingress, String text, List<Long> writers, List<Long> categories, MultipartFile img) throws IOException {
        List<String> errors = newsValidator.runValidations(title, ingress, text, categories, writers, img);
        if (!errors.isEmpty()) {
            return errors;
        }
        News aNew = setNewsAttributes(new News(), title, ingress, text);
        addToWritersAndCategories(aNew, writers, categories);
        newsRepository.save(aNew);
        createFileObjectOutOfImg(aNew, img);

        return errors;
    }

    public String deleteNews(Long id) {
        News aNew = newsRepository.getOne(id);
        String title = aNew.getTitle();
        deleteCategoryRelationsFromNews(aNew);
        deleteWriterRelationsFromNews(aNew);
        for (View view : aNew.getViews()) {
            viewRepository.delete(view);
        }
        
        fileRepository.delete(fileRepository.findByNews(aNew));
        newsRepository.delete(aNew);
        return title;
    }
    
    private void deleteCategoryRelationsFromNews(News aNew) {
        for (Category category : aNew.getCategories()) {
            Category category2 = categoryRepository.getOne(category.getId());
            List<News> n = category2.getNews();
            n.remove(aNew);
            category2.setNews(n);
            categoryRepository.save(category2);
        }
    }
    
    private void deleteWriterRelationsFromNews(News aNew) {
        for (Writer writer : aNew.getWriters()) {
            Writer writer2 = writerRepository.getOne(writer.getId());
            List<News> n = writer2.getNews();
            n.remove(aNew);
            writer2.setNews(n);
            writerRepository.save(writer2);
        }
    }

    public RedirectAttributes addCategoriesAndWriters(RedirectAttributes redirectAttribute) {
        redirectAttribute.addFlashAttribute("writers", writerRepository.findAll());
        redirectAttribute.addFlashAttribute("categories", categoryRepository.findAll());
        return redirectAttribute;
    }

    public Model addCategoriesAndWriters(Model model) {
        model.addAttribute("writers", writerRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return model;
    }
    
    public Model addNewsToModel(Model model, Long id) {
        model.addAttribute("aNew", newsRepository.getOne(id));
        return model;
    }
}
