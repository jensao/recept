package org.hopto.eriksen.core;

import static io.dropwizard.testing.FixtureHelpers.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.dropwizard.jackson.Jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CourseTest {

	private static ObjectMapper MAPPER = Jackson.newObjectMapper();

	@BeforeClass
	public void initialize() {
		MAPPER.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
	
    @Test
    public void testSerializesToJSON() throws Exception {
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
		// The java.util.Date is in local time zone, the jackson MAPPER is in UTC, so set the parser to UTC as well
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    	String dateInString = "2015-05-21 22:02:03";
    	Date date = sdf.parse(dateInString);
        
        final Course course = new Course("test", "comment", date, null);
        course.setCourseId(1);
        
        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(fixture("fixtures/course.json"), Course.class));
        System.out.println("The putput from MAPPER: " + expected);
        Assert.assertEquals(MAPPER.writeValueAsString(course), expected);
    }
    
    @Test
    public void testEqual() {
    	Course course1 = new Course("equal", "Doesnt mather", new Date(1L), null);
    	Course course2 = new Course("equal", "different", new Date(3L), null);
    	Assert.assertTrue(course1.equals(course2), "The bussines rule say that only the title shall be equel to treat two Courses as equel");
    }
}
