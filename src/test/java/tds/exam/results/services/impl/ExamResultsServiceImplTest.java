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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import tds.assessment.Assessment;
import tds.assessment.AssessmentWindow;
import tds.assessment.Item;
import tds.exam.*;
import tds.exam.results.configuration.web.ExamResultsTransmitterApplicationConfiguration;
import tds.exam.results.model.ExamReportStatus;
import tds.exam.results.services.*;
import tds.trt.model.TDSReport;
import tds.exam.results.validation.TDSReportValidator;
import tds.session.ExternalSessionConfiguration;
import tds.session.Session;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExamResultsServiceImplTest {
    private ExamResultsService examResultsService;

    @Mock
    private ExamService mockExamService;

    @Mock
    private AssessmentService mockAssessmentService;

    @Mock
    private SessionService mockSessionService;

    @Mock
    private TDSReportValidator mockReportValidator;

    @Mock
    private ExamReportAuditService mockExamReportAuditService;

    @Mock
    private TestIntegrationSystemService mockTestIntegrationSystemService;

    private Marshaller marshaller;

    @Before
    public void setup() throws JAXBException {
        marshaller = createMarshaller();
        examResultsService = new ExamResultsServiceImpl(mockExamService, mockSessionService, mockAssessmentService,
            mockReportValidator, mockExamReportAuditService, mockTestIntegrationSystemService);
    }

    @Test
    // TDS-1626: ExamineeAttribute nodes must come before ExamineeRelationships for TIS to parse it.
    public void shouldMarshalExamineeAttributesBeforeRelationships() throws IOException, JAXBException, SAXException {
        Exam exam = random(Exam.class);
        Session session = random(Session.class);
        Assessment assessment = random(Assessment.class);
        List<ExamPage> examPages = randomListOf(10, ExamPage.class);
        List<ExamineeAttribute> examineeAttributes = randomListOf(20, ExamineeAttribute.class);
        List<ExamineeRelationship> examineeRelationships = randomListOf(5, ExamineeRelationship.class);
        List<ExamItem> examItems = new ArrayList<>();
        mapMockExamPagesAndItems(examPages, examItems);
        ExpandableExam expandableExam = new ExpandableExam.Builder(exam)
            .withExamItems(examItems)
            .withExamPages(examPages)
            .withExamineeAttributes(examineeAttributes)
            .withExamSegmentWrappers(new ArrayList<>())
            .withExamineeRelationship(examineeRelationships)
            .build();
        List<Item> assessmentItems = expandableExam.getExamItems().stream()
            .map(examItem -> new Item(examItem.getItemKey()))
            .collect(Collectors.toList());
        assessment.getSegments().forEach(segment -> segment.setItems(assessmentItems));
        assessment.setAcademicYear("2017");
        assessment.setGrades(Arrays.asList("7", "8"));
        ExternalSessionConfiguration configuration = random(ExternalSessionConfiguration.class);
        List<AssessmentWindow> assessmentWindows = randomListOf(1, AssessmentWindow.class);
        when(mockExamService.findExpandableExam(exam.getId())).thenReturn(expandableExam);
        when(mockAssessmentService.findAssessment(exam.getClientName(), exam.getAssessmentKey())).thenReturn(assessment);
        when(mockSessionService.findSessionById(exam.getSessionId())).thenReturn(session);
        when(mockSessionService.findExternalSessionConfigurationByClientName(exam.getClientName())).thenReturn(configuration);
        when(mockAssessmentService.findAssessmentWindows(exam.getClientName(), exam.getAssessmentId(), exam.getStudentId() < 0, configuration))
            .thenReturn(assessmentWindows);

        TDSReport report = examResultsService.findAndSendExamResults(expandableExam.getExam().getId());

        final StringWriter sw = new StringWriter();
        marshaller.marshal(report, sw);
        final String reportXml = sw.toString();
        assertThat(reportXml.lastIndexOf("ExamineeAttribute"))
            .withFailMessage("Couldn't find ExamineeAttribute")
            .isPositive();
        assertThat(reportXml.indexOf("ExamineeRelationship"))
            .withFailMessage("Couldn't find ExamineeRelationship")
            .isPositive();
        assertThat(reportXml.lastIndexOf("ExamineeAttribute"))
            .withFailMessage("ExamineeRelationship unexpectedly found before ExamineeAttribute")
            .isLessThan(reportXml.indexOf("ExamineeRelationship"));
    }

    @Test
    public void shouldMapExamResultsToTDSReport() throws IOException, JAXBException, SAXException {
        Exam exam = random(Exam.class);
        Session session = random(Session.class);

        Assessment assessment = random(Assessment.class);
        List<ExamPage> examPages = randomListOf(10, ExamPage.class);
        List<ExamAccommodation> examAccommodations = randomListOf(20, ExamAccommodation.class);
        List<ExamineeAttribute> examineeAttributes = randomListOf(20, ExamineeAttribute.class);
        List<ExamineeRelationship> examineeRelationships = randomListOf(5, ExamineeRelationship.class);
        List<ExamineeNote> examineeNotes = randomListOf(3, ExamineeNote.class);
        List<ExamItem> examItems = new ArrayList<>();

        mapMockExamPagesAndItems(examPages, examItems);

        ExpandableExam expandableExam = new ExpandableExam.Builder(exam)
            .withExamItems(examItems)
            .withExamPages(examPages)
            .withExamAccommodations(examAccommodations)
            .withExamineeAttributes(examineeAttributes)
            .withExamSegmentWrappers(new ArrayList<>())
            .withExamineeRelationship(examineeRelationships)
            .withExamineeNotes(examineeNotes)
            .build();

        List<Item> assessmentItems = expandableExam.getExamItems().stream()
            .map(examItem -> new Item(examItem.getItemKey()))
            .collect(Collectors.toList());
        assessment.getSegments().forEach(segment -> segment.setItems(assessmentItems));
        assessment.setAcademicYear("2017");
        assessment.setGrades(Arrays.asList("7", "8"));

        ExternalSessionConfiguration configuration = random(ExternalSessionConfiguration.class);
        List<AssessmentWindow> assessmentWindows = randomListOf(1, AssessmentWindow.class);
        when(mockExamService.findExpandableExam(exam.getId())).thenReturn(expandableExam);
        when(mockAssessmentService.findAssessment(exam.getClientName(), exam.getAssessmentKey())).thenReturn(assessment);
        when(mockSessionService.findSessionById(exam.getSessionId())).thenReturn(session);
        when(mockSessionService.findExternalSessionConfigurationByClientName(exam.getClientName())).thenReturn(configuration);
        when(mockAssessmentService.findAssessmentWindows(exam.getClientName(), exam.getAssessmentId(), exam.getStudentId() < 0, configuration))
            .thenReturn(assessmentWindows);

        TDSReport report = examResultsService.findAndSendExamResults(expandableExam.getExam().getId());

        verify(mockExamService).findExpandableExam(exam.getId());
        verify(mockAssessmentService).findAssessment(exam.getClientName(), exam.getAssessmentKey());
        verify(mockSessionService).findSessionById(exam.getSessionId());
        verify(mockExamReportAuditService).insertExamReport(eq(exam.getId()), eq(report), eq(ExamReportStatus.RECEIVED));
        verify(mockExamReportAuditService).insertExamReport(eq(exam.getId()), eq(report), eq(ExamReportStatus.SENT));
        verify(mockTestIntegrationSystemService).sendResults(eq(exam.getId()), eq(report));
        verify(mockSessionService).findExternalSessionConfigurationByClientName(exam.getClientName());
        verify(mockAssessmentService).findAssessmentWindows(exam.getClientName(), exam.getAssessmentId(), exam.getStudentId() < 0, configuration);
        // NOTE: Actual mapping logic unit test coverage will be in each individual Mapper class
        assertThat(report).isNotNull();
        assertThat(report.getTest()).isNotNull();
        assertThat(report.getComment()).isNotNull();
        assertThat(report.getExaminee()).isNotNull();
        assertThat(report.getOpportunity()).isNotNull();
    }

    private void mapMockExamPagesAndItems(final List<ExamPage> examPages, final List<ExamItem> examItems) {
        // Mock/map the exam page ids from "ExamItems" to actual ExamPages.
        for (int i = 1; i < 10; i++) {
            ExamPage page = examPages.get(i);
            examPages.set(i, ExamPage.Builder
                .fromExamPage(page)
                .withId(UUID.randomUUID())
                .build());

            ExamItem examItem = new ExamItem.Builder(UUID.randomUUID())
                .withItemKey("187-" + i)
                .withPosition(i)
                .withExamPageId(examPages.get(i).getId())
                .withItemType(random(String.class))
                .withResponse(random(ExamItemResponse.class))
                .withItemFilePath("/path/to/file")
                .withGroupId("groupId")
                .build();

            examItems.add(examItem);
        }
    }

    private Marshaller createMarshaller() throws JAXBException {
        JAXBContext contextObj = JAXBContext.newInstance(TDSReport.class);
        return ExamResultsTransmitterApplicationConfiguration.createMarshaller(contextObj);
    }
}
