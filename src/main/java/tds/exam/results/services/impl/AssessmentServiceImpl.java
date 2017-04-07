package tds.exam.results.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import tds.assessment.Assessment;
import tds.common.cache.CacheType;
import tds.exam.results.repositories.AssessmentRepository;
import tds.exam.results.services.AssessmentService;

@Service
public class AssessmentServiceImpl implements AssessmentService {
    private final AssessmentRepository assessmentRepository;

    @Autowired
    public AssessmentServiceImpl(final AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    @Override
    @Cacheable(CacheType.LONG_TERM)
    public Assessment findAssessment(final String clientName, final String key) {
        return assessmentRepository.findAssessment(clientName, key);
    }
}
