/*
 * Copyright 2015 Pivotal Software, Inc..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.loercher.rating.commons;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author Jimmy
 */
public class GeneralRatingException extends Exception
{
    /** 
     * With the following fields the maintenance team can match errors to logging entries.
     */
    private String uuid;
    private Date time;
    private String error;

    public GeneralRatingException(String pError, Throwable e)
    {
	super(e);
	
	uuid = UUID.randomUUID().toString();
	time= new Date();
	error = pError;
    }
    
    public String getError()
    {
	return error;
    }

    public void setError(String error)
    {
	this.error = error;
    }

    public Date getTime()
    {
	return time;
    }

    public void setTime(Date time)
    {
	this.time = time;
    }

    public String getUuid()
    {
	return uuid;
    }

    public void setUuid(String uuid)
    {
	this.uuid = uuid;
    }
    
    public String getLoggingString()
    {
	Timestamp stamp = new Timestamp(time.getTime());
	return stamp + ": " + uuid + ": " + error;
    }
}
