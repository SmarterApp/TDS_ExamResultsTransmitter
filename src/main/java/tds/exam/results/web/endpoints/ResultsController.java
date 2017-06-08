package tds.exam.results.web.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;

import tds.exam.results.trt.TDSReport;

import java.io.IOException;
import java.util.UUID;

import tds.exam.results.services.ExamResultsService;

@RestController
public class ResultsController {
    private final ExamResultsService examResultsService;

    @Autowired
    public ResultsController(final ExamResultsService examResultsService) {
        this.examResultsService = examResultsService;
    }

    @GetMapping(value = "/{examId}", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public TDSReport findExamResults(@PathVariable final UUID examId) throws JAXBException, IOException, SAXException {
        return examResultsService.findAndSendExamResults(examId);
    }
}
