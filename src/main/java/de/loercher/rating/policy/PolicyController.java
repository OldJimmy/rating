/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.policy;

import de.loercher.rating.commons.InappropriateContentException;
import de.loercher.rating.commons.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.loercher.rating.feedback.FeedbackController;
import de.loercher.rating.feedback.FeedbackDataModel;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Jimmy
 */
@RestController
@RequestMapping("localpress/")
public class PolicyController
{

    private static final Logger log = Logger.getLogger(PolicyController.class);

    /*
     Example calculation: in 72 hours (3 days) the rating goes down 50% with the following parameters:
     SINKINGTIMEINHOURS = 72.0
     SINKINGFACTOR = 0.5
     */
    private static final Double SINKINGTIMEINHOURS = 72.0;
    private static final Double SINKINGFACTOR = 0.5;

    private static final Double OBSOLETEPERCENTAGETHRESHOLD = 0.1;
    private static final Double OBSCENEPERCENTAGETHRESHOLD = 0.1;
    private static final Double COPYRIGHTPERCENTAGETHRESHOLD = 0.1;

    private final FeedbackController feedback;
    private final ObjectMapper mapper;

    @Autowired
    public PolicyController(FeedbackController pFeedback, ObjectMapper pMapper)
    {
	feedback = pFeedback;
	mapper = pMapper;
    }

    @RequestMapping(value = "/{articleId}/rating", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRating(@PathVariable String articleId) throws JsonProcessingException
    {
	Map<String, Object> result = new HashMap<>();
	result.put("articleID", articleId);
	try
	{
	    Double rating = calculateRating(articleId);

	    result.put("appropriate", true);
	    result.put("rating", rating);

	    return new ResponseEntity<>(mapper.writeValueAsString(result), HttpStatus.OK);
	} catch (ResourceNotFoundException ex)
	{
	    log.warn("Rating entry with the articleId " + articleId + " not existing!", ex);
	    
	    Timestamp now = new Timestamp(new Date().getTime());
	    result.put("timestamp", now);
	    result.put("status", 404);
	    result.put("error", "Not Found");
	    
	    return new ResponseEntity<>(mapper.writeValueAsString(result), HttpStatus.NOT_FOUND);
	} catch (InappropriateContentException ex)
	{
	    log.warn("Entry with the articleId " + articleId + " isn't appropriate!", ex);

	    result.put("appropriate", false);
	    return new ResponseEntity<>(mapper.writeValueAsString(result), HttpStatus.OK);
	}
    }

    public Double calculateRating(String articleID) throws InappropriateContentException, ResourceNotFoundException
    {
	FeedbackDataModel model = feedback.getFeedback(articleID);
	if (model == null)
	{
	    throw new ResourceNotFoundException("Entry with the articleId " + articleID + " not existing!");
	}

	if (!isLegit(model))
	{
	    throw new InappropriateContentException("Entry not appropriate to display");
	}

	ZonedDateTime currentTime = ZonedDateTime.now();

	Duration timeDifference = Duration.between(model.getTimeOfPressEntry(), currentTime);

	Double potency = (double) timeDifference.toMinutes() / 60;
	log.info("Time difference between actual time and construction time (in hours): " + potency);

	potency = potency / SINKINGTIMEINHOURS;
	log.info("Parameters used for calculation by using time difference: ");
	log.info("Potency: " + potency);

	Double factor = Math.pow(SINKINGFACTOR, potency);
	log.info("Factor: " + factor);

	// Wrongness is classified as opposite to positive. 
	// This is not correct in reality but appropriate for the rating algorithm.
	Integer effectivePositive = model.getPositiveCounter() - model.getWrongCounter();

	return effectivePositive * factor;
    }

    private boolean isLegit(FeedbackDataModel model)
    {
	Integer entryCount = model.getSize();
	String articleID = model.getArticleID();

	if (entryCount <= 0)
	{
	    return true;
	}

	Double doubledEntryCount = new Double(entryCount);

	if ((model.getObsceneCounter() / doubledEntryCount) > OBSCENEPERCENTAGETHRESHOLD)
	{
	    log.info("Obscene threshold exceeded for Article: " + articleID + " (" + model.getObsceneCounter() + ")");
	    return false;
	}

	if ((model.getCopyrightCounter() / doubledEntryCount) > COPYRIGHTPERCENTAGETHRESHOLD)
	{
	    log.info("Copyright threshold exceeded for Article: " + articleID + " (" + model.getCopyrightCounter() + ")");
	    return false;
	}

	if ((model.getObsoleteCounter() / doubledEntryCount) > OBSOLETEPERCENTAGETHRESHOLD)
	{
	    log.info("Obsolete threshold exceeded for Article: " + articleID + " (" + model.getObsoleteCounter() + ")");
	    return false;
	}

	return true;
    }

}
