package laajaosk.wepa.repository;

import laajaosk.wepa.domain.Writer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio kirjoittajalle.
 * @author oce
 */
@Repository
public interface WriterRepository extends JpaRepository<Writer, Long>{

    /**
     * Hae kirjoittaja nimen perusteella.
     * @param name
     * @return
     */
    Writer findByName(String name);
}
