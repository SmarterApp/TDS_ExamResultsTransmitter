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
