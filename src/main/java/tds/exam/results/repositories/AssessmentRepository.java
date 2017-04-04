package tds.exam.results.repositories;

import tds.assessment.Assessment;

/**
 * Repository for interacting with the Assessment Service
 */
public interface AssessmentRepository {
    /**
     * Retrieves an {@link tds.assessment.Assessment} from the assessment service by the assessment key
     *
     * @param clientName the current envrionment's client name
     * @param key        the key of the {@link tds.assessment.Assessment}
     * @return the fully populated {@link tds.assessment.Assessment}\
     */
    Assessment findAssessment(final String clientName, final String key);
}
