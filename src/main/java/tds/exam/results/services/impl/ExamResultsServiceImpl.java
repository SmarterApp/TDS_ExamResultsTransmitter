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

package tds.exam.results.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import tds.assessment.Assessment;
import tds.assessment.AssessmentWindow;
import tds.exam.Exam;
import tds.exam.ExpandableExam;
import tds.exam.results.mappers.CommentMapper;
import tds.exam.results.mappers.ExamineeMapper;
import tds.exam.results.mappers.OpportunityMapper;
import tds.exam.results.mappers.TestMapper;
import tds.exam.results.model.ExamReportStatus;
import tds.exam.results.services.AssessmentService;
import tds.exam.results.services.ExamReportAuditService;
import tds.exam.results.services.ExamResultsService;
import tds.exam.results.services.ExamService;
import tds.exam.results.services.SessionService;
import tds.exam.results.services.TestIntegrationSystemService;
import tds.exam.results.trt.TDSReport;
import tds.exam.results.validation.TDSReportValidator;
import tds.session.ExternalSessionConfiguration;
import tds.session.Session;

@Service
public class ExamResultsServiceImpl implements ExamResultsService {
    private static final Logger log = LoggerFactory.getLogger(ExamResultsServiceImpl.class);
    private final ExamService examService;
    private final SessionService sessionService;
    private final AssessmentService assessmentService;
    private final TDSReportValidator tdsReportValidator;
    private final ExamReportAuditService examReportAuditService;
    private final TestIntegrationSystemService testIntegrationSystemService;

    @Autowired
    public ExamResultsServiceImpl(final ExamService examService,
                                  final SessionService sessionService,
                                  final AssessmentService assessmentService,
                                  final TDSReportValidator tdsReportValidator,
                                  final ExamReportAuditService examReportAuditService,
                                  final TestIntegrationSystemService testIntegrationSystemService) {
        this.examService = examService;
        this.sessionService = sessionService;
        this.assessmentService = assessmentService;
        this.tdsReportValidator = tdsReportValidator;
        this.examReportAuditService = examReportAuditService;
        this.testIntegrationSystemService = testIntegrationSystemService;
    }

    @Override
    public TDSReport findAndSendExamResults(final UUID examId) {
        final TDSReport report = new TDSReport();
        findAndSendExamResults(examId, report);
        return report;
    }

    private void findAndSendExamResults(final UUID examId, final TDSReport report) {
        final ExpandableExam expandableExam = examService.findExpandableExam(examId);
        final Exam exam = expandableExam.getExam();
        final Session session = sessionService.findSessionById(exam.getSessionId());
        final Assessment assessment = assessmentService.findAssessment(exam.getClientName(), exam.getAssessmentKey());
        final ExternalSessionConfiguration externs = sessionService.findExternalSessionConfigurationByClientName(exam.getClientName());
        final boolean guestStudent = exam.getStudentId() < 0;
        final List<AssessmentWindow> assessmentWindows = assessmentService.findAssessmentWindows(exam.getClientName(),
            exam.getAssessmentId(), guestStudent, externs);

        report.setOpportunity(OpportunityMapper.mapOpportunity(expandableExam, session, assessment, assessmentWindows));
        report.setExaminee(ExamineeMapper.mapExaminee(expandableExam));
        report.setTest(TestMapper.mapTest(assessment));
        CommentMapper.mapComments(report.getComment(), expandableExam);

        tdsReportValidator.validateReport(report);
        examReportAuditService.insertExamReport(examId, report, ExamReportStatus.RECEIVED);
        testIntegrationSystemService.sendResults(examId, report);
        examReportAuditService.insertExamReport(examId, report, ExamReportStatus.SENT);
    }

}