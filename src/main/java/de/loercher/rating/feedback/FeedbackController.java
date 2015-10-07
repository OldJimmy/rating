/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.google.gson.Gson;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.apache.log4j.Logger;

/**
 *
 * @author Jimmy
 */
public class FeedbackController
{

    private static Logger log = Logger.getLogger(FeedbackController.class);
    private final static Integer MAX_ATTEMPTS = 5;

    private final AmazonDynamoDBClient client = new AmazonDynamoDBClient(new BasicAWSCredentials("AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"));
    private final DynamoDB dynamoDB;
    private final DynamoDBMapper mapper;

    private FlagHandlerFactory factory;

    private TransactionalUpdate obsceneUpdate;
    private TransactionalUpdate positiveUpdate;
    private TransactionalUpdate copyrightUpdate;
    private TransactionalUpdate obsoleteUpdate;

    private final String articleId;

    private ZonedDateTime timeOfPressEntry;

    private Map<String, FeedbackEntryDataModel> ratings = new HashMap<>();

    public FeedbackController(ZonedDateTime time, String pArticleId)
    {
	timeOfPressEntry = time;
	articleId = pArticleId;

	client.setEndpoint("http://localhost:8000");
	dynamoDB = new DynamoDB(client);
	mapper = new DynamoDBMapper(client);

	factory = new FlagHandlerFactory(new DynamoDBConnector());
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
    }

    public void addPositive(boolean positive, String articleID, String userID)
    {
	AtomicUpdate update = factory.createAtomicUpdate("positiveCounter", "size");
	addFlag(articleID, userID, positive, update,(a) -> a.getPositive(), (c, b) -> c.setPositive(b));
    }
    
    public void addObsolete(boolean obsolete, String articleID, String userID)
    {
	AtomicUpdate update = factory.createAtomicUpdate("obsoleteCounter", "size");
	addFlag(articleID, userID, obsolete, update,(a) -> a.getObsolete(), (c, b) -> c.setObsolete(b));
    }
    
    public void addObscene(boolean obscene, String articleID, String userID)
    {
	AtomicUpdate update = factory.createAtomicUpdate("obsceneCounter", "size");
	addFlag(articleID, userID, obscene, update,(a) -> a.getObscene(), (c, b) -> c.setObscene(b));
    }
    
    public void addCopyright(boolean copyright, String articleID, String userID)
    {
	AtomicUpdate update = factory.createAtomicUpdate("copyrightCounter", "size");
	addFlag(articleID, userID, copyright, update, (a) -> a.getCopyright(), (c, b) -> c.setCopyright(b));
    }
    
    public FeedbackDataModel getFeedback(String articleID)
    {
	return mapper.load(FeedbackDataModel.class, articleID);
    }

    private void addFlag(String articleID, String userID, boolean copyright, AtomicUpdate update, Function<FeedbackEntryDataModel, Boolean> pGetter, BiConsumer<FeedbackEntryDataModel, Boolean> pSetter) throws IllegalArgumentException
    {
	RepeatedDynamoDBAction action = new RepeatedDynamoDBAction(mapper, (a) -> a.getCopyright(), (c, b) -> c.setCopyright(b));
	updateDatabaseWithFlag(articleID, userID, action, copyright, update);
    }

    private void updateDatabaseWithFlag(String articleID, String userID, RepeatedDynamoDBAction action, boolean flag, AtomicUpdate update) throws IllegalArgumentException
    {
	Integer entryCounterUpdate = 0;
	
	FeedbackEntryDataModel entry = mapper.load(FeedbackEntryDataModel.class, articleID, userID);
	if (entry == null)
	{
	    entry = new FeedbackEntryDataModel(ZonedDateTime.now(), articleID, userID);
	    entryCounterUpdate = 1;
	}

	try
	{
	    action.saveEntryConditionally(flag, entry, MAX_ATTEMPTS);

	    boolean savedModel = action.getLastFlagValue();

	    if (flag != savedModel)
	    {
		update.updateCounter(articleId, entryCounterUpdate, new FeedbackHelper().calculateAddend(!flag, flag));
	    }

	} catch (AmazonServiceException e)
	{
	    log.error("Updating conditionally FeedbackEntry failed after " + MAX_ATTEMPTS + " attempts.", e);
	}
    }

    public void addPositive(boolean positive, String userID)
    {

    }

    public void addObscene(boolean obscene, String userID)
    {
	FeedbackEntryDataModel rating = ratings.get(userID);

	if (rating == null)
	{
	    rating = new FeedbackEntryDataModel(ZonedDateTime.now(), articleId, userID);
	    ratings.put(userID, rating);
	}

	obsceneUpdate = new TransactionalUpdate(articleId, "obsceneCounter", (a) -> a.getObscene(), (b, c) -> b.setObsolete(c));
	obsceneUpdate.updateFlag(rating, obscene, ratings.size());
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

    public void addRating(FeedbackEntryDataModel rating)
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
