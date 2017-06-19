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
