package tds.exam.results.mappers;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import tds.assessment.Assessment;
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
import tds.exam.results.trt.ScoreInfoType;
import tds.exam.results.trt.TDSReport;
import tds.session.Session;

import tds.exam.results.mappers.utils.JaxbMapperUtils;

/**
 * A class used for mapping a {@link tds.exam.results.trt.TDSReport.Opportunity} object from Exam and Session data
 */
@Component
public class OpportunityMapper {
    private static final String ADMINISTRATION_CONDITION_INVALIDATED = "IN";
    private static final float ITEM_NOT_SCORED_VALUE = -1;
    private static final String DEFAULT_ALGORITHM_VERSION = "0";

    public static TDSReport.Opportunity mapOpportunity(final ExpandableExam expandableExam, final Session session,
                                                       final Assessment assessment) {
        final Exam exam = expandableExam.getExam();
        TDSReport.Opportunity opportunity = new TDSReport.Opportunity();
        opportunity.setKey(exam.getId().toString());
//        opportunity.setOppId(oppId.toString()); //TODO: Create oppId - auto-incremented Long ID
        opportunity.setStartDate(expandableExam.getStartedAt().toString()); //TODO: Verify date format
        opportunity.setStatus(exam.getStatus().getCode());
        opportunity.setOpportunity(exam.getAttempts());
        opportunity.setStatusDate(JaxbMapperUtils.convertInstantToGregorianCalendar(exam.getStartedAt()));
        opportunity.setDateCompleted(expandableExam.getCompletedAt().toString()); //TODO: Verify date format
        opportunity.setPauseCount(exam.getRestartsAndResumptions());
        opportunity.setItemCount(expandableExam.getExamItems().size());
        opportunity.setFtCount(expandableExam.getExamItems().stream()
            .filter(ExamItem::isFieldTest)
            .count());
        opportunity.setAbnormalStarts(exam.getAbnormalStarts());
        opportunity.setGracePeriodRestarts(exam.getResumptions());
        opportunity.setTaId(session.getProctorEmail());
        opportunity.setTaName(session.getProctorName());
        opportunity.setSessionId(session.getId().toString());
        opportunity.setWindowId(exam.getAssessmentWindowId());
//        opportunity.setWindowOpportunity(); TODO: Add windowOpportunity to ExpandableExam
        opportunity.setAdministrationCondition(
            exam.getStatus().getCode().equalsIgnoreCase(ExamStatusCode.STATUS_INVALIDATED)
                ? ADMINISTRATION_CONDITION_INVALIDATED
                : "");
        opportunity.setDateForceCompleted(expandableExam.getForceCompletedAt().toString()); //TODO: Verify date format
        opportunity.setClientName(exam.getClientName());
        opportunity.setAssessmentParticipantSessionPlatformUserAgent(exam.getBrowserUserAgent());
//        opportunity.setEffectiveDate(); TODO: Add effectiveDate to ExpandableExam

        mapExamSegmentsToOpportunity(expandableExam.getExamSegments(), opportunity.getSegment());
        mapExamAccommodationsToOpportunity(expandableExam.getExamAccommodations(),  opportunity.getAccommodation());

        // Map all of the assessment items with the item id as the map's key
        final Map<String, Item> assessmentItems = assessment.getSegments().stream()
            .flatMap(segment -> segment.getItems().stream())
            .collect(Collectors.toMap(Item::getId, Function.identity()));

        mapExamItemsAndResponsesToOpportunity(expandableExam, opportunity.getItem(), assessmentItems);


        return opportunity;
    }

    private static void mapExamItemsAndResponsesToOpportunity(final ExpandableExam expandableExam,
                                                              final List<TDSReport.Opportunity.Item> opportunityItems,
                                                              final Map<String, Item> assessmentItems) {

        for (ExamItem examItem : expandableExam.getExamItems()) {
            ExamPage examPage = expandableExam.getExamPages().stream()
                .filter(page -> page.getId().equals(examItem.getExamPageId()))
                .findFirst().get();
            TDSReport.Opportunity.Item opportunityItem = new TDSReport.Opportunity.Item();
            Item assessmentItem = assessmentItems.get(examItem.getItemKey());

            opportunityItem.setPosition(examItem.getPosition());
            // Find the segmentId by joining on the page
            opportunityItem.setSegmentId(examPage.getSegmentId());
            opportunityItem.setBankKey(examItem.getAssessmentItemBankKey());
            opportunityItem.setKey(examItem.getAssessmentItemKey());
//            opportunityItem.setClientId(examItem.getClientId()); TODO: add clientId to ExamItems
            opportunityItem.setOperational(examItem.isFieldTest() ? (short) 0 : 1);
//            opportunityItem.setIsSelected(examItem.isSelected ? (short) 1 : 0); TODO: add isSelected to ExamItem
            opportunityItem.setFormat(examItem.getItemType());

            // If the item response has a score, set it. Otherwise default to -1
            if (examItem.getResponse().isPresent()) {
                ExamItemResponse examItemResponse = examItem.getResponse().get();

//                opportunityItem.setResponseDuration(response.getDuration()); TODO: Get page duration and divide by # of items in page
                ExamItemResponse itemResponse = examItem.getResponse().get();
                TDSReport.Opportunity.Item.Response response = new TDSReport.Opportunity.Item.Response();
                // See ReportingDLL [991] - need to wrap response in CDATA element if MC or MS type
                final String content =
                    (examItem.getItemType().equalsIgnoreCase("MC") || examItem.getItemType().equalsIgnoreCase("MS"))
                        ? "<![CDATA[ " + itemResponse.getResponse() + " ]]>"
                        : itemResponse.getResponse();
                response.setContent(content);
                response.setDate(JaxbMapperUtils.convertInstantToGregorianCalendar(itemResponse.getCreatedAt()));
                opportunityItem.setResponse(response);

                if (examItemResponse.getScore().isPresent()) {
                    ExamItemResponseScore score = examItemResponse.getScore().get();
                    final String scoreString = String.valueOf(score.getScore());
                    final String scoreStatus = score.getScoringStatus().toString();

                    opportunityItem.setScore(scoreString);
                    opportunityItem.setScoreStatus(scoreStatus);

                    ScoreInfoType scoreInfo = new ScoreInfoType();
                    // Add the <ScoreRationale>
                    ScoreInfoType.ScoreRationale rationale = new ScoreInfoType.ScoreRationale();
                    rationale.getContent().add(score.getScoringRationale());
                    scoreInfo.setScoreRationale(rationale);
                    scoreInfo.setScorePoint(scoreString);
//                scoreInfo.setMaxScore(examItem.getMaxScore()); TODO: Add max score to ExamItem
                    if (score.getScoringDimensions().isPresent()) {
                        scoreInfo.setScoreDimension(score.getScoringDimensions().get());
                    }
                    scoreInfo.setScoreStatus(scoreStatus);
                    opportunityItem.setScoreInfo(scoreInfo);
                } else {
                    opportunityItem.setScore(String.valueOf(ITEM_NOT_SCORED_VALUE));
                }
            } else {
                opportunityItem.setScore(String.valueOf(ITEM_NOT_SCORED_VALUE));
            }

            opportunityItem.setAdminDate(JaxbMapperUtils.convertInstantToGregorianCalendar(examPage.getCreatedAt()));
//            opportunityItem.setNumberVisits(examItem.getUpdateCount()); TODO: add updateCount to ExamItem
//            opportunityItem.setMimeType(examItem.getMimeType()); TODO: get MimeType from Assessment Item
            opportunityItem.setStrand(assessmentItem.getStrand());
//            opportunityItem.setContentLevel(examItem.getContentLevel()); TODO: add contentLevel to ExamItem
            opportunityItem.setPageNumber(examPage.getPagePosition());
//            opportunityItem.setPageVisits(examPage.getPageVisits()); TODO: add pageVisits to ExamPage ?
//            opportunityItem.setPageTime(examPage.getPageTime()); TODO: add pageTime ExamPage
//            opportunityItem.setDropped((examItem)); TODO: get notForScoring from itembank
            opportunityItems.add(opportunityItem);
        }
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

    private static void mapExamSegmentsToOpportunity(final List<ExamSegment> examSegments, final List<TDSReport.Opportunity.Segment> opportunitySegments) {
        for (ExamSegment examSegment : examSegments) {
            TDSReport.Opportunity.Segment opportunitySegment = new TDSReport.Opportunity.Segment();
            opportunitySegment.setId(examSegment.getSegmentId());
            opportunitySegment.setPosition((short)examSegment.getSegmentPosition());
            opportunitySegment.setFormKey(examSegment.getFormKey());
            opportunitySegment.setFormId(examSegment.getFormId());
            opportunitySegment.setAlgorithm(examSegment.getAlgorithm().getType());
            opportunitySegment.setAlgorithmVersion(DEFAULT_ALGORITHM_VERSION);
            opportunitySegments.add(opportunitySegment);
        }
    }
}
