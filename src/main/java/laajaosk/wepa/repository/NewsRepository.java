package laajaosk.wepa.repository;

import java.util.List;
import laajaosk.wepa.domain.Category;
import laajaosk.wepa.domain.News;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>  {
    List<News> findByCategories(List<Category> categories);
    List<News> findByCategories(List<Category> categories, Sort sort);
    List<News> findByCategories(List<Category> categories, Pageable pageable);
}
