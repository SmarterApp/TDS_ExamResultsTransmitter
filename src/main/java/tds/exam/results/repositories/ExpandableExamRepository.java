package tds.exam.results.repositories;

import java.util.UUID;

import tds.exam.ExpandableExam;

/**
 * A repository for fetching an {@link tds.exam.ExpandableExam} from the exam service
 */
public interface ExpandableExamRepository {
    /**
     * Finds an {@link tds.exam.ExpandableExam} for the given examId
     *
     * @param examId The id of the exam to fetch
     * @return The fully populated {@link tds.exam.ExpandableExam}
     */
    ExpandableExam findExpandableExam(final UUID examId);
}
