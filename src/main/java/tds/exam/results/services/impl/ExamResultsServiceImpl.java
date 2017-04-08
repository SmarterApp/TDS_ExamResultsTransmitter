package tds.exam.results.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.UUID;

import tds.assessment.Assessment;
import tds.exam.Exam;
import tds.exam.ExpandableExam;
import tds.exam.results.mappers.CommentMapper;
import tds.exam.results.mappers.ExamineeMapper;
import tds.exam.results.mappers.OpportunityMapper;
import tds.exam.results.mappers.TestMapper;
import tds.exam.results.services.AssessmentService;
import tds.exam.results.services.ExamResultsService;
import tds.exam.results.services.ExamService;
import tds.exam.results.services.SessionService;
import tds.exam.results.trt.TDSReport;
import tds.exam.results.validation.TDSReportValidator;
import tds.session.Session;

@Service
public class ExamResultsServiceImpl implements ExamResultsService {
    private final ExamService examService;
    private final SessionService sessionService;
    private final AssessmentService assessmentService;
    private final TDSReportValidator tdsReportValidator;

    @Autowired
    public ExamResultsServiceImpl(final ExamService examService,
                                  final SessionService sessionService,
                                  final AssessmentService assessmentService,
                                  final TDSReportValidator tdsReportValidator) {
        this.examService = examService;
        this.sessionService = sessionService;
        this.assessmentService = assessmentService;
        this.tdsReportValidator = tdsReportValidator;
    }

    @Override
    public TDSReport findExamResults(final UUID examId) throws SAXException, IOException, JAXBException {
        TDSReport report = new TDSReport();
        final ExpandableExam expandableExam = examService.findExpandableExam(examId);
        final Exam exam = expandableExam.getExam();
        final Session session = sessionService.findSessionById(exam.getSessionId());
        final Assessment assessment = assessmentService.findAssessment(exam.getClientName(), exam.getAssessmentKey());

        report.setOpportunity(OpportunityMapper.mapOpportunity(expandableExam, session, assessment));
        report.setExaminee(ExamineeMapper.mapExaminee(expandableExam));
        report.setTest(TestMapper.mapTest(assessment));
        CommentMapper.mapComments(report.getComment(), expandableExam);

        tdsReportValidator.validateReport(report);

        return report;
    }


}
