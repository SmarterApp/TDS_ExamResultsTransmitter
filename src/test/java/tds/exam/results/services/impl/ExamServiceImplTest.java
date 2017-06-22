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

package tds.exam.results.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import tds.exam.ExamStatusCode;
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

    @Test
    public void shouldUpdateExamStatus() {
        UUID examId = UUID.randomUUID();
        examService.updateStatus(examId, ExamStatusCode.STATUS_SUBMITTED);
        verify(mockExamRepository).updateStatus(examId, ExamStatusCode.STATUS_SUBMITTED);
    }
}
