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

package tds.exam.results.mappers;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import tds.assessment.Assessment;
import tds.assessment.AssessmentWindow;
import tds.assessment.Item;
import tds.exam.Exam;
import tds.exam.ExamAccommodation;
import tds.exam.ExamItem;
import tds.exam.ExamItemResponse;
import tds.exam.ExamItemResponseScore;
import tds.exam.ExamPage;
import tds.exam.ExamSegment;
import tds.exam.ExamStatusCode;
import tds.exam.ExpandableExam;
import tds.exam.results.mappers.utils.JaxbMapperUtils;
import tds.trt.model.ScoreInfoType;
import tds.trt.model.TDSReport;
import tds.exam.wrapper.ExamPageWrapper;
import tds.exam.wrapper.ExamSegmentWrapper;
import tds.session.Session;

import static org.apache.commons.lang3.StringUtils.upperCase;

/**
 * A class used for mapping a {@link tds.exam.results.trt.TDSReport.Opportunity} object from Exam and Session data
 */
public class OpportunityMapper {
    private static final String ADMINISTRATION_CONDITION_INVALIDATED = "IN";
    private static final float ITEM_NOT_SCORED_VALUE = -1;
    private static final String DEFAULT_ALGORITHM_VERSION = "0";
    private static final String EXAM_DATABASE_NAME = "exam";
    private static final String ITEM_NOT_SCORED_LABEL = "NOTSCORED";

    //This is the hard coded value for score dimensions in the TDS TRT based on ReportingDLL
    private static final String SCORE_DIMENSION_VALUE = "overall";

    public static TDSReport.Opportunity mapOpportunity(final ExpandableExam expandableExam,
                                                       final Session session,
                                                       final Assessment assessment,
                                                       final List<AssessmentWindow> assessmentWindows) {
        final Exam exam = expandableExam.getExam();
        // Get all exam items, for every page in every segment
        final List<ExamItem> examItems = expandableExam.getExamSegmentWrappers().stream()
            .flatMap(segments -> segments.getExamPages().stream()
                .flatMap(page -> page.getExamItems().stream()))
            .collect(Collectors.toList());

        TDSReport.Opportunity opportunity = new TDSReport.Opportunity();
        // required by TIS server to be an integer value
        opportunity.setOppId("0");
        opportunity.setKey(exam.getId().toString());
        opportunity.setDatabase(EXAM_DATABASE_NAME);
        opportunity.setStartDate(exam.getStartedAt().toDateTime().toString());
        opportunity.setStatus(exam.getStatus().getCode());
        opportunity.setOpportunity(exam.getAttempts());
        opportunity.setStatusDate(JaxbMapperUtils.convertInstantToGregorianCalendar(exam.getStartedAt()));
        opportunity.setDateCompleted(exam.getCompletedAt() != null
            ? exam.getCompletedAt().toDateTime().toString()
            : null);
        opportunity.setPauseCount(exam.getRestartsAndResumptions());
        opportunity.setItemCount(examItems.size());
        opportunity.setFtCount(examItems.stream()
            .filter(ExamItem::isFieldTest)
            .count());
        opportunity.setAbnormalStarts(exam.getAbnormalStarts());
        opportunity.setGracePeriodRestarts(exam.getResumptions());
        opportunity.setTaId(session.getProctorEmail());
        opportunity.setTaName(session.getProctorName());
        opportunity.setSessionId(session.getId().toString());
        opportunity.setWindowId(exam.getAssessmentWindowId());
        opportunity.setWindowOpportunity(String.valueOf(expandableExam.getWindowAttempts()));
        opportunity.setAdministrationCondition(exam.getStatus().getCode().equalsIgnoreCase(ExamStatusCode.STATUS_INVALIDATED)
            ? ADMINISTRATION_CONDITION_INVALIDATED
            :  StringUtils.EMPTY);
        opportunity.setDateForceCompleted(expandableExam.getForceCompletedAt() != null
            ? expandableExam.getForceCompletedAt().toDateTime().toString()
            : null);
        opportunity.setClientName(exam.getClientName());
        opportunity.setAssessmentParticipantSessionPlatformUserAgent(exam.getBrowserUserAgent());
        opportunity.setEffectiveDate(!assessmentWindows.isEmpty()
            ? assessmentWindows.get(0).getStartTime().toString() //TODO: Verify date format
            : StringUtils.EMPTY);

        mapExamSegmentsToOpportunity(expandableExam.getExamSegmentWrappers(), opportunity.getSegment());
        mapExamAccommodationsToOpportunity(expandableExam.getExamAccommodations(),  opportunity.getAccommodation());

        // Map all of the assessment items with the item id as the map's key
        final Map<String, Item> assessmentItems = assessment.getSegments().stream()
            .flatMap(segment -> segment.getItems().stream())
            .collect(Collectors.toMap(Item::getId, Function.identity(), (item1, item2) -> item1));

        mapExamItemsAndResponsesToOpportunity(expandableExam, examItems, opportunity.getItem(), assessmentItems);

        return opportunity;
    }

    private static void mapExamItemsAndResponsesToOpportunity(final ExpandableExam expandableExam,
                                                              final List<ExamItem> examItems,
                                                              final List<TDSReport.Opportunity.Item> opportunityItems,
                                                              final Map<String, Item> assessmentItems) {
        Map<UUID, Integer> itemVisitsMap = expandableExam.getItemResponseUpdates();
        final Map<UUID, ExamPageWrapper> examPageWrappers = expandableExam.getExamSegmentWrappers().stream()
            .flatMap(segment -> segment.getExamPages().stream())
            .collect(Collectors.toMap(wrapper -> wrapper.getExamPage().getId(), Function.identity()));

        for (ExamItem examItem : examItems) {
            // find the page (wrapper) for this exam item - this should never be "empty"
            final ExamPageWrapper examPageWrapper = examPageWrappers.get(examItem.getExamPageId());
            final ExamPage examPage = examPageWrapper.getExamPage();

            TDSReport.Opportunity.Item opportunityItem = new TDSReport.Opportunity.Item();
            Item assessmentItem = assessmentItems.get(examItem.getItemKey());

            opportunityItem.setPosition(examItem.getPosition());
            opportunityItem.setSegmentId(examPage.getSegmentKey());
            opportunityItem.setBankKey(examItem.getAssessmentItemBankKey());
            opportunityItem.setKey(examItem.getAssessmentItemKey());
            opportunityItem.setClientId(assessmentItem.getClientId());
            opportunityItem.setOperational(examItem.isFieldTest() ? (short) 0 : 1);
            opportunityItem.setFormat(examItem.getItemType());
            opportunityItem.setAdminDate(JaxbMapperUtils.convertInstantToGregorianCalendar(examPage.getCreatedAt()));

            //Handle unanswered items
            if(itemVisitsMap.containsKey(examItem.getId())) {
                opportunityItem.setNumberVisits(itemVisitsMap.get(examItem.getId()));
            }

            opportunityItem.setMimeType(assessmentItem.getMimeType());
            opportunityItem.setStrand(getStrandFromContentLevel(assessmentItem.getContentLevel()));
            opportunityItem.setContentLevel(assessmentItem.getContentLevel());
            opportunityItem.setPageNumber(examPage.getPagePosition());
            opportunityItem.setPageTime((int) examPage.getDuration());
            opportunityItem.setDropped(assessmentItem.isNotForScoring() ? (short) 1 : 0);
            opportunityItem.setScoreStatus(ITEM_NOT_SCORED_LABEL);

            // If the item response has a score, set it. Otherwise default to -1
            if (examItem.getResponse().isPresent()) {
                ExamItemResponse examItemResponse = examItem.getResponse().get();
                opportunityItem.setResponseDuration(examPage.getDuration() / examPageWrapper.getExamItems().size());
                opportunityItem.setIsSelected(examItemResponse.isSelected() ? (short) 1 : 0);
                TDSReport.Opportunity.Item.Response response = new TDSReport.Opportunity.Item.Response();
                // See ReportingDLL [991] - need to wrap response in CDATA element if MC or MS type
                final String content =
                    (examItem.getItemType().equalsIgnoreCase("MC") || examItem.getItemType().equalsIgnoreCase("MS"))
                        ? "<![CDATA[ " + examItemResponse.getResponse() + " ]]>"
                        : examItemResponse.getResponse();
                response.setContent(content);
                response.setDate(JaxbMapperUtils.convertInstantToGregorianCalendar(examItemResponse.getCreatedAt()));
                opportunityItem.setResponse(response);

                if (examItemResponse.getScore().isPresent()) {
                    ExamItemResponseScore score = examItemResponse.getScore().get();
                    final String scoreString = String.valueOf(score.getScore());
                    final String scoreStatus = score.getScoringStatus().toString();
                    // <ScoreInfo> element
                    ScoreInfoType scoreInfo = new ScoreInfoType();
                    // Add the child  <ScoreRationale>
                    ScoreInfoType.ScoreRationale rationale = new ScoreInfoType.ScoreRationale();
                    rationale.getContent().add(score.getScoringRationale());
                    scoreInfo.setScoreRationale(rationale);
                    scoreInfo.setScorePoint(scoreString);
                    scoreInfo.setMaxScore(String.valueOf(assessmentItem.getMaxScore()));
                    scoreInfo.setScoreStatus(scoreStatus);
                    scoreInfo.setScoreDimension(SCORE_DIMENSION_VALUE);

                    opportunityItem.setScoreInfo(scoreInfo);
                    opportunityItem.setScore(scoreString);
                    opportunityItem.setScoreStatus(upperCase(scoreStatus));
                } else {
                    opportunityItem.setScore(String.valueOf(ITEM_NOT_SCORED_VALUE));
                }
            } else {
                opportunityItem.setScore(String.valueOf(ITEM_NOT_SCORED_VALUE));
            }

            opportunityItems.add(opportunityItem);
        }
    }

    /* Port of ReportingDLL.ItemkeyStrandName_F() */
    private static String getStrandFromContentLevel(final String contentLevel) {
        if (contentLevel.contains("|")) {
            return contentLevel.split("\\|")[0];
        }

        return contentLevel;
    }

    private static void mapExamAccommodationsToOpportunity(final List<ExamAccommodation> examAccommodations,
                                                           final List<TDSReport.Opportunity.Accommodation> opportunityAccommodations) {
        for (ExamAccommodation examAccommodation : examAccommodations) {
            TDSReport.Opportunity.Accommodation opportunityAccommodation = new TDSReport.Opportunity.Accommodation();
            opportunityAccommodation.setType(examAccommodation.getType());
            opportunityAccommodation.setValue(examAccommodation.getValue());
            opportunityAccommodation.setCode(examAccommodation.getCode());
            opportunityAccommodation.setSegment(examAccommodation.getSegmentPosition());
            opportunityAccommodations.add(opportunityAccommodation);
        }
    }

    private static void mapExamSegmentsToOpportunity(final List<ExamSegmentWrapper> examSegmentWrappers,
                                                     final List<TDSReport.Opportunity.Segment> opportunitySegments) {
        for (ExamSegmentWrapper examSegmentWrapper : examSegmentWrappers) {
            ExamSegment examSegment = examSegmentWrapper.getExamSegment();
            TDSReport.Opportunity.Segment opportunitySegment = new TDSReport.Opportunity.Segment();
            opportunitySegment.setId(examSegment.getSegmentKey());
            opportunitySegment.setPosition((short)examSegment.getSegmentPosition());
            opportunitySegment.setFormKey(examSegment.getFormKey());
            opportunitySegment.setFormId(examSegment.getFormId());
            opportunitySegment.setAlgorithm(examSegment.getAlgorithm().getType());
            opportunitySegment.setAlgorithmVersion(DEFAULT_ALGORITHM_VERSION);
            opportunitySegments.add(opportunitySegment);
        }
    }
}
