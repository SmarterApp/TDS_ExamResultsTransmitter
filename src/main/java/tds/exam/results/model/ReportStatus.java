/* *****************************************************************************
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
package tds.exam.results.model;

public enum ReportStatus {
    RECEIVED("received"),
    SENT("sent"),
    PROCESSED("processed");

    private final String value;

    ReportStatus(String type) {
        this.value = type;
    }

    public String getValue() {
        return value;
    }

    /**
     * @param type the string value for the status
     * @return the equivalent {@link tds.exam.results.model.ReportStatus}
     */
    public static ReportStatus fromValue(String type) {
        for (ReportStatus status : ReportStatus.values()) {
            if (status.getValue().equals(type)) {
                return status;
            }
        }


        throw new IllegalArgumentException(String.format("Could not find ReportStatus for %s", type));
    }
}
