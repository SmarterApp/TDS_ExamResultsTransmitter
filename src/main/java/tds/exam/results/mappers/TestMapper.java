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


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import tds.assessment.Assessment;
import tds.exam.results.trt.TDSReport;

/**
 * A class used for mapping a {@link tds.exam.results.trt.TDSReport.Test} object from {@link tds.assessment.Assessment} data
 */
public class TestMapper {
    private final static Logger LOG = LoggerFactory.getLogger(TestMapper.class);
    private final static String TEST_MODE_ONLINE = "online";
    private static final String HIGH_SCHOOL_GRADE_STRING = "HS";
    private static final int MIN_HIGH_SCHOOL_GRADE = 9;
    private static final int MAX_HIGH_SCHOOL_GRADE = 12;
    private static final int HIGH_SCHOOL_GRADE_RANGE_LENGTH = 4;

    public static TDSReport.Test mapTest(final Assessment assessment) {
        TDSReport.Test test = new TDSReport.Test();
        test.setName(assessment.getKey());
        test.setSubject(assessment.getSubject());
        test.setTestId(assessment.getAssessmentId());
        // Simply select the first bank key from any item (See ReportingDLL line [1321])
        final Long bankKey = assessment.getSegments().get(0).getItems().stream()
            .map(item -> Long.parseLong(parseBankKeyFromId(item.getId())))
            .findFirst().get();

        test.setBankKey(bankKey);
        test.setHandScoreProject(assessment.getHandScoreProjectId() != null
            ? Long.valueOf(assessment.getHandScoreProjectId())
            : null);
        test.setContract(assessment.getContract());
        test.setMode(TEST_MODE_ONLINE);
        test.setGrade(createGradeStringFromGrades(assessment.getGrades()));
        test.setAssessmentType(assessment.getType());
        test.setAcademicYear(parseLatestAcademicYear(assessment.getAcademicYear(), assessment.getKey()));
        test.setAssessmentVersion(assessment.getUpdateVersion() == null
            ? String.valueOf(assessment.getLoadVersion())
            : String.valueOf(assessment.getUpdateVersion()));

        return test;
    }

    private static String parseBankKeyFromId(final String itemId) {
        if (!itemId.contains("-")) {
            throw new IllegalArgumentException(String.format("Could not parse bank key out of the itemId '%s'.", itemId));
        }

        return itemId.split("-")[0];
    }

    /* Rule: If a date range, select the latest year */
    private static long parseLatestAcademicYear(final String academicYearString, final String key) {
        if (academicYearString.equals("")) {
            // If the configs.tbltestadmin table is not configured with an academic year, set this value to 0
            LOG.warn("The academic year for the assessment was not set in the configuration database for assessment {}. Defaulting to '0'.",
                key);
            return 0;
        }
        // If this is a range date, pick the latest year
        else if (academicYearString.contains("-")) {
            return Long.parseLong(academicYearString.split("-")[1]);
        }

        return Long.parseLong(academicYearString);
    }

    /**
     * Creates the grade string from the list of grades.
     *
     * Rules:
     *      If no grades are present, return an empty string
     *      If a single grade is present, return it
     *      If the grades is a contiguous range, hyphenate it (i.e. 7, 8, 9 =  7-9)
     *      If the grade range is a standard high school range (i.e. 9-12), return "HS"
     *      Otherwise, return the list of grades comma delimited, in ascending order.
     *      If non-numeric grades are present, they should be listed first
     *
     * @param grades The list of grade strings
     * @return The properly formatted grade string
     */
    private static String createGradeStringFromGrades(final List<String> grades) {
        /* This logic corresponds to logic found in legacy - ReportingDLL.TestGradeSpan_F(), line 1353 */
        if (grades.size() == 0) {
            return StringUtils.EMPTY;
        } else if (grades.size() == 1) {
            return grades.get(0);
        }

        int minGrade = Integer.MAX_VALUE;
        int maxGrade = Integer.MIN_VALUE;
        List<Integer> intGrades = new ArrayList<>();

        for (String grade : grades) {
            if (StringUtils.isNumeric(grade)) {
                int g = Integer.parseInt(grade);
                minGrade = Math.min(minGrade, g);
                maxGrade = Math.max(maxGrade, g);
                intGrades.add(g);
            }
        }

        final String gradeStr;

        if (intGrades.size() == grades.size()) {
            // If its 9 - 12, "HS" for High School
            if (minGrade == MIN_HIGH_SCHOOL_GRADE && maxGrade == MAX_HIGH_SCHOOL_GRADE && grades.size() == HIGH_SCHOOL_GRADE_RANGE_LENGTH) {
                gradeStr = HIGH_SCHOOL_GRADE_STRING;
            } else if (maxGrade - minGrade + 1 == intGrades.size()) {
                gradeStr = minGrade + "-" + maxGrade;
            } else {
                gradeStr = StringUtils.join(intGrades, ", ");
            }
        } else {
            gradeStr = StringUtils.join(grades, ", ");
        }

        return gradeStr;
    }
}
