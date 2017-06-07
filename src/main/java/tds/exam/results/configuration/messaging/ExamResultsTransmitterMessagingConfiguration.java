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
                                                                          final ExamCompletedMessageListener listener) {
        final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_EXAM_COMPLETION);
        container.setAdviceChain(RetryInterceptorBuilder
            .stateless()
            .maxAttempts(3)
            .backOffOptions(1000, 2, 5000)
            .recoverer(new ExamResultsTransmitterMessageRecoverer())
            .build());
        container.setMessageListener(new MessageListenerAdapter(listener, "handleMessage"));
        return container;
    }
}
