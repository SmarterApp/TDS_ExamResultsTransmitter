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

package tds.exam.results.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import tds.exam.ExpandableExam;
import tds.exam.results.repositories.ExamRepository;
import tds.exam.results.services.ExamService;

@Service
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;

    @Autowired
    public ExamServiceImpl(final ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public ExpandableExam findExpandableExam(final UUID examId) {
        return examRepository.findExpandableExam(examId);
    }

    @Override
    public void updateStatus(final UUID examId, final String status) {
        examRepository.updateStatus(examId, status);
    }
}
