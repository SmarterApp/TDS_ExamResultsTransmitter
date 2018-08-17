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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.Optional;
import java.util.UUID;

import tds.exam.results.repositories.TestIntegrationSystemRepository;
import tds.exam.results.services.TestIntegrationSystemService;
import tds.trt.model.TDSReport;

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
    public void sendResults(final UUID examId, final TDSReport report, final String rescoreJobId) {
        sendResults(examId, report, Optional.of(rescoreJobId));
    }

    @Override
    public void sendResults(final UUID examId, final TDSReport report) {
        sendResults(examId, report, Optional.empty());
    }

    private void sendResults(final UUID examId, final TDSReport report, final Optional<String> rescoreJobId) {
        final StringWriter sw = new StringWriter();
        try {
            rescoreJobId.ifPresent(id -> {
                report.getOpportunity().setOppId("0");
                report.getOpportunity().getScore().clear();
            });
            jaxbMarshaller.marshal(report, sw);
            final String reportXml = sw.toString();
            testIntegrationSystemRepository.sendResults(examId, reportXml, rescoreJobId);
        } catch (final JAXBException e) {
            throw new RuntimeException("Failed to marshall TDSReport into XML", e);
        }

    }
}
