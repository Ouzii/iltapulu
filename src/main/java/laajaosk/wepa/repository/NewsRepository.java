package laajaosk.wepa.repository;

import java.util.Date;
import java.util.List;
import laajaosk.wepa.domain.Category;
import laajaosk.wepa.domain.News;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repositorio uutiselle.
 * @author oce
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * Hae uutisia, jotka liittyvät tiettyihin kategorioihin.
     * @param categories
     * @return
     */
    List<News> findByCategories(List<Category> categories);


    /**
     * Hae uutisia, jotka liittyvät tiettyihin kategorioihin ja liitä haluttu Pageable-olio.
     * @param categories
     * @param pageable
     * @return
     */
    List<News> findByCategories(List<Category> categories, Pageable pageable);
    
    /**
     * Hae uutinen otsikon perusteella.
     * @param title
     * @return
     */
    News findByTitle(String title);
    
    /**
     * Hakee uutiset, joiden otsikossa esiintyy hakusana.
     * @param searchWord
     * @return
     */
    List<News> findByTitleContainingIgnoreCase(String searchWord);

    /**
     * Hakee uutiset, joiden ingressissä esiintyy hakusana.
     * @param searchWord
     * @return
     */
    List<News> findByIngressContainingIgnoreCase(String searchWord);

    /**
     * Hakee uutiset, joiden leipätekstissä esiintyy hakusana.
     * @param searchWord
     * @return
     */
    List<News> findByTextContainingIgnoreCase(String searchWord);

}
