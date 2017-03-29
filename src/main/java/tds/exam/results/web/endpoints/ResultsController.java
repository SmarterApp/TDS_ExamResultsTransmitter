package tds.exam.results.web.endpoints;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ResultsController {
    @GetMapping(value = "/{examId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String findExamResults(@PathVariable final UUID examId) {
        return "test";
    }
}
