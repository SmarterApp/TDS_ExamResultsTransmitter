package tds.exam.results.configuration.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import tds.common.configuration.RestTemplateConfiguration;
import tds.common.configuration.SecurityConfiguration;

/**
 * Configuration for the exam results transmitter microservice
 */
@Configuration
@Import({
    RestTemplateConfiguration.class,
    SecurityConfiguration.class
})
public class ExamResultsTransmitterApplicationConfiguration {
}
