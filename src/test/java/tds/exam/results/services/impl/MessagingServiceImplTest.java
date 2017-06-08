package tds.exam.results.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

import java.util.UUID;

import tds.exam.results.services.MessagingService;
import tds.exam.results.tis.TISState;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
        final TISState tisState = new TISState(UUID.randomUUID().toString(), true);
        final UUID examId = UUID.fromString(tisState.getOppKey());

        messagingService.sendReportAcknowledgement(examId, tisState);
        verify(mockRabbitTemplate).convertAndSend(eq(TOPIC_EXCHANGE), eq(TOPIC_EXAM_REPORTED), eq(tisState.getOppKey()), isA(CorrelationData.class));
    }

    @Test
    public void shouldNotSendMessageIfUnsuccessful() {
        final TISState tisState = new TISState(UUID.randomUUID().toString(), false);
        final UUID examId = UUID.fromString(tisState.getOppKey());

        messagingService.sendReportAcknowledgement(examId, tisState);
        verify(mockRabbitTemplate, never()).convertAndSend(eq(TOPIC_EXCHANGE), eq(TOPIC_EXAM_REPORTED), eq(tisState.getOppKey()), isA(CorrelationData.class));
    }

    @Test
    public void shouldNotSendMessageIfErrorPresent() {
        final TISState tisState = new TISState(UUID.randomUUID().toString(), false, "Whoops");
        final UUID examId = UUID.fromString(tisState.getOppKey());

        messagingService.sendReportAcknowledgement(examId, tisState);
        verify(mockRabbitTemplate, never()).convertAndSend(eq(TOPIC_EXCHANGE), eq(TOPIC_EXAM_REPORTED), eq(tisState.getOppKey()), isA(CorrelationData.class));
    }
}
