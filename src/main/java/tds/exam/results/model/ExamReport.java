/* *****************************************************************************
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
package tds.exam.results.model;

import java.util.UUID;

/**
 * Represents an Exam Report
 */
public class ExamReport {
    private final String reportXml;
    private final ExamReportStatus status;
    private final UUID examId;

    public ExamReport(final String reportXml, final ExamReportStatus status, final UUID examId) {
        this.reportXml = reportXml;
        this.status = status;
        this.examId = examId;
    }

    /**
     * @return the report XML representing the string version of a {@link tds.exam.results.trt.TDSReport}
     */
    public String getReportXml() {
        return reportXml;
    }

    /**
     * @return the current {@link ExamReportStatus}
     */
    public ExamReportStatus getStatus() {
        return status;
    }


    /**
     * @return the associated exam id
     */
    public UUID getExamId() {
        return examId;
    }
}
