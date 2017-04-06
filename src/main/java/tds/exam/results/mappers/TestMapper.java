package tds.exam.results.mappers;

import tds.assessment.Assessment;
import tds.exam.results.trt.TDSReport;

/**
 * A class used for mapping a {@link tds.exam.results.trt.TDSReport.Test} object from {@link tds.assessment.Assessment} data
 */
public class TestMapper {
    private final static String TEST_MODE_ONLINE = "online";

    public static TDSReport.Test mapTest(final Assessment assessment) {
        //TODO: Populate this assessment data
        TDSReport.Test test = new TDSReport.Test();
        test.setName(assessment.getKey());
        test.setSubject(assessment.getSubject());
        test.setTestId(assessment.getAssessmentId());
        // Simply select the first bank key from any item (See ReportingDLL line [1321])
        final Long bankKey = assessment.getSegments().get(0).getItems().stream()
            .map(item -> Long.parseLong(parseBankKeyFromId(item.getId())))
            .findFirst().get();

        test.setBankKey(bankKey);
//        test.setHandScoreProject(assessment.getHandScoredProject());
//        test.setContract(assessment.getContract());
        test.setMode(TEST_MODE_ONLINE);
//        test.setGrade(assessment.getGrade());
//        test.setAssessmentType(assessment.getType());
//        test.setAcademicYear(assessment.getAcademicYear());
//        test.setAssessmentVersion(assessment.getVersion());
        return test;
    }

    private static String parseBankKeyFromId(final String itemId) {
        if (!itemId.contains("-")) {
            throw new IllegalArgumentException(String.format("Could not parse bank key out of the itemId '%s'.", itemId));
        }

        return itemId.split("-")[0];
    }
}
