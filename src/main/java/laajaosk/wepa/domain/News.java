package laajaosk.wepa.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Tietokanta entiteetti uutiselle.
 * @author oce
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class News extends AbstractPersistable<Long> {

    
    private String title;
    @OneToMany(mappedBy = "aNew")
    private List<View> views = new ArrayList<>();
    private int viewsLastWeek = 0;
    
    @ManyToMany
    private List<Category> categories = new ArrayList<>();
    private String text;
    private String ingress;
    private String published = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2)).toString();
    
    @ManyToMany
    private List<Writer> writers = new ArrayList<>();
}
