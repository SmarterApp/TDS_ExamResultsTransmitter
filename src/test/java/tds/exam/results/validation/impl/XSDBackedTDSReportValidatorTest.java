package tds.exam.results.validation.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;

import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.trt.TDSReport;
import tds.exam.results.validation.TDSReportValidator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class XSDBackedTDSReportValidatorTest {
    private Resource resource;
    private ExamResultsTransmitterServiceProperties properties;

    @Before
    public void setUp() throws Exception {
        resource = new ClassPathResource("xsd/TestResultsTransmissionFormat_Schema.xsd");
        properties = new ExamResultsTransmitterServiceProperties();
        properties.setValidateTrtXml(true);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void shouldNotValidate() throws IOException, SAXException {
        JAXBContext context = mock(JAXBContext.class);
        properties.setValidateTrtXml(false);
        TDSReportValidator validator = new XSDBackedTDSReportValidator(context, resource, properties);

        TDSReport report = new TDSReport();

        validator.validateReport(report);

        verifyZeroInteractions(context);
    }

    @Test
    @Ignore
    public void shouldValidateTrt() throws JAXBException, IOException, SAXException {
        JAXBContext jaxbContext = JAXBContext.newInstance(TDSReport.class);
        TDSReportValidator validator = new XSDBackedTDSReportValidator(jaxbContext, resource, properties);

        TDSReport report = buildValidReport();

        validator.validateReport(report);
    }

    private static TDSReport buildValidReport() {
        TDSReport report = new TDSReport();

        TDSReport.Examinee examinee = new TDSReport.Examinee();
        examinee.setIsDemo((short) 0);
        examinee.setKey(1L);

        report.setExaminee(examinee);

        TDSReport.Opportunity opportunity = new TDSReport.Opportunity();
        opportunity.setAbnormalStarts(1L);
        opportunity.setAdministrationCondition("admin");
        opportunity.setAssessmentParticipantSessionPlatformUserAgent("safari");
        opportunity.setAbnormalStarts(0);
        opportunity.setClientName("SBAC_PT");
        opportunity.setCompleteness("complete");
        opportunity.setDatabase("db");
        opportunity.setCompleteStatus("Complete");
        opportunity.setKey("key");

        report.setOpportunity(opportunity);

        TDSReport.Test test = new TDSReport.Test();
        test.setAcademicYear(2016L);
        test.setAssessmentType("adaptive");
        test.setAssessmentVersion("1.0");
        test.setBankKey(1L);
        test.setContract("contract");
        test.setGrade("3");
        test.setHandScoreProject(1L);
        test.setMode("online");
        test.setName("3 ELA");
        test.setSubject("ELA");
        test.setTestId("testId");

        report.setTest(test);

        return report;
    }
}