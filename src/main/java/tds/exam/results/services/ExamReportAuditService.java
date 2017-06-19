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
 * A service for exam report auditing
 */
public interface ExamReportAuditService {
    /**
     * Saves the {@link tds.exam.results.trt.TDSReport} TRT.
     *
     * @param examId The exam id of the TRT
     * @param report The {@link tds.exam.results.trt.TDSReport} TRT jaxb object
     */
    void insertExamReport(final UUID examId, final TDSReport report);
}
