package tds.exam.results.configuration.messaging;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;

/**
 * Handles the message after it has been rejected the required number of times.
 */
public class ExamResultsTransmitterMessageRecoverer extends RejectAndDontRequeueRecoverer {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ExamResultsTransmitterMessageRecoverer.class);

    @Override
    public void recover(final Message message, final Throwable cause) {
        log.error(String.format("Unable to send TRT for completed exam to TIS with message: %s", message), cause);
    }
}
