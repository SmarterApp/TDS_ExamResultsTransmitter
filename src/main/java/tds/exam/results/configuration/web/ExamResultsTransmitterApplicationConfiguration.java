package tds.exam.results.configuration.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import tds.common.configuration.CacheConfiguration;
import tds.common.configuration.DataSourceConfiguration;
import tds.common.configuration.EventLoggerConfiguration;
import tds.common.configuration.RedisClusterConfiguration;
import tds.common.configuration.RestTemplateConfiguration;
import tds.common.configuration.SecurityConfiguration;
import tds.common.web.advice.ExceptionAdvice;
import tds.exam.results.trt.TDSReport;

/**
 * Configuration for the exam results transmitter microservice
 */
@Configuration
@Import({
    RestTemplateConfiguration.class,
    RedisClusterConfiguration.class,
    CacheConfiguration.class,
    SecurityConfiguration.class,
    DataSourceConfiguration.class,
    EventLoggerConfiguration.class,
    ExceptionAdvice.class
})
public class ExamResultsTransmitterApplicationConfiguration {
    @Bean
    public JAXBContext jaxbContext() throws JAXBException {
        JAXBContext contextObj = JAXBContext.newInstance(TDSReport.class);
        Marshaller marshallerObj = contextObj.createMarshaller();
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return contextObj;
    }

    @Bean
    public Marshaller jaxbMarshaller(final JAXBContext jaxbContext) throws JAXBException {
        return jaxbContext.createMarshaller();
    }
}
