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

package tds.exam.results.services;

import java.util.UUID;

import tds.exam.results.tis.TISState;

/**
 * A service used for AMPQ messaging
 */
public interface MessagingService {
    /**
     * Sends a message to the exam report queue acknowledging that a response was received from TIS
     *
     * @param examId The id of the exam to report
     * @param state  The TIS response object
     */
    void sendReportAcknowledgement(final UUID examId, final TISState state);
}
