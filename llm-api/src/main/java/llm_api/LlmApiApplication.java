package llm_api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LlmApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LlmApiApplication.class, args);
	}

	@Bean
	CommandLineRunner loadDocuments(QdrantService qdrantService) {
		return args -> {

			qdrantService.insertDocument(
					"1",
					"Java Spring Boot backend developer",
					"Backend",
					"Bangalore"
			);

			qdrantService.insertDocument(
					"2",
					"Python Django backend engineer",
					"Backend",
					"Hyderabad"
			);

			qdrantService.insertDocument(
					"3",
					"React frontend developer",
					"Frontend",
					"Bangalore"
			);

			System.out.println("All documents inserted!");
		};
	}

}
