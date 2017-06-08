package tds.exam.results.services;

import tds.exam.results.trt.TDSReport;

import java.util.UUID;

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
     */
    TDSReport findAndSendExamResults(final UUID examId);
}
