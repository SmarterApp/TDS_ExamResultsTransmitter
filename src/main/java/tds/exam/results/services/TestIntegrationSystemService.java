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

package tds.exam.results.services;

import java.util.UUID;

import tds.trt.model.TDSReport;

/**
 * Service for interacting with an instance of the Test Integration System
 */
public interface TestIntegrationSystemService {
    /**
     * Sends the exam TRT results to the Test Integration System
     *
     * @param examId The exam id of the TRT report
     * @param report The {@link tds.exam.results.trt.TDSReport} TRT jaxb object
     */
    void sendResults(final UUID examId, final TDSReport report);

    void sendResults(final UUID examId, final TDSReport report, final String rescoreJobId);
}
