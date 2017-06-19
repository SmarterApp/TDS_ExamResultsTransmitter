/*******************************************************************************
 * Copyright 2016 Smarter Balance Licensed under the
 *     Educational Community License, Version 2.0 (the "License"); you may
 *     not use this file except in compliance with the License. You may
 *     obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an "AS IS"
 *     BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *     or implied. See the License for the specific language governing
 *     permissions and limitations under the License.
 ******************************************************************************/

package tds.exam.results.messaging;

import com.esotericsoftware.minlog.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

import tds.exam.ExamStatusCode;
import tds.exam.results.services.ExamResultsService;
import tds.exam.results.services.ExamService;

/**
 * This Messaging listener is responsible for handling Exam completion messages.
 */
@Component
public class ExamCompletedMessageListener {
    private final static Logger LOG = LoggerFactory.getLogger(ExamCompletedMessageListener.class);

    private final ExamResultsService examResultsService;

    private final ExamService examService;

    @Autowired
    public ExamCompletedMessageListener(final ExamResultsService examResultsService,
                                        final ExamService examService) {
        this.examResultsService = examResultsService;
        this.examService = examService;
    }

    /**
     * Handle the completion of an exam.
     * This method is an external entry-point called when we receive a new exam completion
     * message from our message broker.
     *
     * NOTE: If this method throws an exception, the message will be retried the specified number of times (as dictated
     * by the {@code retry-amount} property value).  If the number of retries is exceeded, the message will be
     * acknowledged, logged and discarded.  See {@link tds.exam.results.configuration.messaging.ExamResultsTransmitterMessagingConfiguration}
     * for details on how the retry/recovery is configured.
     *
     * @param examId The completed exam id
     */
    public void handleMessage(final String examId) {
        LOG.debug("Received completed exam notification for id: {}", examId);

        try{
            processMessage(examId);
        } catch (Exception e) {
            Log.error(String.format("Unexpected error sending TRT for exam %s", examId), e);
            throw e;
        }
    }

    private void processMessage(final String examId) {
        // Once this ERT message has been received, the exam status is "submitted"
        final UUID id = UUID.fromString(examId);
        examResultsService.findAndSendExamResults(id);
        examService.updateStatus(id, ExamStatusCode.STATUS_SUBMITTED);
    }
}
