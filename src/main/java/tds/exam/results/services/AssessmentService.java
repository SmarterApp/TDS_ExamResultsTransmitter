package tds.exam.results.services;

import tds.assessment.Assessment;

/**
 * Service for interacting with a remote assessment service
 */
public interface AssessmentService {
    /**
     * Retrieves an {@link tds.assessment.Assessment} from the assessment service by the assessment key
     *
     * @param clientName the current envrionment's client name
     * @param key        the key of the {@link tds.assessment.Assessment}
     * @return the fully populated {@link tds.assessment.Assessment}\
     */
    Assessment findAssessment(final String clientName, final String key);
}