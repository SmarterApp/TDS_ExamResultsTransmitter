package tds.exam.results.validation.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;

import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.trt.TDSReport;
import tds.exam.results.validation.TDSReportValidator;

@Component
public class XSDBackedTDSReportValidator implements TDSReportValidator {
    private final JAXBContext jaxbContext;
    private final Validator validator;
    private final ExamResultsTransmitterServiceProperties properties;

    XSDBackedTDSReportValidator(final JAXBContext jaxbContext,
                                @Value("classpath:xsd/TestResultsTransmissionFormat_Schema.xsd") final Resource xsd,
                                final ExamResultsTransmitterServiceProperties properties) throws IOException, SAXException {
        this.jaxbContext = jaxbContext;
        this.properties = properties;
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(xsd.getFile());
        validator = schema.newValidator();
    }

    @Override
    public void validateReport(final TDSReport results) {
        if (!properties.isValidateTrtXml()) {
            return;
        }

        try {
            JAXBSource source = new JAXBSource(jaxbContext, results);
            validator.validate(source);
        } catch (JAXBException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
