/*******************************************************************************
 * Copyright 2017 Regents of the University of California. Licensed under the Educational
 * Community License, Version 2.0 (the “license”); you may not use this file except in
 * compliance with the License. You may obtain a copy of the license at
 *
 * https://opensource.org/licenses/ECL-2.0
 *
 * Unless required under applicable law or agreed to in writing, software distributed under the
 * License is distributed in an “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for specific language governing permissions
 * and limitations under the license.
 *
 ******************************************************************************/

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
