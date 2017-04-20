package tds.exam.results.services;

import javax.xml.bind.JAXBException;
import java.util.UUID;

import tds.exam.results.trt.TDSReport;

/**
 * Service for interacting with an instance of the Test Integration System
 */
public interface TestIntegrationSystemService {
    /**
     * Sends the exam TRT results to the Test Integration System
     *
     * @param examId The exam id of the TRT report
     * @param report The {@link tds.exam.results.trt.TDSReport} TRT jaxb object
     * @throws JAXBException if an error occurs while marshalling the report
     */
    void sendResults(final UUID examId, final TDSReport report);
}
