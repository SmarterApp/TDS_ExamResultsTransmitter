package tds.exam.results.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import tds.assessment.Assessment;
import tds.assessment.AssessmentWindow;
import tds.exam.Exam;
import tds.exam.ExpandableExam;
import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.mappers.CommentMapper;
import tds.exam.results.mappers.ExamineeMapper;
import tds.exam.results.mappers.OpportunityMapper;
import tds.exam.results.mappers.TestMapper;
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
    private final ExamResultsTransmitterServiceProperties properties;

    @Autowired
    public ExamResultsServiceImpl(final ExamService examService,
                                  final SessionService sessionService,
                                  final AssessmentService assessmentService,
                                  final TDSReportValidator tdsReportValidator,
                                  final ExamReportAuditService examReportAuditService,
                                  final TestIntegrationSystemService testIntegrationSystemService,
                                  final ExamResultsTransmitterServiceProperties properties) {
        this.properties = properties;
        this.examService = examService;
        this.sessionService = sessionService;
        this.assessmentService = assessmentService;
        this.tdsReportValidator = tdsReportValidator;
        this.examReportAuditService = examReportAuditService;
        this.testIntegrationSystemService = testIntegrationSystemService;
    }

    @Override
    public TDSReport findAndSendExamResults(final UUID examId) {
        TDSReport report = new TDSReport();

        if (properties.isRetryOnError()) {
            findAndSendExamResults(examId, report);
        } else {
            try {
                findAndSendExamResults(examId, report);
            } catch (Exception e) {
                // Log error, and do not rethrow to prevent ERT from re-processing this same request
                log.error("Error occurred while processing or sending the exam results for examId {}: {}", examId,
                    e.getMessage());
            }
        }

        return report;
    }

    private void findAndSendExamResults(final UUID examId, final TDSReport report) {
        //TODO: Look into making these service calls asynchronously
        final ExpandableExam expandableExam = examService.findExpandableExam(examId);
        final Exam exam = expandableExam.getExam();
        final Session session = sessionService.findSessionById(exam.getSessionId());
        final Assessment assessment = assessmentService.findAssessment(exam.getClientName(), exam.getAssessmentKey());
        final ExternalSessionConfiguration externs = sessionService.findExternalSessionConfigurationByClientName(exam.getClientName());
        final List<AssessmentWindow> assessmentWindows = assessmentService.findAssessmentWindows(exam.getClientName(), exam.getAssessmentId(),
            exam.getStudentId(), externs);


        report.setOpportunity(OpportunityMapper.mapOpportunity(expandableExam, session, assessment, assessmentWindows));
        report.setExaminee(ExamineeMapper.mapExaminee(expandableExam));
        report.setTest(TestMapper.mapTest(assessment));
        CommentMapper.mapComments(report.getComment(), expandableExam);

        tdsReportValidator.validateReport(report);
        testIntegrationSystemService.sendResults(examId, report);
        examReportAuditService.insertExamReport(examId, report);
    }

}