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
import java.time.ZonedDateTime;
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
    private final DynamoDBMapper mapper;

    private final FlagHandlerFactory factory;

    public FeedbackController()
    {
	client.setEndpoint("http://localhost:8000");
	mapper = new DynamoDBMapper(client);

	factory = new FlagHandlerFactory(new DynamoDBConnector());
    }

    public FeedbackDataModel getFeedback(String articleID)
    {
	return mapper.load(FeedbackDataModel.class, articleID);
    }

    public void addPositive(boolean positive, String articleID, String userID)
    {
	AtomicUpdate update = factory.createAtomicUpdate("positiveCounter", "size");
	addFlag(articleID, userID, positive, update,(a) -> a.isPositive(), (c, b) -> c.setPositive(b));
    }
    
    public void addObsolete(boolean obsolete, String articleID, String userID)
    {
	AtomicUpdate update = factory.createAtomicUpdate("obsoleteCounter", "size");
	addFlag(articleID, userID, obsolete, update,(a) -> a.isObsolete(), (c, b) -> c.setObsolete(b));
    }
    
    public void addObscene(boolean obscene, String articleID, String userID)
    {
	AtomicUpdate update = factory.createAtomicUpdate("obsceneCounter", "size");
	addFlag(articleID, userID, obscene, update,(a) -> a.isObscene(), (c, b) -> c.setObscene(b));
    }
    
    public void addCopyright(boolean copyright, String articleID, String userID)
    {
	AtomicUpdate update = factory.createAtomicUpdate("copyrightCounter", "size");
	addFlag(articleID, userID, copyright, update, (a) -> a.isCopyright(), (c, b) -> c.setCopyright(b));
    }
    
    public void addWrong(boolean wrong, String articleID, String userID)
    {
	AtomicUpdate update = factory.createAtomicUpdate("wrongCounter", "size");
	addFlag(articleID, userID, wrong, update, (a) -> a.isWrong(), (c, b) -> c.setWrong(b));
    }
    
    // TODO Optimize!!
    public void addRating(FeedbackEntryDataModel rating)
    {
	String userID = rating.getUserID();
	String articleID = rating.getArticleID();

	addCopyright(rating.isCopyright(), articleID, userID);
	addObscene(rating.isObscene(), articleID, userID);
	addObsolete(rating.isObsolete(), articleID, userID);
	addPositive(rating.isPositive(), articleID, userID);
	addWrong(rating.isWrong(), articleID, userID);
    }
    
    private void addFlag(String articleID, String userID, boolean flag, AtomicUpdate update, Function<FeedbackEntryDataModel, Boolean> pGetter, BiConsumer<FeedbackEntryDataModel, Boolean> pSetter) throws IllegalArgumentException
    {
	RepeatedDynamoDBAction action = new RepeatedDynamoDBAction(mapper, pGetter, pSetter);
	updateDatabaseWithFlag(articleID, userID, action, flag, update);
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

	    Boolean savedModel = action.getLastFlagValue();

	    if ((flag != savedModel) || (entryCounterUpdate != 0))
	    {
		update.updateCounter(articleID, entryCounterUpdate, new FeedbackHelper().calculateAddend(savedModel, flag));
	    }

	} catch (AmazonServiceException e)
	{
	    log.error("Updating conditionally FeedbackEntry failed after " + MAX_ATTEMPTS + " attempts.", e);
	}
    }
}
