package tds.exam.results.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;
import org.omg.CORBA.Object;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import tds.exam.results.services.MessagingService;
import tds.exam.results.tis.TISState;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static tds.exam.ExamTopics.TOPIC_EXAM_REPORTED;
import static tds.exam.ExamTopics.TOPIC_EXCHANGE;

@RunWith(MockitoJUnitRunner.class)
public class MessagingServiceImplTest {
    private MessagingService messagingService;

    @Mock
    private RabbitTemplate mockRabbitTemplate;

    @Before
    public void setup() {
        messagingService = new MessagingServiceImpl(mockRabbitTemplate);
    }

    @Test
    public void shouldSendMessageToReportQueue() {
        final TISState tisState = new TISState.Builder()
            .withExamId(UUID.randomUUID().toString())
            .withSuccess(true)
            .build();
        final UUID examId = UUID.fromString(tisState.getExamId());

        messagingService.sendReportAcknowledgement(examId, tisState);
        verify(mockRabbitTemplate).convertAndSend(eq(TOPIC_EXCHANGE), eq(TOPIC_EXAM_REPORTED), eq(tisState.getExamId()), isA(CorrelationData.class));
    }

    @Test
    public void shouldNotSendMessageIfUnsuccessful() {
        final TISState tisState = new TISState.Builder()
            .withExamId(UUID.randomUUID().toString())
            .withSuccess(false)
            .build();
        final UUID examId = UUID.fromString(tisState.getExamId());

        messagingService.sendReportAcknowledgement(examId, tisState);
        verify(mockRabbitTemplate, never()).convertAndSend(eq(TOPIC_EXCHANGE), eq(TOPIC_EXAM_REPORTED), eq(tisState.getExamId()), isA(CorrelationData.class));
    }

    @Test
    public void shouldNotSendMessageIfErrorPresent() {
        final TISState tisState = new TISState.Builder()
            .withExamId(UUID.randomUUID().toString())
            .withSuccess(true)
            .withError("Whoopsies!")
            .build();
        final UUID examId = UUID.fromString(tisState.getExamId());

        messagingService.sendReportAcknowledgement(examId, tisState);
        verify(mockRabbitTemplate, never()).convertAndSend(eq(TOPIC_EXCHANGE), eq(TOPIC_EXAM_REPORTED), eq(tisState.getExamId()), isA(CorrelationData.class));
    }

    @Test(expected = AmqpException.class)
    public void shouldRetryThreeTimesThenLogFailure() {
        final TISState tisState = new TISState.Builder()
            .withExamId(UUID.randomUUID().toString())
            .withSuccess(true)
            .build();
        final UUID examId = UUID.fromString(tisState.getExamId());

        doThrow(new AmqpException("unit test exception"))
            .when(mockRabbitTemplate).convertAndSend(eq(TOPIC_EXCHANGE),
            eq(TOPIC_EXAM_REPORTED),
            eq(tisState.getExamId()),
            isA(CorrelationData.class));

        messagingService.sendReportAcknowledgement(examId, tisState);
        verify(mockRabbitTemplate, times(5)).convertAndSend(eq(TOPIC_EXCHANGE),
            eq(TOPIC_EXAM_REPORTED),
            eq(tisState.getExamId()),
            isA(CorrelationData.class));
    }
}
