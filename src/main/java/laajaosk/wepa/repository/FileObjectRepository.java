package laajaosk.wepa.repository;

import laajaosk.wepa.domain.FileObject;
import laajaosk.wepa.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FileObjectRepository extends JpaRepository<FileObject, Long> {
    @Transactional
    FileObject findByNews(News news);
}
