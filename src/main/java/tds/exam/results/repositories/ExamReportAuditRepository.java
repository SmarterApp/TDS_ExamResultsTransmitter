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

package tds.exam.results.repositories;

import java.util.Optional;
import java.util.UUID;

import tds.exam.results.model.ExamReport;
import tds.exam.results.model.ExamReportStatus;

/**
 * Repository for interacting with the exam
 */
public interface ExamReportAuditRepository {
    /**
     * Saves the exam report XML blob to the exam_report table
     *
     * @param examId        The id of the exam being reported
     * @param examReportXml The XML blob of the TRT report
     * @param examReportStatus the status of the report
     */
    void insertExamReport(final UUID examId, final String examReportXml, ExamReportStatus examReportStatus);

    Optional<ExamReport> findLatestExamReport(final UUID examId);
}
