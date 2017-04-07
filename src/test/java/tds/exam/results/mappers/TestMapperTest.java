package tds.exam.results.mappers;

import org.junit.Test;

import java.util.Arrays;

import tds.assessment.Assessment;
import tds.assessment.Item;
import tds.assessment.Segment;
import tds.exam.results.trt.TDSReport;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.assertj.core.api.Assertions.assertThat;

public class TestMapperTest {

    @Test
    public void shouldMapAssessmentToTDSReportTest() {
        Assessment assessment = random(Assessment.class, "segments");
        Segment segment = random(Segment.class);
        // Bank key should be 187
        segment.setItems(Arrays.asList(new Item("187-1234")));
        assessment.setSegments(Arrays.asList(segment));

        TDSReport.Test reportTest = TestMapper.mapTest(assessment);

        assertThat(reportTest).isNotNull();
        assertThat(reportTest.getBankKey()).isEqualTo(187);
        assertThat(reportTest.getName()).isEqualTo(assessment.getKey());
        assertThat(reportTest.getSubject()).isEqualTo(assessment.getSubject());
        assertThat(reportTest.getTestId()).isEqualTo(assessment.getAssessmentId());
        assertThat(reportTest.getMode()).isEqualTo("online");
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
