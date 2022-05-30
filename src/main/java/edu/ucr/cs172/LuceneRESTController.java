package edu.ucr.cs172;

import java.util.ArrayList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@SpringBootApplication()
@RestController
public class LuceneRESTController {

	public static void main(String[] args) {
		SpringApplication.run(LuceneRESTController.class, args);
	}

	@GetMapping(value = "/api/search")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<ArrayList> HandleGetRequest(
			@RequestParam String q,
			@RequestParam(required = false) String model){
		if(!q.isEmpty()) {
			try{
				ArrayList response = SearchDocs.searchDocuments(q,model);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			catch (Exception e){
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Query not provided or unable to parse query");
	}
}


