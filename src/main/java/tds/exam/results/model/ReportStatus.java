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
