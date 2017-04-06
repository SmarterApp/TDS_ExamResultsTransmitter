package tds.exam.results.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
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
import tds.session.Session;

@Service
public class ExamResultsServiceImpl implements ExamResultsService {
    private final ExamService examService;
    private final SessionService sessionService;
    private final AssessmentService assessmentService;
    private final JAXBContext contextObj;

    @Autowired
    public ExamResultsServiceImpl(final ExamService examService,
                                  final SessionService sessionService,
                                  final AssessmentService assessmentService) throws JAXBException {
        this.examService = examService;
        this.sessionService = sessionService;
        this.assessmentService = assessmentService;

        contextObj = JAXBContext.newInstance(TDSReport.class);
        Marshaller marshallerObj = contextObj.createMarshaller();
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
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

        validateReport(report);

        return report;
    }

    private void validateReport(final TDSReport results) throws JAXBException, SAXException, IOException {
        JAXBSource source = new JAXBSource(contextObj, results);
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File("src/main/xsd/TestResultsTransmissionFormat_Schema.xsd"));

        Validator validator = schema.newValidator();
        // TODO: Enable the validator once all "required" properties are added to Exam/Assessment
//        validator.validate(source);
    }
}
