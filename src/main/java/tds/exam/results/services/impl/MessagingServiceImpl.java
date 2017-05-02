package tds.exam.results.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tds.exam.results.services.MessagingService;
import tds.exam.results.tis.TISState;

import static tds.exam.ExamTopics.TOPIC_EXAM_REPORTED;
import static tds.exam.ExamTopics.TOPIC_EXCHANGE;

@Service
public class MessagingServiceImpl implements MessagingService {
    private static final Logger log = LoggerFactory.getLogger(MessagingServiceImpl.class);
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessagingServiceImpl(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendReportAcknowledgement(final TISState state) {
        final String examId = state.getExamId();

        if (state.isSuccess() && state.getError() == null) {
            final CorrelationData correlationData = new CorrelationData("exam.reported-" + examId);
            this.rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, TOPIC_EXAM_REPORTED, examId, correlationData);
        } else {
            log.error("There was an error processing the request from TIS for examId {}: {}", examId, state.getError());
        }
    }
}
