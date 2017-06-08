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
