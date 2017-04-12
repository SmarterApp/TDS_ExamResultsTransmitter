package tds.exam.results.services;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;

import tds.exam.results.services.impl.ExamResultsServiceImpl;
import tds.exam.results.trt.TDSReport;

import java.io.IOException;
import java.util.UUID;

/**
 * A service responsible for fetching the necessary data and constructing the TDSReport TRT object
 */
public interface ExamResultsService {
    /**
     * Constructs the {@link tds.exam.results.trt.TDSReport} object with data fetched from Exam, Session, and Assessment
     * services.
     *
     * @param examId The id of the exam for the {@link tds.exam.results.trt.TDSReport}
     * @return The fully populated {@link tds.exam.results.trt.TDSReport} object
     * @throws JAXBException
     * @throws SAXException
     * @throws IOException
     */
    TDSReport findExamResults(final UUID examId);
}
