package laajaosk.wepa.repository;

import laajaosk.wepa.domain.Writer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WriterRepository extends JpaRepository<Writer, Long>{
    Writer findByName(String name);
}
