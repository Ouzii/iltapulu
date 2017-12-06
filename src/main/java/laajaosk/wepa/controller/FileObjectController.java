package laajaosk.wepa.controller;

import laajaosk.wepa.repository.FileObjectRepository;
import laajaosk.wepa.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Transactional
public class FileObjectController {

    @Autowired
    private FileObjectRepository fileRepository;
    @Autowired
    private NewsRepository newsRepository;
    
    @GetMapping(path = "/images/{id}", produces = "image/jpeg")
    @ResponseBody
    public byte[] jpegContent(@PathVariable Long id) {
        return fileRepository.findByNews(newsRepository.getOne(id)).getContent();

    }
}
