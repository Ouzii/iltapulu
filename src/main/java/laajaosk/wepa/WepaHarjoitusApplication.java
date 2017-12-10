package laajaosk.wepa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sovelluksen käynnistysluokka.
 * @author oce
 */
@SpringBootApplication
public class WepaHarjoitusApplication {

    /**
     * Päämetodi. Käynnistää sovelluksen.
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(WepaHarjoitusApplication.class, args);
    }
}
