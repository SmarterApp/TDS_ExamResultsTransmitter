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

package tds.exam.results.web.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import tds.exam.results.model.ExamReportStatus;
import tds.exam.results.services.ExamReportAuditService;
import tds.exam.results.services.MessagingService;
import tds.exam.results.services.ScoringValidationStatusService;
import tds.exam.results.tis.TISState;

@RestController
public class TISCallbackController {
    private static final Logger LOG = LoggerFactory.getLogger(TISCallbackController.class);

    private final MessagingService messagingService;
    private final ExamReportAuditService examReportAuditService;
    private final ScoringValidationStatusService scoringValidationStatusService;

    @Autowired
    public TISCallbackController(final MessagingService messagingService, final ExamReportAuditService examReportAuditService,
                                 ScoringValidationStatusService scoringValidationStatusService) {
        this.messagingService = messagingService;
        this.examReportAuditService = examReportAuditService;
        this.scoringValidationStatusService = scoringValidationStatusService;
    }

    @RequestMapping(value = "/tis", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void tisCallback(@RequestParam(value = "jobid") Optional<String> jobId,
                            @RequestBody final TISState state) {
        LOG.info(String.format("Entered TIS callback. jobId present: %b, state present: %b", jobId.isPresent(), state));
        if (jobId.isPresent() || state.getTrt() != null) {
            if (!jobId.isPresent() || state.getTrt() == null) {
                LOG.error(String.format("Can't report rescore results - jobId or TRT missing. jobId: '%s', TRT: '%s'",
                    jobId, state.getTrt()));
            } else {
                LOG.info(String.format("Reporting rescore results for jobId '%s', TRT len %d", jobId, state.getTrt().length()));
                scoringValidationStatusService.updateScoringValidationResults(jobId.get(), state.getTrt());
            }
        } else {
            UUID examId = UUID.fromString(state.getOppKey());
            messagingService.sendReportAcknowledgement(examId, state);
            /*
            Try catch the exception because TIS has processed the message so we do not want to stop the sending of that to exam
            just to update the status
            */
            try {
                examReportAuditService.updateExamReportStatus(examId, ExamReportStatus.PROCESSED);
            } catch (final Exception e) {
                LOG.error(String.format("Failed to update the report status for %s to processed due to exception", examId), e);
            }
        }
    }
}
