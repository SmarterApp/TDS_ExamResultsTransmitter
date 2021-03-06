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

package tds.exam.results.mappers;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import tds.assessment.Assessment;
import tds.assessment.Item;
import tds.assessment.Segment;
import tds.trt.model.TDSReport;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.assertj.core.api.Assertions.assertThat;

public class TestMapperTest {

    @Test
    public void shouldMapAssessmentToTDSReportTestWithSingleGrade() {
        Assessment assessment = random(Assessment.class, "segments");
        Segment segment = random(Segment.class);
        // Bank key should be 187
        segment.setItems(Arrays.asList(new Item("187-1234")));
        assessment.setSegments(Arrays.asList(segment));

        assessment.setGrades(Collections.singletonList("8"));
        assessment.setAcademicYear("2014-2015");
        assessment.setLoadVersion(1234L);
        assessment.setUpdateVersion(4321L);

        TDSReport.Test reportTest = TestMapper.mapTest(assessment);

        assertThat(reportTest).isNotNull();
        assertThat(reportTest.getBankKey()).isEqualTo(187);
        assertThat(reportTest.getName()).isEqualTo(assessment.getKey());
        assertThat(reportTest.getSubject()).isEqualTo(assessment.getSubject());
        assertThat(reportTest.getTestId()).isEqualTo(assessment.getAssessmentId());
        assertThat(reportTest.getMode()).isEqualTo("online");
        assertThat(reportTest.getAcademicYear()).isEqualTo(2015L);
        assertThat(reportTest.getGrade()).isEqualTo("8");
        assertThat(reportTest.getAssessmentVersion()).isEqualTo(String.valueOf(assessment.getUpdateVersion()));
        assertThat(reportTest.getHandScoreProject()).isEqualTo(new Long(assessment.getHandScoreProjectId()));
    }

    @Test
    public void shouldMapAssessmentToTDSReportTestWithBlankAcademicYear() {
        Assessment assessment = random(Assessment.class, "segments");
        Segment segment = random(Segment.class);
        // Bank key should be 187
        segment.setItems(Arrays.asList(new Item("187-1234")));
        assessment.setSegments(Arrays.asList(segment));

        assessment.setGrades(Collections.singletonList("8"));
        assessment.setAcademicYear("");
        assessment.setLoadVersion(1234L);
        assessment.setUpdateVersion(4321L);

        TDSReport.Test reportTest = TestMapper.mapTest(assessment);

        assertThat(reportTest).isNotNull();
        assertThat(reportTest.getAcademicYear()).isEqualTo(0L);
    }

    @Test
    public void shouldMapAssessmentToTDSReportTestWithGradeRangeAndNoUpdateVersion() {
        Assessment assessment = random(Assessment.class, "segments");
        Segment segment = random(Segment.class);
        // Bank key should be 187
        segment.setItems(Arrays.asList(new Item("187-1234")));
        assessment.setSegments(Arrays.asList(segment));

        assessment.setGrades(Arrays.asList("7", "8"));
        assessment.setAcademicYear("2014-2015");
        assessment.setLoadVersion(1234L);
        assessment.setUpdateVersion(null);

        TDSReport.Test reportTest = TestMapper.mapTest(assessment);

        assertThat(reportTest).isNotNull();
        assertThat(reportTest.getBankKey()).isEqualTo(187);
        assertThat(reportTest.getName()).isEqualTo(assessment.getKey());
        assertThat(reportTest.getSubject()).isEqualTo(assessment.getSubject());
        assertThat(reportTest.getTestId()).isEqualTo(assessment.getAssessmentId());
        assertThat(reportTest.getMode()).isEqualTo("online");
        assertThat(reportTest.getAcademicYear()).isEqualTo(2015L);
        assertThat(reportTest.getGrade()).isEqualTo("7-8");
        assertThat(reportTest.getAssessmentVersion()).isEqualTo(String.valueOf(assessment.getLoadVersion()));
        assertThat(reportTest.getHandScoreProject()).isEqualTo(new Long(assessment.getHandScoreProjectId()));
    }

    @Test
    public void shouldMapAssessmentToTDSReportTestWithGradeRangeAndSkippedGrade() {
        Assessment assessment = random(Assessment.class, "segments");
        Segment segment = random(Segment.class);
        // Bank key should be 187
        segment.setItems(Arrays.asList(new Item("187-1234")));
        assessment.setSegments(Arrays.asList(segment));

        assessment.setGrades(Arrays.asList("7", "8", "10"));
        assessment.setAcademicYear("2014");
        assessment.setLoadVersion(1234L);
        assessment.setUpdateVersion(null);

        TDSReport.Test reportTest = TestMapper.mapTest(assessment);

        assertThat(reportTest).isNotNull();
        assertThat(reportTest.getBankKey()).isEqualTo(187);
        assertThat(reportTest.getName()).isEqualTo(assessment.getKey());
        assertThat(reportTest.getAcademicYear()).isEqualTo(2014L);
        assertThat(reportTest.getGrade()).isEqualTo("7, 8, 10");
        assertThat(reportTest.getAssessmentVersion()).isEqualTo(String.valueOf(assessment.getLoadVersion()));
        assertThat(reportTest.getHandScoreProject()).isEqualTo(new Long(assessment.getHandScoreProjectId()));
    }

    @Test
    public void shouldMapAssessmentToTDSReportTestWithsHighSchoolRange() {
        Assessment assessment = random(Assessment.class, "segments");
        Segment segment = random(Segment.class);
        // Bank key should be 187
        segment.setItems(Arrays.asList(new Item("187-1234")));
        assessment.setSegments(Arrays.asList(segment));

        assessment.setGrades(Arrays.asList("9", "10", "12", "11"));
        assessment.setAcademicYear("2014-2015");
        assessment.setLoadVersion(1234L);
        assessment.setUpdateVersion(null);

        TDSReport.Test reportTest = TestMapper.mapTest(assessment);

        assertThat(reportTest).isNotNull();
        assertThat(reportTest.getBankKey()).isEqualTo(187);
        assertThat(reportTest.getName()).isEqualTo(assessment.getKey());
        assertThat(reportTest.getGrade()).isEqualTo("HS");
        assertThat(reportTest.getAssessmentVersion()).isEqualTo(String.valueOf(assessment.getLoadVersion()));
    }

    @Test
    public void shouldMapAssessmentToTDSReportTestWithsNonNumericGrade() {
        Assessment assessment = random(Assessment.class, "segments");
        Segment segment = random(Segment.class);
        // Bank key should be 187
        segment.setItems(Arrays.asList(new Item("187-1234")));
        assessment.setSegments(Arrays.asList(segment));

        assessment.setGrades(Arrays.asList("Kinder", "2", "3"));
        assessment.setAcademicYear("2014-2015");
        assessment.setLoadVersion(1234L);
        assessment.setUpdateVersion(null);

        TDSReport.Test reportTest = TestMapper.mapTest(assessment);

        assertThat(reportTest).isNotNull();
        assertThat(reportTest.getBankKey()).isEqualTo(187);
        assertThat(reportTest.getName()).isEqualTo(assessment.getKey());
        assertThat(reportTest.getGrade()).isEqualTo("Kinder, 2, 3");
        assertThat(reportTest.getAssessmentVersion()).isEqualTo(String.valueOf(assessment.getLoadVersion()));
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowForBeingUnableToParseMalformedBankKey() {
        Assessment assessment = random(Assessment.class, "segments");
        Segment segment = random(Segment.class);
        // Bank key should be 187
        segment.setItems(Arrays.asList(new Item("foo")));
        assessment.setSegments(Arrays.asList(segment));
        TDSReport.Test reportTest = TestMapper.mapTest(assessment);
    }
}
