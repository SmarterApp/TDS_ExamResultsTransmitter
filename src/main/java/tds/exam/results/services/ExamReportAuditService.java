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

import java.util.UUID;

import tds.exam.results.model.ExamReportStatus;
import tds.trt.model.TDSReport;

/**
 * A service for exam report auditing
 */
public interface ExamReportAuditService {
    /**
     * Saves the {@link tds.exam.results.trt.TDSReport} TRT.
     *
     * @param examId The exam id of the TRT
     * @param report The {@link tds.exam.results.trt.TDSReport} TRT jaxb object
     * @param status the {@link tds.exam.results.model.ExamReportStatus} of the report
     */
    void insertExamReport(final UUID examId, final TDSReport report, final ExamReportStatus status);

    /**
     * Update the exam report status
     *
     * @param examId       exam id for the report status
     * @param statusUpdate the {@link tds.exam.results.model.ExamReportStatus}
     */
    void updateExamReportStatus(final UUID examId, final ExamReportStatus statusUpdate);
}
