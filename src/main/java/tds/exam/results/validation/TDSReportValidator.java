package tds.exam.results.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

import tds.exam.results.trt.TDSReport;

@Component
public class TDSReportValidator {
    private final JAXBContext jaxbContext;
    private final Validator validator;

    @Autowired
    public TDSReportValidator(final JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;

        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File("src/main/xsd/TestResultsTransmissionFormat_Schema.xsd"));
            validator = schema.newValidator();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public void validateReport(final TDSReport results) {
        try {
            JAXBSource source = new JAXBSource(jaxbContext, results);
//             TODO: Enable the validator once all "required" properties are added to Exam/Assessment
//            validator.validate(source);
        } catch (SAXException | JAXBException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
