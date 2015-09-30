/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 *
 * @author Jimmy
 */
public class DateTimeConverter implements DynamoDBMarshaller<ZonedDateTime>
{

    private final String datePattern = "dd.MM.yyy hh:mm:ss";
    private final String datePartPattern = "dd.MM.yyy";
     
    private final String timePartPattern = "hh:mm:ss";
    
    @Override
    public String marshall(ZonedDateTime t)
    {
//	return t.format(DateTimeFormatter.ofPattern(datePattern));
	return t.toString();
    }

    @Override
    public ZonedDateTime unmarshall(Class<ZonedDateTime> type, String timeString)
    {
//	LocalDate date = LocalDate.parse(timeString, DateTimeFormatter.ofPattern(datePartPattern));
//	LocalTime time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern(timePartPattern));
	
	return ZonedDateTime.parse(timeString);
    }
}
