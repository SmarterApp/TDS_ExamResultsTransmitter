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

package tds.exam.results.repositories;

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
     */
    void sendResults(final UUID examId, final String results);
}
