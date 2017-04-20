package tds.exam.results.repositories;

import java.util.UUID;

/**
 * Repository for interacting with the exam
 */
public interface ExamReportAuditRepository {
    /**
     * Saves the exam report XML blob to the exam_report table
     *
     * @param examId        The id of the exam being reported
     * @param examReportXml The XML blob of the TRT report
     */
    void insertExamReport(final UUID examId, final String examReportXml);
}
