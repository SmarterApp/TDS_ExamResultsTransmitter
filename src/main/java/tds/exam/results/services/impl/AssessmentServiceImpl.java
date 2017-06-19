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

package tds.exam.results.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import tds.assessment.Assessment;
import tds.assessment.AssessmentWindow;
import tds.common.cache.CacheType;
import tds.exam.results.repositories.AssessmentRepository;
import tds.exam.results.services.AssessmentService;
import tds.session.ExternalSessionConfiguration;

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

    @Override
    @Cacheable(CacheType.LONG_TERM)
    public List<AssessmentWindow> findAssessmentWindows(final String clientName, final String assessmentId, final boolean guestStudent,
                                                        final ExternalSessionConfiguration configuration) {
        return assessmentRepository.findAssessmentWindows(clientName, assessmentId, guestStudent, configuration);
    }
}
