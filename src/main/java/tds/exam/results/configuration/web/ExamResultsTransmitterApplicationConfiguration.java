package tds.exam.results.configuration.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import tds.common.configuration.CacheConfiguration;
import tds.common.configuration.DataSourceConfiguration;
import tds.common.configuration.RestTemplateConfiguration;
import tds.common.configuration.SecurityConfiguration;
import tds.exam.results.trt.TDSReport;

/**
 * Configuration for the exam results transmitter microservice
 */
@Configuration
@Import({
    CacheConfiguration.class,
    RestTemplateConfiguration.class,
    SecurityConfiguration.class,
    DataSourceConfiguration.class
})
public class ExamResultsTransmitterApplicationConfiguration {
    @Bean
    public JAXBContext jaxbContext() throws JAXBException {
        JAXBContext contextObj = JAXBContext.newInstance(TDSReport.class);
        Marshaller marshallerObj = contextObj.createMarshaller();
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return contextObj;
    }
}
