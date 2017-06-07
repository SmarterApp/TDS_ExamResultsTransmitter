package tds.exam.results.messaging;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBException;
import java.util.UUID;

import tds.exam.ExamStatusCode;
import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.services.ExamResultsService;
import tds.exam.results.services.ExamService;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExamCompletedMessageListenerTest {

    @Mock
    private ExamResultsService mockExamResultsService;

    @Mock
    private ExamService mockExamService;

    @Mock
    private ExamResultsTransmitterServiceProperties mockProperties;

    private ExamCompletedMessageListener listener;

    @Before
    public void setup() {
        listener = new ExamCompletedMessageListener(mockExamResultsService, mockExamService);
    }

    @Test
    public void itShouldGenerateAReportForTheExamId() throws JAXBException {
        final UUID examId = UUID.randomUUID();
        listener.handleMessage(examId.toString());
        verify(mockExamService).updateStatus(examId, ExamStatusCode.STATUS_SUBMITTED);
        verify(mockExamResultsService).findAndSendExamResults(examId);
    }

    @Test(expected = IllegalStateException.class)
    public void itShouldOptionallyRetryOnError() {
        final UUID examId = UUID.randomUUID();

        when(mockProperties.isRetryOnError()).thenReturn(true);
        doThrow(new IllegalStateException("Badness"))
            .when(mockExamService)
            .updateStatus(examId, ExamStatusCode.STATUS_SUBMITTED);

        listener.handleMessage(examId.toString());
    }

    @Test
    public void itShouldOptionallyNotRetryOnError() {
        final UUID examId = UUID.randomUUID();

        when(mockProperties.isRetryOnError()).thenReturn(false);
        doThrow(new IllegalStateException("Badness"))
            .when(mockExamService)
            .updateStatus(examId, ExamStatusCode.STATUS_SUBMITTED);

        listener.handleMessage(examId.toString());
        verifyZeroInteractions(mockExamResultsService);
    }
}