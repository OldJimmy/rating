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
package de.loercher.rating.commons.exception;

import java.sql.Timestamp;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 *
 * @author Jimmy
 */
@Component
public class RatingHttpHandlerExceptionResolver implements HandlerExceptionResolver, Ordered
{

    private static final Logger log = LoggerFactory.getLogger(RatingHttpHandlerExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest hsr, HttpServletResponse hsr1, Object o, Exception excptn)
    {
	if (excptn instanceof HttpRequestMethodNotSupportedException)
	{
	    HttpRequestMethodNotSupportedException e = (HttpRequestMethodNotSupportedException) excptn;

	    log.warn("There was a method not allowed exception on the resource: " + hsr.getRequestURI() + ". Tried Method: " + e.getMethod() + ".");

	    ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
	    mv.addObject("status", HttpStatus.METHOD_NOT_ALLOWED.value());
	    mv.addObject("error", "Method not allowed.");
	    mv.addObject("message", excptn.getLocalizedMessage());
	    mv.addObject("path", hsr.getRequestURI());

	    mv.addObject("timestamp", new Timestamp(new Date().getTime()));

	    hsr1.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());

	    return mv;
	}

	if (excptn instanceof HttpMessageNotReadableException)
	{
	    log.warn("There was a request to a resource with bad syntax: " + hsr.getRequestURI());

	    ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
	    mv.addObject("status", HttpStatus.BAD_REQUEST.value());
	    mv.addObject("error", "Bad Request.");
	    mv.addObject("message", "There was a fault in your request syntax. Please revalidate.");
	    mv.addObject("path", hsr.getRequestURI());
	    
	    mv.addObject("timestamp", new Timestamp(new Date().getTime()));

	    hsr1.setStatus(HttpStatus.BAD_REQUEST.value());

	    return mv;
	}

	return null;
    }

    @Override
    public int getOrder()
    {
	return Integer.MIN_VALUE;
    }

}
