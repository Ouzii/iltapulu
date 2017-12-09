package laajaosk.wepa.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Tietokanta entiteetti uutisen kirjoittajalle.
 * @author oce
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Writer extends AbstractPersistable<Long> {

    @ManyToMany(mappedBy = "writers")
    private List<News> news = new ArrayList<>();
    
    private String name;
    private String password;
    
    
}
