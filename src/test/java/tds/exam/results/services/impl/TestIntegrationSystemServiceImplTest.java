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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.UUID;

import tds.exam.results.repositories.TestIntegrationSystemRepository;
import tds.exam.results.services.TestIntegrationSystemService;
import tds.trt.model.TDSReport;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TestIntegrationSystemServiceImplTest {
    private TestIntegrationSystemService testIntegrationSystemService;

    @Mock
    private TestIntegrationSystemRepository mockTestIntegrationSystemRepository;

    @Mock
    private Marshaller marshaller;

    @Before
    public void setUp() {
        testIntegrationSystemService = new TestIntegrationSystemServiceImpl(mockTestIntegrationSystemRepository, marshaller);
    }

    @Test
    public void shouldSendToTis() throws JAXBException {
        final UUID examId = UUID.randomUUID();
        final TDSReport report = new TDSReport();

        testIntegrationSystemService.sendResults(examId, report);
        verify(mockTestIntegrationSystemRepository).sendResults(eq(examId), any());
    }
}
