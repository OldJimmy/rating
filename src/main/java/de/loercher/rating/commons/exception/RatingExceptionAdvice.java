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

import com.amazonaws.services.simpleworkflow.model.UnknownResourceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *
 * @author Jimmy
 */
@ControllerAdvice
public class RatingExceptionAdvice
{

    private static final Logger log = LoggerFactory.getLogger(RatingExceptionAdvice.class);

    private final ObjectMapper objectMapper;

    @Autowired
    public RatingExceptionAdvice(ObjectMapper pObjectMapper)
    {
	objectMapper = pObjectMapper;
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception
    {
	log.warn("There is an unexpected error: ", e);

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
	result.put("error", "Internal Server Error");
	result.put("message", "Please try again later.");
	result.put("path", req.getRequestURI());

	Timestamp now = new Timestamp(new Date().getTime());
	result.put("timestamp", now);

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = ArticleResourceNotFoundException.class)
    public ResponseEntity<String> resourceNotFoundErrorHandler(HttpServletRequest req, ArticleResourceNotFoundException e) throws Exception
    {
	String articleID = e.getArticleID();
	log.warn("Resource belonging to the articleId " + articleID + " not existing!", e);

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("articleID", articleID);

	result.put("status", HttpStatus.NOT_FOUND.value());
	result.put("error", "Not Found");
	result.put("message", "Resource belonging to articleID " + articleID + " not available.");
	result.put("path", req.getRequestURI());

	Timestamp now = new Timestamp(new Date().getTime());
	result.put("timestamp", now);
	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = RequiredHeaderFieldNotAvailableException.class)
    public ResponseEntity<String> requiredHeaderFieldNotAvailableErrorHandler(HttpServletRequest req, RequiredHeaderFieldNotAvailableException e) throws Exception
    {
	String articleID = e.getArticleID();
	log.warn("No userID carried inside HTTP-header!", e);

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("articleID", articleID);
	result.put("path", req.getRequestURI());

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @ExceptionHandler(value = InappropriateContentException.class)
    public ResponseEntity<String> inappropriateContentErrorHandler(HttpServletRequest req, InappropriateContentException e) throws Exception
    {
	String articleID = e.getArticleID();
	log.warn("Entry with the articleId " + articleID + " isn't appropriate!", e);

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("articleID", articleID);
	result.put("appropriate", false);
	result.put("path", req.getRequestURI());
	result.put("release", e.getRelease());

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @ExceptionHandler(value = UserResourceNotFoundException.class)
    public ResponseEntity<String> userResourceNotFoundErrorHandler(HttpServletRequest req, UserResourceNotFoundException e) throws Exception
    {
	String userID = e.getUserID();
	log.warn("There is no resource belonging to user " + userID + "!", e);

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("userID", userID);
	result.put("appropriate", false);
	result.put("path", req.getRequestURI());

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @ExceptionHandler(value = GeneralRatingException.class)
    public ResponseEntity<String> generalRatingErrorHandler(HttpServletRequest req, GeneralRatingException e) throws Exception
    {
	log.warn("There was a general Rating exception.", e);

	Map<String, Object> result = new LinkedHashMap<>();

	result.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
	result.put("error", "Internal Server Error");
	result.put("message", "There has been an unexpected error. Please try again later.");
	result.put("path", req.getRequestURI());

	result.put("uuid", e.getUuid());
	result.put("timestamp", new Timestamp(e.getTime().getTime()));

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = UnknownResourceException.class)
    public ResponseEntity<String> unknownResourceHandler(HttpServletRequest req, UnknownResourceException e) throws Exception
    {
	log.warn("The following path was unsuccessfully tried to reach: " + req.getRequestURI());

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("status", HttpStatus.NOT_FOUND.value());
	result.put("error", "Not Found");
	result.put("message", "There is no resource behind this URL.");
	result.put("path", req.getRequestURI());

	Timestamp now = new Timestamp(new Date().getTime());
	result.put("timestamp", now);

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.NOT_FOUND);
    }
}
