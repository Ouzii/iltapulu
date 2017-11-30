package laajaosk.wepa.repository;

import laajaosk.wepa.domain.FileObject;
import laajaosk.wepa.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileObjectRepository extends JpaRepository<FileObject, Long> {
    FileObject findByNews(News news);
}
