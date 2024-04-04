package drugqrreoport.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QrCodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(QrCodeApplication.class, args);
		System.out.println("============MMI-QR-CODE-IS-RUNNING===============");
	}

}
