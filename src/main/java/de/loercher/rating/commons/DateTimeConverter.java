/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.commons;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import java.time.ZonedDateTime;

/**
 *
 * @author Jimmy
 */
public class DateTimeConverter implements DynamoDBMarshaller<ZonedDateTime>
{
    @Override
    public String marshall(ZonedDateTime t)
    {
	return t.toString();
    }

    @Override
    public ZonedDateTime unmarshall(Class<ZonedDateTime> type, String timeString)
    {
	return ZonedDateTime.parse(timeString);
    }
}
