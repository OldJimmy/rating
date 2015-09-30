/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.policy;

import de.loercher.rating.feedback.Feedback;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Jimmy
 */
@RestController
public class Policy
{

    private static Logger log = Logger.getLogger(Policy.class);

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

    private Feedback feedback;

    public Feedback getFeedback()
    {
	return feedback;
    }

    public void setFeedback(Feedback feedback)
    {
	this.feedback = feedback;
    }

    @RequestMapping("/rating")
    public Double getRating()
    {
	ZonedDateTime actualTime = ZonedDateTime.now();

	Duration timeDifference = Duration.between(feedback.getTimeOfPressEntry(), actualTime);

	Double potency = (double) timeDifference.toMinutes() / 60;
	log.info("Time difference between actual time and construction time (in hours): " + potency);

	potency = potency / SINKINGTIMEINHOURS;
	log.info("Parameters used for calculation by using time difference: ");
	log.info("Potency: " + potency);

	Double factor = Math.pow(SINKINGFACTOR, potency);
	log.info("Factor: " + factor);

	return feedback.getPositiveCounter() * factor;
    }

    public boolean isLegit()
    {
	Integer entryCount = feedback.getRatingCount();

	if ((feedback.getObsceneCounter() / entryCount) > OBSCENEPERCENTAGETHRESHOLD)
	{
	    log.info("Obscene threshold exceeded!");
	    return false;
	}
	
	if ((feedback.getObsoleteCounter() / entryCount) > OBSOLETEPERCENTAGETHRESHOLD)
	{
	    log.info("Obsolete threshold exceeded!");
	    return false;
	}

	if ((feedback.getCopyrightCounter() / entryCount) > COPYRIGHTPERCENTAGETHRESHOLD)
	{
	    log.info("Copyright threshold exceeded!");
	    return false;

	}
	return true;
    }

}
