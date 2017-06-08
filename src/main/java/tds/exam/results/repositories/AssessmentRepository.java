package tds.exam.results.repositories;

import java.util.List;

import tds.assessment.Assessment;
import tds.assessment.AssessmentWindow;
import tds.session.ExternalSessionConfiguration;

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

    /**
     * Finds the assessment windows for an exam
     *
     * @param clientName    environment's client name
     * @param assessmentId  the assessment id for the assessment
     * @param guestStudent  flag indicating whether the windows should be retrieved for a guest student
     * @param configuration {@link tds.session.ExternalSessionConfiguration} for the environment
     * @return array of {@link tds.assessment.AssessmentWindow}
     */
    List<AssessmentWindow> findAssessmentWindows(final String clientName, final String assessmentId,
                                                 final boolean guestStudent, final ExternalSessionConfiguration configuration);

}
