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

package tds.exam.results.validation.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;

import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.trt.model.TDSReport;
import tds.exam.results.validation.TDSReportValidator;

@Component
public class XSDBackedTDSReportValidator implements TDSReportValidator {
    private final JAXBContext jaxbContext;
    private final Validator validator;
    private final ExamResultsTransmitterServiceProperties properties;

    XSDBackedTDSReportValidator(final JAXBContext jaxbContext,
                                @Value("classpath:/xsd/TestResultsTransmissionFormat_Schema.xsd") final Resource xsd,
                                final ExamResultsTransmitterServiceProperties properties) throws IOException, SAXException {
        this.jaxbContext = jaxbContext;
        this.properties = properties;
        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try (final InputStream xsdStream = xsd.getInputStream()) {
            final Schema schema = sf.newSchema(new StreamSource(xsdStream));
            validator = schema.newValidator();
        }
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
