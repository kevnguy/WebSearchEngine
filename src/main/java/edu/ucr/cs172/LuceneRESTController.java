package edu.ucr.cs172;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;

@SpringBootApplication(/*exclude = MongoAutoConfiguration.class*/)
@RestController
public class LuceneRESTController {

	public static void main(String[] args) {
		SpringApplication.run(LuceneRESTController.class, args);
	}

	@GetMapping(value = "/api/search")
	public ResponseEntity<ArrayList> hello2(
			@RequestParam String q,
			@RequestParam(required = false) String title,
			@RequestParam(required = false) String or)throws Exception {
		String queryTitle = title;
		String boolQuery = or;
		String queryMain = q;
		ArrayList response = SearchDocs.searchDocuments(q);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/hello")
	public String hello(){
		return "Hello World fk lols top led" +
				"holysht it workd";
	}
}
