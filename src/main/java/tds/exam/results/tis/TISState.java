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

package tds.exam.results.tis;

import static tds.common.util.Preconditions.checkNotNull;

/**
 * An object representing a TIS response from
 */
public class TISState {
    private String oppKey;
    private boolean success;
    private String error;
    private String trt;

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

    public TISState(final String trt) {
        this.trt = trt;
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

    /**
     * @return A string with an error message,
     */
    public String getTrt() {
        return trt;
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
