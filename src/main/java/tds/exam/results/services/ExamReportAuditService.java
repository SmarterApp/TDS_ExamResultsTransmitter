package tds.exam.results.services;

import tds.exam.results.trt.TDSReport;

import java.util.UUID;

/**
 * A service for exam report auditing
 */
public interface ExamReportAuditService {
    /**
     * Saves the {@link tds.exam.results.trt.TDSReport} TRT.
     *
     * @param examId The exam id of the TRT
     * @param report The {@link tds.exam.results.trt.TDSReport} TRT jaxb object
     */
    void insertExamReport(final UUID examId, final TDSReport report);
}
