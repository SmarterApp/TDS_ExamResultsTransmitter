package tds.exam.results.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import tds.assessment.Assessment;
import tds.assessment.AssessmentWindow;
import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.repositories.AssessmentRepository;
import tds.session.ExternalSessionConfiguration;

import static tds.exam.results.configuration.SupportApplicationConfiguration.ASSESSMENT_APP_CONTEXT;

@Repository
public class RemoteAssessmentRepository implements AssessmentRepository {
    private final RestTemplate restTemplate;
    private final ExamResultsTransmitterServiceProperties properties;

    @Autowired
    public RemoteAssessmentRepository(final RestTemplate restTemplate,
                                      final ExamResultsTransmitterServiceProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public Assessment findAssessment(final String clientName, final String key) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> requestHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<Assessment> responseEntity;

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(String.format("%s/%s/assessments/%s", properties.getAssessmentUrl(), clientName, key));

        responseEntity = restTemplate.exchange(
            builder.build().toUri(),
            HttpMethod.GET,
            requestHttpEntity,
            new ParameterizedTypeReference<Assessment>() {
            });

        return responseEntity.getBody();
    }

    @Override
    public List<AssessmentWindow> findAssessmentWindows(final String clientName,
                                                        final String assessmentId,
                                                        final long studentId,
                                                        final ExternalSessionConfiguration configuration) {
        UriComponentsBuilder builder =
            UriComponentsBuilder
                .fromHttpUrl(String.format("%s/%s/%s/%s/windows/student/%d",
                    properties.getAssessmentUrl(),
                    clientName,
                    ASSESSMENT_APP_CONTEXT,
                    assessmentId,
                    studentId));

        builder.queryParam("shiftWindowStart", configuration.getShiftWindowStart());
        builder.queryParam("shiftWindowEnd", configuration.getShiftWindowEnd());
        builder.queryParam("shiftFormStart", configuration.getShiftFormStart());
        builder.queryParam("shiftFormEnd", configuration.getShiftFormEnd());

        ResponseEntity<List<AssessmentWindow>> responseEntity = restTemplate.exchange(builder.build().toUri(),
            HttpMethod.GET, null, new ParameterizedTypeReference<List<AssessmentWindow>>() {
            });

        return responseEntity.getBody();
    }
}
