/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

import com.google.gson.Gson;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Jimmy
 */
public class Feedback
{

    private static Logger log = Logger.getLogger(Feedback.class);

    private TransactionalUpdate obsceneUpdate;
    private TransactionalUpdate positiveUpdate;
    private TransactionalUpdate copyrightUpdate;
    private TransactionalUpdate obsoleteUpdate;

    private final String articleId;

//    private Integer positiveCounter = 0;
//    private Integer obsoleteCounter = 0;
//    private Integer obsceneCounter = 0;
//    private Integer copyrightCounter = 0; 
    
    private ZonedDateTime timeOfPressEntry;

    private Map<String, FeedbackEntry> ratings = new HashMap<>();

    public Feedback(ZonedDateTime time, String pArticleId)
    {
	timeOfPressEntry = time;
	articleId = pArticleId;
    }

    public ZonedDateTime getTimeOfPressEntry()
    {
	return timeOfPressEntry;
    }

    public void setTimeOfPressEntry(ZonedDateTime timeOfPressEntry)
    {
	this.timeOfPressEntry = timeOfPressEntry;
    }

    public void addObsolete(boolean obsolete, String userID)
    {
	FeedbackEntry rating = ratings.get(userID);
	
	if (rating == null)
	{
	    rating = new FeedbackEntry(ZonedDateTime.now(), articleId, userID);
	    ratings.put(userID, rating);
	}

	obsoleteUpdate = new TransactionalUpdate(articleId, "obsoleteCounter", (a) -> a.getObsolete(), (b, c) -> b.setObsolete(c));
	obsoleteUpdate.updateFlag(rating, obsolete, ratings.size());
    }

    public void addPositive(boolean positive, String userID)
    {
//	FeedbackEntry rating;
//	if (ratings.containsKey(userID))
//	{
//	    rating = ratings.get(userID);
//	    boolean oldPositive = ratings.get(userID).getPositive();
//
//	    positiveCounter = updateCounterFromDuplicate(positiveCounter, oldPositive, positive);
//	} else
//	{
//	    rating = addNewRatingToRatings(userID);
//	    if (positive)
//	    {
//		positiveCounter++;
//	    }
//	}
//
//	rating.setPositive(positive);
    }

    public void addObscene(boolean obscene, String userID)
    {
//	FeedbackEntry rating;
//	if (ratings.containsKey(userID))
//	{
//	    rating = ratings.get(userID);
//	    boolean oldObscene = rating.getObscene();
//
//	    obsceneCounter = updateCounterFromDuplicate(obsceneCounter, oldObscene, obscene);
//	} else
//	{
//	    rating = addNewRatingToRatings(userID);
//	    if (obscene)
//	    {
//		obsceneCounter++;
//	    }
//	}
//
//	rating.setObscene(obscene);
    }

    public void addCopyright(boolean copyright, String userID)
    {
//	FeedbackEntry rating;
//	if (ratings.containsKey(userID))
//	{
//	    rating = ratings.get(userID);
//	    boolean oldCopyright = rating.getCopyright();
//
//	    copyrightCounter = updateCounterFromDuplicate(copyrightCounter, oldCopyright, copyright);
//	} else
//	{
//	    rating = addNewRatingToRatings(userID);
//	    if (copyright)
//	    {
//		copyrightCounter++;
//	    }
//	}
//
//	rating.setCopyright(copyright);
    }

    public Integer getPositiveCounter()
    {
	return positiveUpdate.getCounter();
    }

    public Integer getObsoleteCounter()
    {
	return obsoleteUpdate.getCounter();
    }

    public Integer getObsceneCounter()
    {
	return obsceneUpdate.getCounter();
    }

    public Integer getCopyrightCounter()
    {
	return copyrightUpdate.getCounter();
    }

    public void addRating(FeedbackEntry rating)
    {
	String userID = rating.getUserID();

	addCopyright(rating.getCopyright(), userID);
	addObscene(rating.getObscene(), userID);
	addObsolete(rating.getObsolete(), userID);
	addPositive(rating.getPositive(), userID);
    }

    public Integer getRatingCount()
    {
	return ratings.size();
    }

    private Integer updateCounterFromDuplicate(Integer counter, boolean oldEntry, boolean newEntry)
    {
	if (newEntry)
	{
	    if (!oldEntry)
	    {
		counter++;
	    }
	} else
	{
	    if (oldEntry)
	    {
		counter--;
	    }
	}

	return counter;
    }

//    private FeedbackEntry addNewRatingToRatings(String userID)
//    {
//	FeedbackEntry rating = new FeedbackEntry(ZonedDateTime.now(), dao.getArticleID(), userID);
//	ratings.put(userID, rating);
//
//	return rating;
//    }

    public String toJSON()
    {
	Gson gson = new Gson();
	return gson.toJson(this);
    }

    public void fromJSON(String json)
    {
	Gson gson = new Gson();
	ratings = gson.fromJson(json, Map.class);
    }
}
