package tds.exam.results.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tds.exam.ExpandableExam;
import tds.exam.results.repositories.ExamRepository;
import tds.exam.results.services.ExamService;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExamServiceImplTest {
    private ExamService examService;

    @Mock
    private ExamRepository mockExamRepository;

    @Before
    public void setup() {
        this.examService = new ExamServiceImpl(mockExamRepository);
    }

    @Test
    public void shouldFindExam() {
        ExpandableExam expandableExam = random(ExpandableExam.class);

        when(mockExamRepository.findExpandableExam(expandableExam.getExam().getId())).thenReturn(expandableExam);
        ExpandableExam retExpandableExam = examService.findExpandableExam(expandableExam.getExam().getId());
        verify(mockExamRepository).findExpandableExam(expandableExam.getExam().getId());

        assertThat(retExpandableExam).isEqualTo(expandableExam);
    }
}
