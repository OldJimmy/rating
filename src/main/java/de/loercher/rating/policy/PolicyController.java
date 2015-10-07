/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.policy;

import de.loercher.rating.feedback.FeedbackController;
import de.loercher.rating.feedback.FeedbackDataModel;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Jimmy
 */
@RestController
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

    @Autowired
    public PolicyController(FeedbackController pFeedback)
    {
	feedback = pFeedback;
    }

    // TODO: Should return HTTP status code 451 in the case the rating is not legit anymore
    @RequestMapping(value = "/rating/{articleId}", produces = "application/json") 
    public Double getRating(@PathVariable String articleId) throws InappropriateContentException
    {
	FeedbackDataModel model = feedback.getFeedback(articleId);
	if (!isLegit(model)) throw new InappropriateContentException("Entry not appropriate to display");

	ZonedDateTime currentTime = ZonedDateTime.now();

	Duration timeDifference = Duration.between(model.getTimeOfPressEntry(), currentTime);

	Double potency = (double) timeDifference.toMinutes() / 60;
	log.info("Time difference between actual time and construction time (in hours): " + potency);

	potency = potency / SINKINGTIMEINHOURS;
	log.info("Parameters used for calculation by using time difference: ");
	log.info("Potency: " + potency);

	Double factor = Math.pow(SINKINGFACTOR, potency);
	log.info("Factor: " + factor);
	
	Integer effectivePositive = model.getPositiveCounter() - model.getWrongCounter();

	return effectivePositive * factor;
    }

    private boolean isLegit(FeedbackDataModel model)
    {
	Integer entryCount = model.getSize();
	if (entryCount <= 0) return true;
	Double doubledEntryCount = new Double(entryCount);

	if ((model.getObsceneCounter() / doubledEntryCount) > OBSCENEPERCENTAGETHRESHOLD)
	{
	    log.info("Obscene threshold exceeded!");
	    return false;
	}

	if ((model.getObsoleteCounter() / doubledEntryCount) > OBSOLETEPERCENTAGETHRESHOLD)
	{
	    log.info("Obsolete threshold exceeded!");
	    return false;
	}

	if ((model.getCopyrightCounter() / doubledEntryCount) > COPYRIGHTPERCENTAGETHRESHOLD)
	{
	    log.info("Copyright threshold exceeded!");
	    return false;

	}
	return true;
    }

}
