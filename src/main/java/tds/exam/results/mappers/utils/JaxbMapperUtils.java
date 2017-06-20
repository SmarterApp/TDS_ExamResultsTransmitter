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

package tds.exam.results.mappers.utils;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.joda.time.Instant;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;

/**
 * Utility class for mapping performance domain objects to JAXB objects
 */
public class JaxbMapperUtils {
    public static XMLGregorianCalendar convertInstantToGregorianCalendar(final Instant instant) {
        final GregorianCalendar calendar = new GregorianCalendar(instant.getZone().toTimeZone());
        calendar.setTimeInMillis(instant.getMillis());
        return new XMLGregorianCalendarImpl(calendar);
    }
}
