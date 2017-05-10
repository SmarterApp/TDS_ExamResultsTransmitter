package tds.exam.results.messaging;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBException;
import java.util.UUID;

import tds.exam.ExamStatusCode;
import tds.exam.results.services.ExamResultsService;
import tds.exam.results.services.ExamService;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExamCompletedMessageListenerTest {

    @Mock
    private ExamResultsService mockExamResultsService;

    @Mock
    private ExamService mockExamService;

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
}