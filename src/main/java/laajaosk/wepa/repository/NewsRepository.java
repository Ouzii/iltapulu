package laajaosk.wepa.repository;

import laajaosk.wepa.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long>  {
    
}
