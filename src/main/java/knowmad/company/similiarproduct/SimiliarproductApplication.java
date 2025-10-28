package knowmad.company.similiarproduct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SimiliarproductApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimiliarproductApplication.class, args);
	}

}
