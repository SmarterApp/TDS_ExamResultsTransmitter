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

package tds.exam.results.web.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.UUID;

import tds.common.configuration.SecurityConfiguration;
import tds.common.web.advice.ExceptionAdvice;
import tds.exam.results.model.ExamReportStatus;
import tds.exam.results.services.ExamReportAuditService;
import tds.exam.results.services.MessagingService;
import tds.exam.results.services.ScoringValidationStatusService;
import tds.exam.results.tis.TISState;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TISCallbackController.class)
@Import({ExceptionAdvice.class, SecurityConfiguration.class})
public class TISCallbackControllerIntegrationTests {
    @Autowired
    private MockMvc http;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessagingService mockMessagingService;

    @MockBean
    private ExamReportAuditService mockExamReportAuditService;

    @MockBean
    private ScoringValidationStatusService mockScoringValidationStatusService;

    private ObjectWriter ow;

    @Before
    public void setUp() {
        ow = objectMapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    public void shouldSendMessageForNoErrorFound() throws Exception {
        final TISState tisState = new TISState(UUID.randomUUID().toString(), true);
        final UUID examId = UUID.fromString(tisState.getOppKey());

        http.perform(post(new URI("/tis"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(ow.writeValueAsString(tisState)))
            .andExpect(status().isOk());

        verify(mockMessagingService).sendReportAcknowledgement(eq(examId), isA(TISState.class));
        verify(mockExamReportAuditService).updateExamReportStatus(examId, ExamReportStatus.PROCESSED);
        verify(mockScoringValidationStatusService, Mockito.times(0)).updateScoringValidationResults(
            Matchers.any(), Matchers.any());
    }

    @Test
    public void shouldSendMessageForRescoreResult() throws Exception {
        final TISState tisState = new TISState("<TDSReport/>");

        http.perform(post(new URI("/tis?jobid=1234"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(ow.writeValueAsString(tisState)))
            .andExpect(status().isOk());

        verify(mockMessagingService, Mockito.times(0)).sendReportAcknowledgement(Matchers.any(), Matchers.any());
        verify(mockExamReportAuditService, Mockito.times(0)).updateExamReportStatus(Matchers.any(), Matchers.any());
        verify(mockScoringValidationStatusService).updateScoringValidationResults("1234", "<TDSReport/>");
    }

    @Test
    public void shouldNotReportAnythingIfJobIdMissingButTrtPresent() throws Exception {
        // Provide a TRT, do not provide a jobid.
        final TISState tisState = new TISState("<TDSReport/>");

        http.perform(post(new URI("/tis"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(ow.writeValueAsString(tisState)))
            .andExpect(status().isOk());

        // Make sure nothing got called.
        verify(mockMessagingService, Mockito.times(0)).sendReportAcknowledgement(Matchers.any(), Matchers.any());
        verify(mockExamReportAuditService, Mockito.times(0)).updateExamReportStatus(Matchers.any(), Matchers.any());
        verify(mockScoringValidationStatusService, Mockito.times(0)).updateScoringValidationResults(
            Matchers.any(), Matchers.any());
    }

    @Test
    public void shouldNotReportAnythingIfTrtMissingButJobIdPresent() throws Exception {
        // Do not provide a TRT, but do provide a jobid.
        final TISState tisState = new TISState("oppKey", true);

        http.perform(post(new URI("/tis?jobid=1234"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(ow.writeValueAsString(tisState)))
            .andExpect(status().isOk());

        // Make sure nothing got called.
        verify(mockMessagingService, Mockito.times(0)).sendReportAcknowledgement(Matchers.any(), Matchers.any());
        verify(mockExamReportAuditService, Mockito.times(0)).updateExamReportStatus(Matchers.any(), Matchers.any());
        verify(mockScoringValidationStatusService, Mockito.times(0)).updateScoringValidationResults(
            Matchers.any(), Matchers.any());
    }

    @Test
    public void shouldReturn400ForBadExamId() throws Exception {
        final TISState tisState = new TISState("not-a-uuid", true);

        http.perform(post(new URI("/tis"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(ow.writeValueAsString(tisState)))
            .andExpect(status().isBadRequest());

        verify(mockMessagingService, never()).sendReportAcknowledgement(isA(UUID.class), eq(tisState));
    }
}
