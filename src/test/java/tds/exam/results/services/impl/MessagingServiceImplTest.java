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
