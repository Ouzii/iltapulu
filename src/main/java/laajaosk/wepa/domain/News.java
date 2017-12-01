package laajaosk.wepa.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class News extends AbstractPersistable<Long> {
    
    private String title;
    
    @ManyToMany
    private List<Category> categories = new ArrayList<>();
    private String text;
    private String ingress;
    private Date published = new Date();
    
    @ManyToMany
    private List<Writer> writers = new ArrayList<>();
}
