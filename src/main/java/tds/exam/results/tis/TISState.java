package tds.exam.results.tis;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An object representing a TIS response from
 */
public class TISState {
    private String examId;
    private boolean success;
    private String error;

    /**
     * Empty constructor for frameworks
     */
    private TISState() {}

    public TISState(Builder builder) {
        this.examId = checkNotNull(builder.examId);
        this.success = builder.success;
        this.error = builder.error;
    }

    public static final class Builder {
        private String examId;
        private boolean success;
        private String error;

        public Builder() {
        }

        public Builder withExamId(String examId) {
            this.examId = examId;
            return this;
        }

        public Builder withSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public Builder withError(String error) {
            this.error = error;
            return this;
        }

        public TISState build() {
            return new TISState(this);
        }
    }

    /**
     * @return The id of the exam that was reported
     */
    @JsonProperty("oppKey")
    public String getExamId() {
        return examId;
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
}
