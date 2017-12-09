package laajaosk.wepa.repository;

import laajaosk.wepa.domain.Image;
import laajaosk.wepa.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repositorio kuvalle.
 * @author oce
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    /**
     * Hae kuva siihen liittyvän uutisen avulla.
     * @param news
     * @return
     */
    @Transactional
    Image findByNews(News news);
}
