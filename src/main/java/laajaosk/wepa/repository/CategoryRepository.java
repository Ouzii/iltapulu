package laajaosk.wepa.repository;

import laajaosk.wepa.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio kategorioille.
 * @author oce
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Hae kategoria nimen perusteella.
     * @param name
     * @return
     */
    Category findByName(String name);
}
