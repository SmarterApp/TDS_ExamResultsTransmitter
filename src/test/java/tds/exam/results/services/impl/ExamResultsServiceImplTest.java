package tds.exam.results.services.impl;

import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import tds.assessment.Assessment;
import tds.assessment.Item;
import tds.exam.Exam;
import tds.exam.ExamAccommodation;
import tds.exam.ExamItem;
import tds.exam.ExamItemResponse;
import tds.exam.ExamPage;
import tds.exam.ExamSegment;
import tds.exam.ExamineeAttribute;
import tds.exam.ExamineeNote;
import tds.exam.ExamineeRelationship;
import tds.exam.ExpandableExam;
import tds.exam.results.services.AssessmentService;
import tds.exam.results.services.ExamResultsService;
import tds.exam.results.services.ExamService;
import tds.exam.results.services.SessionService;
import tds.exam.results.trt.TDSReport;
import tds.session.Session;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExamResultsServiceImplTest {
    private ExamResultsService examResultsService;

    @Mock
    ExamService mockExamService;

    @Mock
    AssessmentService mockAssessmentService;

    @Mock
    SessionService mockSessionService;

    @Before
    public void setup() throws JAXBException {
        examResultsService = new ExamResultsServiceImpl(mockExamService, mockSessionService, mockAssessmentService);
    }

    @Test
    public void shouldMapExamResultsToTDSReport() throws IOException, JAXBException, SAXException {
        Exam exam = random(Exam.class);
        Session session = random(Session.class);

        Assessment assessment = random(Assessment.class);
        List<ExamPage> examPages = randomListOf(10, ExamPage.class);
        List<ExamAccommodation> examAccommodations = randomListOf(20, ExamAccommodation.class);
        List<ExamSegment> examSegments = randomListOf(2, ExamSegment.class);
        List<ExamineeAttribute> examineeAttributes = randomListOf(20, ExamineeAttribute.class);
        List<ExamineeRelationship> examineeRelationships = randomListOf(5, ExamineeRelationship.class);
        List<ExamineeNote> examineeNotes = randomListOf(3, ExamineeNote.class);
        List<ExamItem> examItems = new ArrayList<>();

        mapMockExamPagesAndItems(examPages, examItems);

        ExpandableExam expandableExam = new ExpandableExam.Builder(exam)
            .withExamItems(examItems)
            .withExamPages(examPages)
            .withExamAccommodations(examAccommodations)
            .withExamSegments(examSegments)
            .withStartedAt(Instant.now().minus(2000))
            .withCompletedAt(Instant.now().minus(500))
            .withExamineeAttributes(examineeAttributes)
            .withExamineeRelationship(examineeRelationships)
            .withExamineeNotes(examineeNotes)
            .build();

        List<Item> assessmentItems = expandableExam.getExamItems().stream()
            .map(examItem -> new Item(examItem.getItemKey()))
            .collect(Collectors.toList());
        assessment.getSegments().forEach(segment -> segment.setItems(assessmentItems));

        when(mockExamService.findExpandableExam(exam.getId())).thenReturn(expandableExam);
        when(mockAssessmentService.findAssessment(exam.getClientName(), exam.getAssessmentKey())).thenReturn(assessment);
        when(mockSessionService.findSessionById(exam.getSessionId())).thenReturn(session);

        TDSReport report = examResultsService.findExamResults(expandableExam.getExam().getId());

        verify(mockExamService).findExpandableExam(exam.getId());
        verify(mockAssessmentService).findAssessment(exam.getClientName(), exam.getAssessmentKey());
        verify(mockSessionService).findSessionById(exam.getSessionId());

        // NOTE: Actual mapping logic unit test coverage will be in each individual Mapper class
        assertThat(report).isNotNull();
        assertThat(report.getTest()).isNotNull();
        assertThat(report.getComment()).isNotNull();
        assertThat(report.getExaminee()).isNotNull();
        assertThat(report.getOpportunity()).isNotNull();
    }

    private void mapMockExamPagesAndItems(final List<ExamPage> examPages, final List<ExamItem> examItems) {
        // Mock/map the exam page ids from "ExamItems" to actual ExamPages.
        for (int i = 0; i < 10; i++) {
            ExamPage page = examPages.get(i);
            examPages.set(i, new ExamPage.Builder()
                .fromExamPage(page)
                .withId(UUID.randomUUID())
                .build());

            ExamItem examItem = new ExamItem.Builder(UUID.randomUUID())
                .withItemKey("187-" + i)
                .withPosition(i)
                .withExamPageId(examPages.get(i).getId())
                .withItemType(random(String.class))
                .withResponse(random(ExamItemResponse.class))
                .build();

            examItems.add(examItem);
        }
    }
}
