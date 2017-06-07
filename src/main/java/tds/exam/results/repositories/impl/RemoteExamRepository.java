package tds.exam.results.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import tds.common.web.resources.NoContentResponseResource;
import tds.exam.ExamStatusCode;
import tds.exam.ExamStatusRequest;
import tds.exam.ExpandableExam;
import tds.exam.ExpandableExamAttributes;
import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.repositories.ExamRepository;

import static tds.exam.results.configuration.SupportApplicationConfiguration.EXAM_APP_CONTEXT;

@Repository
public class RemoteExamRepository implements ExamRepository {
    private final RestTemplate restTemplate;
    private final ExamResultsTransmitterServiceProperties properties;

    @Autowired
    public RemoteExamRepository(final RestTemplate restTemplate,
                                final ExamResultsTransmitterServiceProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public ExpandableExam findExpandableExam(final UUID examId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> requestHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<ExpandableExam> responseEntity;

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(String.format("%s/%s/%s/expandable",
                properties.getExamUrl(),
                EXAM_APP_CONTEXT,
                examId))
            .queryParam("expandableAttribute", ExpandableExamAttributes.EXAM_ACCOMMODATIONS)
            .queryParam("expandableAttribute", ExpandableExamAttributes.EXAM_NOTES)
            .queryParam("expandableAttribute", ExpandableExamAttributes.EXAMINEE_ATTRIBUTES_AND_RELATIONSHIPS)
            .queryParam("expandableAttribute", ExpandableExamAttributes.EXAM_STATUS_DATES)
            .queryParam("expandableAttribute", ExpandableExamAttributes.WINDOW_ATTEMPTS)
            .queryParam("expandableAttribute", ExpandableExamAttributes.ITEM_RESPONSE_UPDATES)
            .queryParam("expandableAttribute", ExpandableExamAttributes.EXAM_SEGMENT_WRAPPERS);


        try {
            responseEntity = restTemplate.exchange(
                builder.build().toUri(),
                HttpMethod.GET,
                requestHttpEntity,
                new ParameterizedTypeReference<ExpandableExam>() {
                });
        } catch (final HttpStatusCodeException e) {
            throw new RuntimeException(String.format("Unable to find expandable exam: %s", e.getResponseBodyAsString()), e);
        }

        return responseEntity.getBody();
    }

    @Override
    public void updateStatus(final UUID examId, final String status) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ExamStatusRequest request = new ExamStatusRequest(new ExamStatusCode(status), "ExamResultsTransmitter");

        HttpEntity<?> requestHttpEntity = new HttpEntity<>(request, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(String.format("%s/%s/%s/status",
          properties.getExamUrl(), EXAM_APP_CONTEXT, examId));

        try {
            restTemplate.exchange(
                builder.build().toUri(),
                HttpMethod.PUT,
                requestHttpEntity,
                new ParameterizedTypeReference<NoContentResponseResource>() {
                });
        } catch (final HttpStatusCodeException e) {
            throw new RuntimeException(String.format("Unable to update exam status: %s", e.getResponseBodyAsString()), e);
        }
    }
}
