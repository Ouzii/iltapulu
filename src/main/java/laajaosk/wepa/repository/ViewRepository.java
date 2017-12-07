package laajaosk.wepa.repository;

import java.util.Date;
import java.util.List;
import laajaosk.wepa.domain.News;
import laajaosk.wepa.domain.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewRepository extends JpaRepository<View, Long> {

    List<View> findByANewAndDateTimeAfter(News aNew, Date dateTime);
    
    View findByANew(News aNew);

    @Query("select v from View v where v.aNew = ?1 and dateTime > ?2")
    List<View> findByANew(News aNew, Date dateTime);
}
