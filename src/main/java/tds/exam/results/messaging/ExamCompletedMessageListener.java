package tds.exam.results.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;

import tds.exam.results.services.ExamResultsService;
import tds.exam.results.services.TestIntegrationSystemService;
import tds.exam.results.trt.TDSReport;

import java.util.UUID;

/**
 * This Messaging listener is responsible for handling Exam completion messages.
 */
@Component
public class ExamCompletedMessageListener {
    private final static Logger LOG = LoggerFactory.getLogger(ExamCompletedMessageListener.class);

    private final ExamResultsService examResultsService;
    private final TestIntegrationSystemService testIntegrationSystemService;

    @Autowired
    public ExamCompletedMessageListener(final ExamResultsService examResultsService,
                                        final TestIntegrationSystemService testIntegrationSystemService) {
        this.examResultsService = examResultsService;
        this.testIntegrationSystemService = testIntegrationSystemService;
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
        final TDSReport report = examResultsService.findExamResults(UUID.fromString(examId));

        try {
            testIntegrationSystemService.sendResults(UUID.fromString(examId), report);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
