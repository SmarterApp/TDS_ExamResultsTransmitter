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

package tds.exam.results.repositories;

import com.google.common.base.Optional;

import java.util.UUID;

import tds.common.ValidationError;
import tds.exam.ExpandableExam;

/**
 * A repository for fetching an {@link tds.exam.ExpandableExam} from the exam service
 */
public interface ExamRepository {
    /**
     * Finds an {@link tds.exam.ExpandableExam} for the given examId
     *
     * @param examId The id of the exam to fetch
     * @return The fully populated {@link tds.exam.ExpandableExam}
     */
    ExpandableExam findExpandableExam(final UUID examId);

    /**
     * Creates a request to update the status of an exam
     *
     * @param examId the id of the {@link tds.exam.Exam}
     * @param status the status to update the exam to
     */
    void updateStatus(final UUID examId, final String status);
}
