package tds.exam.results.configuration.messaging;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;


public class ExamResultsTransmitterMessageRecoverer extends RejectAndDontRequeueRecoverer {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ExamResultsTransmitterMessageRecoverer.class);

    @Override
    public void recover(final Message message, final Throwable cause) {
        log.warn(String.format("Could not put message to ERT completion queue. message: %s", message), cause);
    }
}
