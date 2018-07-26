/***************************************************************************************************
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
 **************************************************************************************************/

package tds.exam.results.messaging;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import tds.exam.results.services.ScoringValidationStatusService;
import tds.exam.results.services.TestIntegrationSystemService;
import tds.support.job.JobUpdateRequest;
import tds.support.job.Status;
import tds.support.job.TargetSystem;
import tds.support.job.TestResultsWrapper;
import tds.trt.model.TDSReport;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExamRescoreMessageListenerTest {
    private ExamRescoreMessageListener listener;

    @Mock
    private ScoringValidationStatusService mockScoringValidationStatusService;

    @Mock
    private TestIntegrationSystemService mockTisService;

    private Marshaller marshaller;

    private TDSReport mockTDSReport;

    @Captor
    private ArgumentCaptor<JobUpdateRequest> requestArgumentCaptor;

    @Before
    public void setup() throws JAXBException {
        listener = new ExamRescoreMessageListener(mockScoringValidationStatusService, mockTisService);
        JAXBContext context = JAXBContext.newInstance(TestResultsWrapper.class);
        marshaller = context.createMarshaller();

        mockTDSReport = new TDSReport();
        TDSReport.Opportunity opportunity = new TDSReport.Opportunity();
        opportunity.setKey("253f4dae-71cd-426c-8945-5ba428aad47b");
        mockTDSReport.setOpportunity(opportunity);
    }

    @Test
    public void shouldHandleMessageSuccessfully() throws JAXBException {
        TestResultsWrapper results = new TestResultsWrapper("jobId", mockTDSReport);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        marshaller.marshal(results, stream);

        listener.handleMessage(stream.toByteArray());

        verify(mockScoringValidationStatusService, times(2)).updateScoringValidationStatus(eq(results.getJobId()), requestArgumentCaptor.capture());
        final JobUpdateRequest firstUpdateRequest = requestArgumentCaptor.getAllValues().get(0);
        assertThat(firstUpdateRequest.getStatus()).isEqualTo(Status.SUCCESS);
        assertThat(firstUpdateRequest.getTargetSystem()).isEqualTo(TargetSystem.ERT);
        assertThat(firstUpdateRequest.getDescription()).isEqualTo("The TRT was successfully received from the Exam Service. Exam items have been re-scored successfully.");

        final JobUpdateRequest secondUpdateRequest = requestArgumentCaptor.getAllValues().get(1);
        assertThat(secondUpdateRequest.getStatus()).isEqualTo(Status.SUCCESS);
        assertThat(secondUpdateRequest.getTargetSystem()).isEqualTo(TargetSystem.ERT);
        assertThat(secondUpdateRequest.getDescription()).isEqualTo("The TRT was successfully sent to TIS for exam-level rescoring");

        verify(mockTisService).sendResults(isA(UUID.class), isA(TDSReport.class), isA(String.class));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionForFailureToUnmarshall() {
        listener.handleMessage(new byte[10]);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionForFailedTISPost() throws JAXBException {
        TestResultsWrapper results = new TestResultsWrapper("jobId", mockTDSReport);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        marshaller.marshal(results, stream);

        doThrow(RuntimeException.class).when(mockScoringValidationStatusService).updateScoringValidationStatus(eq("jobId"), isA(JobUpdateRequest.class));

        listener.handleMessage(stream.toByteArray());
    }
}
