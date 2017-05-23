package tds.exam.results.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tds.exam.ExamStatusCode;
import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.services.ExamResultsService;
import tds.exam.results.services.ExamService;

import java.util.UUID;

/**
 * This Messaging listener is responsible for handling Exam completion messages.
 */
@Component
public class ExamCompletedMessageListener {
    private final static Logger LOG = LoggerFactory.getLogger(ExamCompletedMessageListener.class);

    private final ExamResultsService examResultsService;

    private final ExamService examService;

    private final ExamResultsTransmitterServiceProperties properties;

    @Autowired
    public ExamCompletedMessageListener(final ExamResultsService examResultsService,
                                        final ExamService examService,
                                        final ExamResultsTransmitterServiceProperties properties) {
        this.examResultsService = examResultsService;
        this.examService = examService;
        this.properties = properties;
    }

    /**
     * Handle the completion of an exam.
     * This method is an external entry-point called when we receive a new exam completion
     * message from our message broker.
     *
     * NOTE: If this method throws an exception, the message will not be acknowledged, causing
     * the same examId to be resubmitted to this method.
     *
     * @param examId The completed exam id
     */
    public void handleMessage(final String examId) {
        LOG.debug("Received completed exam notification for id: {}", examId);
        if (properties.isRetryOnError()) {
            processMessage(examId);
        } else {
            try {
                processMessage(examId);
            } catch (final Exception e) {
                // Log error, and do not rethrow to prevent ERT from re-processing this same request
                LOG.error("Error occurred while processing or sending the exam results for examId {}: {}", examId,
                    e);
            }
        }

    }

    private void processMessage(final String examId) {
        // Once this ERT message has been received, the exam status is "submitted"
        final UUID id = UUID.fromString(examId);
        examService.updateStatus(id, ExamStatusCode.STATUS_SUBMITTED);
        examResultsService.findAndSendExamResults(id);
    }
}
