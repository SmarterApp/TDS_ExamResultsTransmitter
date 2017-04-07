package tds.exam.results.services;

import java.util.UUID;

import tds.exam.ExpandableExam;

/**
 * Service for interacting with a remote exam service
 */
public interface ExamService {
    /**
     * Fetches an {@link tds.exam.ExpandableExam} with the given exam id.
     *
     * @param examId The id of the exam to fetch
     * @return The fully-populated {@link tds.exam.ExpandableExam}
     */
    ExpandableExam findExpandableExam(final UUID examId);
}
