package laajaosk.wepa.repository;

import java.util.Date;
import java.util.List;
import laajaosk.wepa.domain.News;
import laajaosk.wepa.domain.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repositorio uutisten katselukerroille.
 * @author oce
 */
@Repository
public interface ViewRepository extends JpaRepository<View, Long> {

    /**
     * Hae katselukerrat, jotka liittyvät tiettyyn uutiseen tietyn ajanhetken jäĺkeen.
     * @param aNew
     * @param dateTime
     * @return
     */
    List<View> findByANewAndDateTimeAfter(News aNew, Date dateTime);
}
