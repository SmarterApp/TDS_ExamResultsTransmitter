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

package tds.exam.results.repositories;

import java.util.Optional;
import java.util.UUID;

/**
 * A repository for sending TRT reports to the remote Test Integration System
 */
public interface TestIntegrationSystemRepository {
    /**
     * Sends the TRT results XML to the Test Integration System
     *
     * @param examId  The exam id of the TRT
     * @param results The marshalled TRT XML blob
     * @param rescoreJobId Support tool job id associated with rescoring this TRT
     */
    void sendResults(final UUID examId, final String results, final Optional<UUID> rescoreJobId);
}
