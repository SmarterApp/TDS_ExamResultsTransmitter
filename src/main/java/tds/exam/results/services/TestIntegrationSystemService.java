package tds.exam.results.services;

import tds.exam.results.trt.TDSReport;

import java.util.UUID;

/**
 * Service for interacting with an instance of the Test Integration System
 */
public interface TestIntegrationSystemService {
    /**
     * Sends the exam TRT results to the Test Integration System
     *
     * @param examId The exam id of the TRT report
     * @param report The {@link tds.exam.results.trt.TDSReport} TRT jaxb object
     */
    void sendResults(final UUID examId, final TDSReport report);
}
