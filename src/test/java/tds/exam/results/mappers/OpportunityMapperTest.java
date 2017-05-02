package tds.exam.results.mappers;

import org.joda.time.Instant;
import org.junit.Test;

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
import tds.exam.ExpandableExam;
import tds.exam.results.trt.TDSReport;
import tds.session.Session;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.assertj.core.api.Assertions.assertThat;

public class OpportunityMapperTest {

    @Test
    public void shouldMapExpandableExamToOpportunity() {
        Session session = random(Session.class);
        Assessment assessment = random(Assessment.class);
        Exam exam = random(Exam.class);
        List<ExamPage> examPages = randomListOf(5, ExamPage.class);
        List<ExamAccommodation> examAccommodations = randomListOf(20, ExamAccommodation.class);
        List<ExamSegment> examSegments = randomListOf(2, ExamSegment.class);
        List<ExamItem> examItems = new ArrayList<>();

        mapMockExamPagesAndItems(examPages, examItems);

        ExpandableExam expandableExam = new ExpandableExam.Builder(exam)
            .withExamItems(examItems)
            .withExamPages(examPages)
            .withExamAccommodations(examAccommodations)
            .withExamSegments(examSegments)
            .withStartedAt(Instant.now().minus(2000))
            .withCompletedAt(Instant.now().minus(500))
            .build();

        List<Item> assessmentItems = expandableExam.getExamItems().stream()
            .map(examItem -> new Item(examItem.getItemKey()))
            .collect(Collectors.toList());
        assessment.getSegments().forEach(segment -> segment.setItems(assessmentItems));

        TDSReport.Opportunity opportunity = OpportunityMapper.mapOpportunity(expandableExam, session, assessment);

        assertThat(opportunity).isNotNull();
        assertThat(opportunity.getKey()).isEqualTo(exam.getId().toString());
        assertThat(opportunity.getDatabase()).isEqualTo("exam");
        assertThat(opportunity.getStartDate()).isEqualTo(exam.getStartedAt().toString());
        assertThat(opportunity.getStatus()).isEqualTo(exam.getStatus().getCode());
        assertThat(opportunity.getStatusDate()).isNotNull();
        assertThat(opportunity.getDateCompleted()).isEqualTo(exam.getCompletedAt().toString());
        assertThat(opportunity.getPauseCount()).isEqualTo(exam.getRestartsAndResumptions());
        assertThat(opportunity.getItemCount()).isEqualTo(expandableExam.getExamItems().size());
        assertThat(opportunity.getFtCount()).isEqualTo(expandableExam.getExamItems().stream()
            .filter(ExamItem::isFieldTest)
            .count());
        assertThat(opportunity.getAbnormalStarts()).isEqualTo(exam.getAbnormalStarts());
        assertThat(opportunity.getGracePeriodRestarts()).isEqualTo(exam.getResumptions());
        assertThat(opportunity.getTaName()).isEqualTo(session.getProctorName());
        assertThat(opportunity.getTaId()).isEqualTo(session.getProctorEmail());
        assertThat(opportunity.getSessionId()).isEqualTo(session.getId().toString());
        assertThat(opportunity.getWindowId()).isEqualTo(exam.getAssessmentWindowId());
        assertThat(opportunity.getAssessmentParticipantSessionPlatformUserAgent()).isEqualTo(exam.getBrowserUserAgent());

        // ExamSegment assertions
        assertThat(opportunity.getSegment().size()).isEqualTo(examSegments.size());
        ExamSegment examSegment1 = examSegments.get(0);
        TDSReport.Opportunity.Segment oppSeg1 = null;

        for (TDSReport.Opportunity.Segment oppSeg : opportunity.getSegment()) {
            if (oppSeg.getId().equals(examSegment1.getSegmentId())) {
                oppSeg1 = oppSeg;
            }
        }

        assertThat(oppSeg1.getPosition()).isEqualTo((short)examSegment1.getSegmentPosition());
        assertThat(oppSeg1.getFormKey()).isEqualTo(examSegment1.getFormKey());
        assertThat(oppSeg1.getFormId()).isEqualTo(examSegment1.getFormId());
        assertThat(oppSeg1.getAlgorithm()).isEqualTo(examSegment1.getAlgorithm().getType());
        assertThat(oppSeg1.getAlgorithmVersion()).isEqualTo("0");

        // ExamAccommodation assertions
        assertThat(opportunity.getAccommodation().size()).isEqualTo(examAccommodations.size());
        ExamAccommodation examAccommodation1 = examAccommodations.get(0);
        TDSReport.Opportunity.Accommodation acc1 = null;

        for (TDSReport.Opportunity.Accommodation accommodation : opportunity.getAccommodation()) {
            if (accommodation.getCode().equals(examAccommodation1.getCode())) {
                acc1 = accommodation;
            }
        }

        assertThat(acc1.getType()).isEqualTo(examAccommodation1.getType());
        assertThat(acc1.getSegment()).isEqualTo(examAccommodation1.getSegmentPosition());
        assertThat(acc1.getValue()).isEqualTo(examAccommodation1.getValue());
    }

    private void mapMockExamPagesAndItems(final List<ExamPage> examPages, final List<ExamItem> examItems) {
        // Mock/map the exam page ids from "ExamItems" to actual ExamPages.
        for (int i = 1; i < 5; i++) {
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
                .withItemFilePath("/path/to/file/")
                .build();

            examItems.add(examItem);
        }
    }
}
