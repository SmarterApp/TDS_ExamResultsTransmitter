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

package tds.exam.results.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
    public void sendReportAcknowledgement(final UUID examId, final TISState state) {
        final String examIdStr = examId.toString();

        if (state.isSuccess() && state.getError() == null) {
            final CorrelationData correlationData = new CorrelationData("exam.reported-" + examIdStr);
            this.rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, TOPIC_EXAM_REPORTED, examIdStr, correlationData);
        } else {
            log.error("There was an error processing the request from TIS for examId {}: {}", examIdStr, state.getError());
        }
    }
}
