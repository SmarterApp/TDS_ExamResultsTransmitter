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

package tds.exam.results.configuration.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.Comparator;

import tds.common.configuration.*;
import tds.common.web.advice.ExceptionAdvice;
import tds.exam.results.trt.TDSReport;

/**
 * Configuration for the exam results transmitter microservice
 */
@Configuration
@Import({
    RestTemplateConfiguration.class,
    RedisClusterConfiguration.class,
    CacheConfiguration.class,
    SecurityConfiguration.class,
    DataSourceConfiguration.class,
    EventLoggerConfiguration.class,
    ExceptionAdvice.class
})
public class ExamResultsTransmitterApplicationConfiguration {

    public static class MarshalListener extends Marshaller.Listener {
        // TDS-1626: ExamineeAttribute nodes must come before ExamineeRelationships for TIS to parse it.
        private class ExamineeChildComparator implements Comparator<Object> {
            @Override
            public int compare(Object a, Object b) {
                return a.getClass().getName().compareTo(b.getClass().getName());
            }
        }

        @Override
        public void beforeMarshal(Object source) {
            if (source instanceof TDSReport.Examinee) {
                ((TDSReport.Examinee) source).getExamineeAttributeOrExamineeRelationship().sort(
                    new ExamineeChildComparator());
            }
        }
    }

    // Exposed as public static so it can be invoked from unit tests.
    public static Marshaller createMarshaller(JAXBContext contextObj) throws JAXBException {
        Marshaller marshallerObj = contextObj.createMarshaller();
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshallerObj.setListener(new ExamResultsTransmitterApplicationConfiguration.MarshalListener());
        return marshallerObj;
    }

    @Bean
    public JAXBContext jaxbContext() throws JAXBException {
        JAXBContext contextObj = JAXBContext.newInstance(TDSReport.class);
        createMarshaller(contextObj);
        return contextObj;
    }

    @Bean
    public Marshaller jaxbMarshaller(final JAXBContext jaxbContext) throws JAXBException {
        return jaxbContext.createMarshaller();
    }
}
