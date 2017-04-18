package tds.exam.results.messaging;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBException;
import java.util.UUID;

import tds.exam.results.services.ExamResultsService;
import tds.exam.results.services.TestIntegrationSystemService;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExamCompletedMessageListenerTest {

    @Mock
    private ExamResultsService mockExamResultsService;

    @Mock
    private TestIntegrationSystemService mockTestIntegrationSystemService;

    private ExamCompletedMessageListener listener;

    @Before
    public void setup() {
        listener = new ExamCompletedMessageListener(mockExamResultsService, mockTestIntegrationSystemService);
    }

    @Test
    public void itShouldGenerateAReportForTheExamId() throws JAXBException {
        final UUID examId = UUID.randomUUID();
        listener.handleMessage(examId.toString());
        verify(mockExamResultsService).findExamResults(examId);
        verify(mockTestIntegrationSystemService).sendResults(eq(examId), any());
    }
}