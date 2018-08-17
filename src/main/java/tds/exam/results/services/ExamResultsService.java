/*******************************************************************************
 * Copyright 2017 Regents of the University of California. Licensed under the Educational
 * Community License, Version 2.0 (the “license”); you may not use this file except in
 * compliance with the License. You may obtain a copy of the license at
 *
 * https://opensource.org/licenses/ECL-2.0
 *
 * Unless required under applicable law or agreed to in writing, software distributed under the
 * License is distributed in an “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for specific language governing permissions
 * and limitations under the license.
 *
 ******************************************************************************/

package tds.exam.results.services;

import tds.trt.model.TDSReport;

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
