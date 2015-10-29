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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jimmy
 */
@Component
public class RatingProperties
{

    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    private final Properties prop;
    private final SecurityHelper helper;

    @Autowired
    public RatingProperties(SecurityHelper pHelper) 
    {
	helper = pHelper;
	prop = new Properties();

	// Try-with-resource -- never used that before :D
	try (InputStream in = RatingProperties.class.getResourceAsStream("/config/rating.properties"))
	{
	    prop.load(in);
	} catch (IOException ex)
	{
	    log.error("Unexpected error occured on loading file /config/rating.properties. Loading unsuccessful.", ex);
	}

	// load the obfuscated properties from secure.properties by using the obfuscation key
	try (InputStream in = RatingProperties.class.getResourceAsStream("/config/secure.properties"))
	{
	    Properties obfuscatedProperties = new Properties();
	    obfuscatedProperties.load(in);

	    obfuscatedProperties.replaceAll((a, b) -> helper.unobfuscateString((String) b));

	    prop.putAll(obfuscatedProperties);
	} catch (IOException ex)
	{
	    log.error("Unexpected error occured on loading file /config/secure.properties. Loading unsuccessful.", ex);
	}
    }

    public Properties getProp()
    {
	return prop;
    }

}
