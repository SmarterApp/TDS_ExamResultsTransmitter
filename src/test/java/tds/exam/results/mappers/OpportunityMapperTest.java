/*******************************************************************************
 * Copyright 2016 Smarter Balance Licensed under the
 *     Educational Community License, Version 2.0 (the "License"); you may
 *     not use this file except in compliance with the License. You may
 *     obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an "AS IS"
 *     BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *     or implied. See the License for the specific language governing
 *     permissions and limitations under the License.
 ******************************************************************************/

package tds.exam.results.mappers;

import org.junit.Test;
import tds.assessment.Assessment;
import tds.assessment.AssessmentWindow;
import tds.assessment.Item;
import tds.exam.Exam;
import tds.exam.ExamAccommodation;
import tds.exam.ExamItem;
import tds.exam.ExamItemResponse;
import tds.exam.ExamPage;
import tds.exam.ExamSegment;
import tds.exam.ExpandableExam;
import tds.exam.results.trt.TDSReport;
import tds.exam.wrapper.ExamPageWrapper;
import tds.exam.wrapper.ExamSegmentWrapper;
import tds.session.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.assertj.core.api.Assertions.assertThat;

public class OpportunityMapperTest {

    @Test
    public void shouldMapExpandableExamToOpportunity() {
        final Session session = random(Session.class);
        final Assessment assessment = random(Assessment.class);

        final ExpandableExam expandableExam = expandableExam();
        final Exam exam = expandableExam.getExam();
        final List<ExamAccommodation> examAccommodations = expandableExam.getExamAccommodations();
        final ExamSegmentWrapper examSegmentWrapper = expandableExam.getExamSegmentWrappers().get(0);

        final List<Item> assessmentItems = examSegmentWrapper.getExamPages().stream()
            .flatMap(p -> p.getExamItems().stream())
            .map(examItem -> {
                Item item = new Item(examItem.getItemKey());
                item.setContentLevel("Strand|TL|D-R");
                return item;
            })
            .collect(Collectors.toList());

        assessment.getSegments().forEach(segment -> segment.setItems(assessmentItems));
        final List<AssessmentWindow> assessmentWindows = randomListOf(1, AssessmentWindow.class);

        final TDSReport.Opportunity opportunity = OpportunityMapper.mapOpportunity(expandableExam, session, assessment, assessmentWindows);

        assertThat(opportunity).isNotNull();
        assertThat(opportunity.getKey()).isEqualTo(exam.getId().toString());
        assertThat(opportunity.getDatabase()).isEqualTo("exam");
        assertThat(opportunity.getStartDate()).isEqualTo(exam.getStartedAt().toDateTime().toString());
        assertThat(opportunity.getStatus()).isEqualTo(exam.getStatus().getCode());
        assertThat(opportunity.getStatusDate()).isNotNull();
        assertThat(opportunity.getDateCompleted()).isEqualTo(exam.getCompletedAt().toDateTime().toString());
        assertThat(opportunity.getPauseCount()).isEqualTo(exam.getRestartsAndResumptions());
        assertThat(opportunity.getItemCount()).isEqualTo(4);
        assertThat(opportunity.getFtCount()).isEqualTo(0);
        assertThat(opportunity.getAbnormalStarts()).isEqualTo(exam.getAbnormalStarts());
        assertThat(opportunity.getGracePeriodRestarts()).isEqualTo(exam.getResumptions());
        assertThat(opportunity.getTaName()).isEqualTo(session.getProctorName());
        assertThat(opportunity.getTaId()).isEqualTo(session.getProctorEmail());
        assertThat(opportunity.getSessionId()).isEqualTo(session.getId().toString());
        assertThat(opportunity.getWindowId()).isEqualTo(exam.getAssessmentWindowId());
        assertThat(opportunity.getAssessmentParticipantSessionPlatformUserAgent()).isEqualTo(exam.getBrowserUserAgent());
        assertThat(opportunity.getWindowOpportunity()).isEqualTo(String.valueOf(expandableExam.getWindowAttempts()));
        assertThat(Integer.valueOf(opportunity.getOppId())).isInstanceOf(Integer.class);

        // ExamSegment assertions
        assertThat(opportunity.getSegment().size()).isEqualTo(1);
        ExamSegment examSegment1 = examSegmentWrapper.getExamSegment();
        TDSReport.Opportunity.Segment oppSeg1 = null;

        for (TDSReport.Opportunity.Segment oppSeg : opportunity.getSegment()) {
            if (oppSeg.getId().equals(examSegment1.getSegmentId())) {
                oppSeg1 = oppSeg;
            }
        }

        assertThat(oppSeg1.getPosition()).isEqualTo((short) examSegment1.getSegmentPosition());
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

        assertThat(opportunity.getItem()).hasSize(4);
        for (TDSReport.Opportunity.Item item : opportunity.getItem()) {
            assertThat(item.getPosition()).isGreaterThan(0);
            assertThat(item.getSegmentId()).isEqualTo("segmentId1");
            assertThat(item.getBankKey()).isEqualTo(187);
            assertThat(item.getKey()).isGreaterThan(0);
            assertThat(item.getOperational()).isEqualTo((short) 1);
            assertThat(item.getContentLevel()).isEqualTo("Strand|TL|D-R");
            assertThat(item.getStrand()).isEqualTo("Strand");
            TDSReport.Opportunity.Item.Response response = item.getResponse();

            assertThat(response).isNotNull();
            assertThat(response.getContent()).isNotNull();
            assertThat(response.getDate()).isNotNull();
            assertThat(item.getScoreInfo().getScoreDimension()).isEqualTo("overall");
        }
    }

    @Test
    public void shouldCapitalizeTheItemScoreStatus() {
        final Session session = random(Session.class);
        final Assessment assessment = random(Assessment.class);

        final ExpandableExam expandableExam = expandableExam();
        final ExamSegmentWrapper examSegmentWrapper = expandableExam.getExamSegmentWrappers().get(0);

        final List<Item> assessmentItems = examSegmentWrapper.getExamPages().stream()
            .flatMap(p -> p.getExamItems().stream())
            .map(examItem -> {
                Item item = new Item(examItem.getItemKey());
                item.setContentLevel("Strand|TL|D-R");
                return item;
            })
            .collect(Collectors.toList());

        assessment.getSegments().forEach(segment -> segment.setItems(assessmentItems));
        final List<AssessmentWindow> assessmentWindows = randomListOf(1, AssessmentWindow.class);

        final TDSReport.Opportunity opportunity = OpportunityMapper.mapOpportunity(expandableExam, session, assessment, assessmentWindows);

        assertThat(opportunity.getItem().stream()
            .map(item -> item.getScoreStatus())
            .collect(Collectors.toList())).isSubsetOf(
                "NOTSCORED",
                "SCORED",
                "NOSCORINGENGINE",
                "SCORINGERROR",
                "WAITINGFORMACHINESCORE");
    }

    @Test
    public void shouldUseTheContentLevelAsTheStrand() {
        final Session session = random(Session.class);
        final Assessment assessment = random(Assessment.class);

        final ExpandableExam expandableExam = expandableExam();
        final ExamSegmentWrapper examSegmentWrapper = expandableExam.getExamSegmentWrappers().get(0);

        final List<Item> assessmentItems = examSegmentWrapper.getExamPages().stream()
            .flatMap(p -> p.getExamItems().stream())
            .map(examItem -> {
                Item item = new Item(examItem.getItemKey());
                item.setContentLevel("Strand|TL|D-R");
                return item;
            })
            .collect(Collectors.toList());

        assessment.getSegments().forEach(segment -> segment.setItems(assessmentItems));
        final List<AssessmentWindow> assessmentWindows = randomListOf(1, AssessmentWindow.class);

        final TDSReport.Opportunity opportunity = OpportunityMapper.mapOpportunity(expandableExam, session, assessment, assessmentWindows);

        assertThat(opportunity.getItem().stream()
            .map(item -> item.getStrand())
            .collect(Collectors.toList())).isSubsetOf(
                assessment.getSegments().stream()
                    .flatMap(segment -> segment.getItems().stream())
                    .map(assessmentItem -> "Strand")
                    .collect(Collectors.toList()));
    }

    private List<ExamPageWrapper> mapMockExamPagesAndItems(final List<ExamPage> examPages) {
        List<ExamPageWrapper> pageWrappers = new ArrayList<>();
        // Mock/map the exam page ids from "ExamItems" to actual ExamPages.
        for (int i = 1; i < 5; i++) {
            ExamPage page = examPages.get(i);
            examPages.set(i, ExamPage.Builder
                .fromExamPage(page)
                .withId(UUID.randomUUID())
                .withSegmentKey("segmentKey1")
                .build());

            ExamItem examItem = new ExamItem.Builder(UUID.randomUUID())
                .withItemKey("187-" + i)
                .withAssessmentItemBankKey(187)
                .withAssessmentItemKey(i)
                .withPosition(i)
                .withExamPageId(examPages.get(i).getId())
                .withItemType(random(String.class))
                .withResponse(random(ExamItemResponse.class))
                .withItemFilePath("/path/to/file/")
                .withGroupId("groupId")
                .build();

            pageWrappers.add(new ExamPageWrapper(examPages.get(i), Collections.singletonList(examItem)));
        }

        return pageWrappers;
    }

    private ExpandableExam expandableExam() {
        final Exam exam = random(Exam.class);
        final List<ExamPage> examPages = randomListOf(5, ExamPage.class);
        final List<ExamAccommodation> examAccommodations = randomListOf(20, ExamAccommodation.class);
        final List<ExamPageWrapper> pageWrappers = mapMockExamPagesAndItems(examPages);

        final ExamSegmentWrapper examSegmentWrapper = new ExamSegmentWrapper(
            ExamSegment.Builder
                .fromSegment(random(ExamSegment.class))
                .withSegmentKey("segmentKey1")
                .withSegmentId("segmentId1")
                .build(), pageWrappers);

        final Map<UUID, Integer> itemResponseUpdates = new HashMap<>();
        final List<ExamItem> allExamItems = examSegmentWrapper.getExamPages().stream()
            .flatMap(p -> p.getExamItems().stream())
            .collect(Collectors.toList());

        for (final ExamItem item : allExamItems) {
            itemResponseUpdates.put(item.getId(), random(Integer.class));
        }

        return new ExpandableExam.Builder(exam)
            .withExamSegmentWrappers(Collections.singletonList(examSegmentWrapper))
            .withExamPages(examPages)
            .withExamAccommodations(examAccommodations)
            .withItemResponseUpdates(itemResponseUpdates)
            .withWindowAttempts(3)
            .build();
    }
}
