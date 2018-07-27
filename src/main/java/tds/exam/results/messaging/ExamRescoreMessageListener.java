/***************************************************************************************************
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
 **************************************************************************************************/

package tds.exam.results.messaging;

import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import tds.exam.results.services.ScoringValidationStatusService;
import tds.exam.results.services.TestIntegrationSystemService;
import tds.support.job.JobUpdateRequest;
import tds.support.job.Status;
import tds.support.job.TargetSystem;
import tds.support.job.TestResultsWrapper;
import tds.support.tool.testpackage.configuration.TestPackageObjectMapperConfiguration;
import tds.trt.model.TDSReport;

/**
 * This Messaging listener is responsible for handling Exam re-score messages.
 */
@Component
public class ExamRescoreMessageListener {
    private final static Logger LOG = LoggerFactory.getLogger(ExamRescoreMessageListener.class);

    private final ScoringValidationStatusService scoringValidationStatusService;

    private final TestIntegrationSystemService tisService;

    private final Unmarshaller unmarshaller;

    @Autowired
    public ExamRescoreMessageListener(final ScoringValidationStatusService scoringValidationStatusService,
                                      final TestIntegrationSystemService tisService) throws JAXBException {
        this.scoringValidationStatusService = scoringValidationStatusService;
        this.tisService = tisService;
        JAXBContext context = JAXBContext.newInstance(TestResultsWrapper.class);
        this.unmarshaller = context.createUnmarshaller();
    }

    /**
     * Handle the rescoring of a TRT. This involves updating the support tool job status, and forwarding the re-scored TRT to TIS
     * for exam-level rescoring
     *
     * @param wrapper The TRT wrapper containing the job id and actual {@link tds.trt.model.TDSReport} object
     */
    public void handleMessage(final byte[] wrapper) {
        final TestResultsWrapper testResultsWrapper;
        try {
            testResultsWrapper = (TestResultsWrapper) unmarshaller.unmarshal(new ByteArrayInputStream(wrapper));
            final String examId = testResultsWrapper.getTestResults().getOpportunity().getKey();
            LOG.debug("Received rescored exam notification for id: {}", examId);

            processMessage(examId, testResultsWrapper);
        } catch (JAXBException e) {
            Log.error("Unexpected error processing the rescore message. Could not unmarshall the TRT in the message body", e);
            throw new RuntimeException(e);
        }
    }

    private void processMessage(final String examId, final TestResultsWrapper testResultsWrapper) {
        final JobUpdateRequest request = createJobUpdate(Status.SUCCESS,
            "The TRT was successfully received from the Exam Service. Exam items have been re-scored successfully.");
        scoringValidationStatusService.updateScoringValidationStatus(testResultsWrapper.getJobId(), request);

        final TDSReport report = testResultsWrapper.getTestResults();

        try {
            tisService.sendResults(UUID.fromString(examId), report, UUID.fromString(testResultsWrapper.getJobId()));
            final JobUpdateRequest updateRequest = createJobUpdate(Status.SUCCESS,
                "The TRT was successfully sent to TIS for exam-level rescoring");
            scoringValidationStatusService.updateScoringValidationStatus(testResultsWrapper.getJobId(), updateRequest);
        } catch (final Exception e) {
            LOG.error("An error occured when attempting to send the re-scored TRT to TIS: ", e);
            final JobUpdateRequest jobFailedUpdateRequest = createJobUpdate(Status.FAIL, "The TRT could not be successfully transferred to TIS");
            scoringValidationStatusService.updateScoringValidationStatus(testResultsWrapper.getJobId(), jobFailedUpdateRequest);
            throw e;
        }
    }

    private JobUpdateRequest createJobUpdate(final Status status, final String message) {
        return new JobUpdateRequest("ERT Job Update", TargetSystem.ERT, status, message);
    }
}
