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

package tds.exam.results.tis;

import static tds.common.util.Preconditions.checkNotNull;

/**
 * An object representing a TIS response from
 */
public class TISState {
    private String oppKey;
    private boolean success;
    private String error;

    /**
     * Empty constructor for frameworks
     */
    private TISState() {}

    public TISState(final String oppKey, final boolean success, final String error) {
        this.oppKey = checkNotNull(oppKey);
        this.success = success;
        this.error = error;
    }

    public TISState(final String oppKey, final boolean success) {
        this.oppKey = oppKey;
        this.success = success;
    }

    /**
     * @return The id of the exam that was reported
     */
    public String getOppKey() {
        return oppKey;
    }

    /**
     * @return A flag indicating whether or not the TIS report request was successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return A string with an error message,
     */
    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "TISState{" +
            "oppKey='" + oppKey + '\'' +
            ", success=" + success +
            ", error='" + error + '\'' +
            '}';
    }
}
