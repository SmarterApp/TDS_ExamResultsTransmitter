package tds.exam.results.web.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import tds.exam.results.services.MessagingService;
import tds.exam.results.tis.TISState;

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
    private MessagingService messagingService;

    private ObjectWriter ow;

    @Before
    public void setUp() {
        ow = objectMapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    public void shouldSendMessageForNoErrorFound() throws Exception {
        final TISState tisState = new TISState.Builder()
            .withExamId(UUID.randomUUID().toString())
            .withSuccess(true)
            .build();

        http.perform(post(new URI("/tis"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(ow.writeValueAsString(tisState)))
            .andExpect(status().isOk());

        verify(messagingService).sendReportAcknowledgement(tisState);
    }


    @Test
    public void shouldReturn400ForBadExamId() throws Exception {
        final TISState tisState = new TISState.Builder()
            .withExamId("not-a-uuid")
            .withSuccess(true)
            .build();

        http.perform(post(new URI("/tis"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(ow.writeValueAsString(tisState)))
            .andExpect(status().isBadRequest());

        verify(messagingService, never()).sendReportAcknowledgement(tisState);
    }
}
