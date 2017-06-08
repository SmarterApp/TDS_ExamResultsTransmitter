package tds.exam.results.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tds.assessment.Assessment;
import tds.exam.results.repositories.AssessmentRepository;
import tds.exam.results.services.AssessmentService;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentServiceImplTest {
    private AssessmentService assessmentService;

    @Mock
    private AssessmentRepository mockAssessmentRepository;

    @Before
    public void setup() {
        assessmentService = new AssessmentServiceImpl(mockAssessmentRepository);
    }

    @Test
    public void shouldFetchAssessment() {
        final String clientName = "SBAC";
        final String assessmentKey = "Assessment-key";
        Assessment assessment = random(Assessment.class);

        when(mockAssessmentRepository.findAssessment(clientName, assessmentKey)).thenReturn(assessment);
        Assessment retAssessment = assessmentService.findAssessment(clientName, assessmentKey);
        verify(mockAssessmentRepository).findAssessment(clientName, assessmentKey);
        assertThat(retAssessment).isEqualTo(assessment);
    }
}
