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

import java.util.UUID;

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
            .queryParam("expandableAttribute", ExpandableExamAttributes.EXAM_SEGMENTS)
            .queryParam("expandableAttribute", ExpandableExamAttributes.EXAM_ACCOMMODATIONS)
            .queryParam("expandableAttribute", ExpandableExamAttributes.EXAM_NOTES)
            .queryParam("expandableAttribute", ExpandableExamAttributes.EXAMINEE_ATTRIBUTES_AND_RELATIONSHIPS)
            .queryParam("expandableAttribute", ExpandableExamAttributes.EXAM_PAGE_AND_ITEMS)
            .queryParam("expandableAttribute", ExpandableExamAttributes.EXAM_STATUS_DATES);


        responseEntity = restTemplate.exchange(
            builder.build().toUri(),
            HttpMethod.GET,
            requestHttpEntity,
            new ParameterizedTypeReference<ExpandableExam>() {
            });

        return responseEntity.getBody();
    }
}
