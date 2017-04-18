package tds.exam.results.services;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.UUID;

import tds.exam.results.trt.TDSReport;

/**
 * A service responsible for fetching the necessary data and constructing the TDSReport TRT object
 */
public interface ExamResultsService {
    /**
     * Constructs the {@link tds.exam.results.trt.TDSReport} object with data fetched from Exam, Session, and Assessment
     * services and sends it to TIS.
     *
     * @param examId The id of the exam for the {@link tds.exam.results.trt.TDSReport}
     * @return The fully populated {@link tds.exam.results.trt.TDSReport} object
     * @throws JAXBException
     * @throws SAXException
     * @throws IOException
     */
    TDSReport findAndSendExamResults(final UUID examId);
}
