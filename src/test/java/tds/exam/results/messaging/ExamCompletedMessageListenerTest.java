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

package tds.exam.results.messaging;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBException;
import java.util.UUID;

import tds.exam.ExamStatusCode;
import tds.exam.results.services.ExamResultsService;
import tds.exam.results.services.ExamService;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExamCompletedMessageListenerTest {

    @Mock
    private ExamResultsService mockExamResultsService;

    @Mock
    private ExamService mockExamService;

    private ExamCompletedMessageListener listener;

    @Before
    public void setup() {
        listener = new ExamCompletedMessageListener(mockExamResultsService, mockExamService);
    }

    @Test
    public void itShouldGenerateAReportForTheExamId() throws JAXBException {
        final UUID examId = UUID.randomUUID();
        listener.handleMessage(examId.toString());
        verify(mockExamResultsService).findAndSendExamResults(examId);
        verify(mockExamService).updateStatus(examId, ExamStatusCode.STATUS_SUBMITTED);

        InOrder inOrder = inOrder(mockExamResultsService, mockExamService);

        inOrder.verify(mockExamResultsService).findAndSendExamResults(examId);
        inOrder.verify(mockExamService).updateStatus(examId, ExamStatusCode.STATUS_SUBMITTED);
    }
}