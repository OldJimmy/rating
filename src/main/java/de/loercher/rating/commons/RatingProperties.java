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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jimmy
 */
@Component
public class RatingProperties
{

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final String PROP_FILE = "/config/rating.properties";
    private final String SECURE_FILE = "/config/secure.properties";

    private final String PROP_FILE_ENDING = "rating.properties";
    private final String SECURE_FILE_ENDING = "secure.properties";

    private final Properties prop;
    private final SecurityHelper helper;

    @Autowired
    public RatingProperties(SecurityHelper pHelper, ApplicationArguments args)
    {
	helper = pHelper;
	prop = new Properties();

	List<String> propertyFileOptions = args.getOptionValues("props");

	String propertyFile = PROP_FILE;
	String securePropertyFile = SECURE_FILE;
	if (!(propertyFileOptions == null) && !propertyFileOptions.isEmpty())
	{
	    propertyFile = propertyFileOptions.get(0) + "\\" + PROP_FILE_ENDING;
	    securePropertyFile = propertyFileOptions.get(0) + "\\" + SECURE_FILE_ENDING;
	}

	propertyFile = propertyFile.replace("\\", "/");
	securePropertyFile = securePropertyFile.replace("\\", "/");

	initProperties(propertyFile, securePropertyFile);
    }

    public RatingProperties(SecurityHelper pHelper)
    {
	helper = pHelper;
	prop = new Properties();

	initProperties(PROP_FILE, SECURE_FILE);
    }

    private void initProperties(String propertyFile, String securePropertyFile)
    {
	InputStream in1 = RatingProperties.class.getResourceAsStream(propertyFile);
	try
	{
	    prop.load(in1);
	} catch (Exception ex)
	{
	    InputStream hin = null;
	    try
	    {
		hin = new FileInputStream(propertyFile);
		prop.load(hin);
	    } catch (FileNotFoundException ex1)
	    {
		log.error("Unexpected error occured on loading file /config/rating.properties. Loading unsuccessful.", ex1);
	    } catch (IOException ex1)
	    {
		log.error("Unexpected error occured on loading file /config/rating.properties. Loading unsuccessful.", ex1);
	    } finally
	    {
		if (hin != null)
		{
		    try
		    {
			hin.close();
		    } catch (IOException ex1)
		    {
			log.error("Unexpected error occured on loading file /config/rating.properties. Loading unsuccessful.", ex1);
		    }
		}
	    }
	} finally
	{
	    try
	    {
		if (in1 != null)
		{
		    in1.close();
		}
	    } catch (IOException ex)
	    {
		log.error("Unexpected error occured on loading file /config/rating.properties. Loading unsuccessful.", ex);
	    }
	}

	// load the obfuscated properties from secure.properties by using the obfuscation key
	InputStream in = RatingProperties.class.getResourceAsStream(securePropertyFile);
	try
	{
	    Properties obfuscatedProperties = new Properties();
	    obfuscatedProperties.load(in);

	    obfuscatedProperties.replaceAll((a, b) -> helper.unobfuscateString((String) b));

	    prop.putAll(obfuscatedProperties);
	} catch (Exception ex)
	{
	    InputStream hin = null;
	    try
	    {
		hin = new FileInputStream(securePropertyFile);
		Properties obfuscatedProperties = new Properties();
		obfuscatedProperties.load(hin);

		obfuscatedProperties.replaceAll((a, b) -> helper.unobfuscateString((String) b));

		prop.putAll(obfuscatedProperties);
	    } catch (FileNotFoundException ex1)
	    {
		log.error("Unexpected error occured on loading file /config/rating.properties. Loading unsuccessful.", ex);
	    } catch (IOException ex1)
	    {
		log.error("Unexpected error occured on loading file /config/rating.properties. Loading unsuccessful.", ex);
	    } finally
	    {
		if (hin != null)
		{
		    try
		    {
			hin.close();
		    } catch (IOException ex1)
		    {
			log.error("Unexpected error occured on loading file /config/rating.properties. Loading unsuccessful.", ex1);
		    }
		}
	    }
	} finally
	{
	    try
	    {
		if (in1 != null)
		{
		    in1.close();
		}
	    } catch (IOException ex)
	    {
		log.error("Unexpected error occured on loading file /config/rating.properties. Loading unsuccessful.", ex);
	    }
	}

	log.info("Imported Properties: ");
	for (Object key : prop.keySet())
	{
	    log.info(key + ": " + prop.get(key));
	}
    }

    public Properties getProp()
    {
	return prop;
    }

}
