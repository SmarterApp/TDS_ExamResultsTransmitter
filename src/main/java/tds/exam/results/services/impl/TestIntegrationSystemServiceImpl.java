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

package tds.exam.results.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tds.exam.results.repositories.TestIntegrationSystemRepository;
import tds.exam.results.services.TestIntegrationSystemService;
import tds.exam.results.trt.TDSReport;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.UUID;

@Service
public class TestIntegrationSystemServiceImpl implements TestIntegrationSystemService {
    private final Marshaller jaxbMarshaller;
    private final TestIntegrationSystemRepository testIntegrationSystemRepository;

    @Autowired
    public TestIntegrationSystemServiceImpl(final TestIntegrationSystemRepository testIntegrationSystemRepository,
                                            final Marshaller jaxbMarshaller) {
        this.testIntegrationSystemRepository = testIntegrationSystemRepository;
        this.jaxbMarshaller = jaxbMarshaller;
    }

    @Override
    public void sendResults(final UUID examId, final TDSReport report) {
        final StringWriter sw = new StringWriter();
        try {
            jaxbMarshaller.marshal(report, sw);
            final String reportXml = sw.toString();
            testIntegrationSystemRepository.sendResults(examId, reportXml);
        } catch (final JAXBException e) {
            throw new RuntimeException("Failed to marshall TDSReport into XML", e);
        }

    }
}
