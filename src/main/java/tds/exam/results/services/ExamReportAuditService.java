package tds.exam.results.services;

import javax.xml.bind.JAXBException;
import java.util.UUID;

import tds.exam.results.trt.TDSReport;

/**
 * A service for exam report auditing
 */
public interface ExamReportAuditService {
    /**
     * Saves the {@link tds.exam.results.trt.TDSReport} TRT.
     *
     * @param examId The exam id of the TRT
     * @param report The {@link tds.exam.results.trt.TDSReport} TRT jaxb object
     * @throws JAXBException
     */
    void insertExamReport(final UUID examId, final TDSReport report);
}
