/*******************************************************************************
 * Copyright 2016 Smarter Balance Licensed under the
 *     Educational Community License, Version 2.0 (the "License"); you may
 *     not use this file except in compliance with the License. You may
 *     obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an "AS IS"
 *     BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *     or implied. See the License for the specific language governing
 *     permissions and limitations under the License.
 ******************************************************************************/

package tds.exam.results.services;

import java.util.List;

import tds.assessment.Assessment;
import tds.assessment.AssessmentWindow;
import tds.session.ExternalSessionConfiguration;

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
