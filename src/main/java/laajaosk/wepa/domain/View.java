package laajaosk.wepa.domain;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class View extends AbstractPersistable<Long> {
    
    @ManyToOne
    private News aNew;
    private Date dateTime = new Date();
}
