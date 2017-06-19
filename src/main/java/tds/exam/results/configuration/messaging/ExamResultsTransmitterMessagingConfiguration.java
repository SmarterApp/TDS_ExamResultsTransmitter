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

package tds.exam.results.configuration.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.messaging.ExamCompletedMessageListener;

import static tds.exam.ExamTopics.TOPIC_EXAM_COMPLETED;
import static tds.exam.ExamTopics.TOPIC_EXCHANGE;

/**
 * This configuration is responsible for initializing AMQP (RabbitMQ)
 */
@Configuration
public class ExamResultsTransmitterMessagingConfiguration {
    private final static String QUEUE_EXAM_COMPLETION = "exam_completion_results_transmitter_queue";

    @Bean
    public TopicExchange examTopicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public Queue examCompletionQueue() {
        return new Queue(QUEUE_EXAM_COMPLETION, true);
    }

    @Bean
    public Binding examCompletionBinding(@Qualifier("examCompletionQueue") final Queue queue,
                                         @Qualifier("examTopicExchange") final TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(TOPIC_EXAM_COMPLETED);
    }

    @Bean
    public SimpleMessageListenerContainer examCompletionListenerContainer(final ConnectionFactory connectionFactory,
                                                                          final ExamCompletedMessageListener listener,
                                                                          final ExamResultsTransmitterServiceProperties properties) {
        final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_EXAM_COMPLETION);
        container.setMessageListener(new MessageListenerAdapter(listener, "handleMessage"));
        container.setAdviceChain(RetryInterceptorBuilder.stateless()
            .maxAttempts(properties.getRetryAmount())
            .recoverer(new ExamResultsTransmitterMessageRecoverer())
            .backOffOptions(properties.getRetryInitialInterval(), properties.getRetryIntervalMultiplier(), properties.getRetryMaxInterval())
            .build());
        return container;
    }
}
