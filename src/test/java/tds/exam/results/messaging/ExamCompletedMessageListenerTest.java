package tds.exam.results.messaging;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBException;
import java.util.UUID;

import tds.exam.results.services.ExamResultsService;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExamCompletedMessageListenerTest {

    @Mock
    private ExamResultsService mockExamResultsService;

    private ExamCompletedMessageListener listener;

    @Before
    public void setup() {
        listener = new ExamCompletedMessageListener(mockExamResultsService);
    }

    @Test
    public void itShouldGenerateAReportForTheExamId() throws JAXBException {
        final UUID examId = UUID.randomUUID();
        listener.handleMessage(examId.toString());
        verify(mockExamResultsService).findAndSendExamResults(examId);
    }
}