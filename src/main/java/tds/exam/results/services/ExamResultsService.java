package tds.exam.results.services;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;

import tds.exam.results.services.impl.ExamResultsServiceImpl;
import tds.exam.results.trt.TDSReport;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by emunoz on 4/3/17.
 */
public interface ExamResultsService {
    TDSReport findExamResults(UUID examId) throws JAXBException, SAXException, IOException;
}
