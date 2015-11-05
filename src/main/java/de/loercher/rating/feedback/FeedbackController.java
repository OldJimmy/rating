/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.loercher.rating.commons.RatingProperties;
import de.loercher.rating.commons.exception.ArticleResourceNotFoundException;
import de.loercher.rating.commons.exception.GeneralRatingException;
import de.loercher.rating.commons.exception.UserResourceNotFoundException;
import de.loercher.rating.feedback.dto.CopyrightPostDTO;
import de.loercher.rating.feedback.dto.ObscenePostDTO;
import de.loercher.rating.feedback.dto.ObsoletePostDTO;
import de.loercher.rating.feedback.dto.PositivePostDTO;
import de.loercher.rating.feedback.dto.WrongPostDTO;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Jimmy
 */
@RestController
@RequestMapping("localpress/")
public class FeedbackController
{

    private final String baseurl;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final static Integer MAX_ATTEMPTS = 5;

    // the following classes are thread-safe thus can be declared as class members!
    private final DynamoDBMapper mapper;
    private final ObjectMapper objectMapper;
    private final Properties properties;

    private final FlagHandlerFactory factory;

    @Autowired
    public FeedbackController(DynamoDBConnector connector, ObjectMapper pMapper, RatingProperties pProperties, FlagHandlerFactory pFactory)
    {
	properties = pProperties.getProp();
	baseurl = properties.getProperty("baseUrl");

	mapper = new DynamoDBMapper(connector.getClient());

	factory = pFactory;
	objectMapper = pMapper;
    }

    public FeedbackDataModel getFeedback(String articleID)
    {
	return mapper.load(FeedbackDataModel.class, articleID);
    }

    @RequestMapping(value = "/feedback/{userID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getFeedbackOfUser(@PathVariable String userID) throws JsonProcessingException, UserResourceNotFoundException, GeneralRatingException
    {
	FeedbackEntryDataModel reply = new FeedbackEntryDataModel();
	reply.setUserID(userID);

	DynamoDBQueryExpression<FeedbackEntryDataModel> queryExpression = new DynamoDBQueryExpression<FeedbackEntryDataModel>()
		.withIndexName("UserIndex")
		.withHashKeyValues(reply)
		.withConsistentRead(false);

	PaginatedQueryList<FeedbackEntryDataModel> list;
	
	try
	{
	   list = mapper.query(FeedbackEntryDataModel.class, queryExpression);
	} catch (Exception e)
	{
	    GeneralRatingException ex = new GeneralRatingException("Unexpected exception occurred by loading DB-entries of FeedbackEntry from user " + userID + ".", e);
	    log.error(ex.getLoggingString(), e);
	    throw ex;
	}
	
	list.loadAllResults();

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("userID", userID);

	List< Map<String, Object>> entries = new ArrayList<>();
	for (FeedbackEntryDataModel model : list)
	{
	    Map<String, Object> entry = model.toMap();
	    entries.add(entry);
	}

	if (entries.isEmpty())
	{
	    throw new UserResourceNotFoundException(userID, "No feedback available from user " + userID + "!");
	}

	result.put("entries", entries);

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/{articleID}/feedback/{userID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getFeedback(@PathVariable String articleID, @PathVariable String userID) throws JsonProcessingException, ArticleResourceNotFoundException, GeneralRatingException
    {
	FeedbackEntryDataModel model;

	try
	{
	    model = mapper.load(FeedbackEntryDataModel.class, articleID, userID);
	} catch (Exception e)
	{
	    GeneralRatingException ex = new GeneralRatingException("Unexpected exception occurred by loading DB-entries of FeedbackEntryData from article " + articleID + " and user " + userID + ".", e);
	    log.error(ex.getLoggingString(), e);
	    throw ex;
	}

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("articleID", articleID);
	result.put("userID", userID);

	if (model == null)
	{
	    throw new ArticleResourceNotFoundException(articleID, "FeedbackEntry with articleID " + articleID + " for user " + userID + "not found.");
	}

	result.put("feedback", model.toMap());

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/{articleID}/feedback/positive", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addPositive(@PathVariable String articleID, @RequestHeader(value = "UserID") String userID, @RequestBody PositivePostDTO positive) throws JsonProcessingException, IllegalArgumentException, GeneralRatingException
    {
	AtomicUpdate update = factory.createAtomicUpdate("positiveCounter", "size");
	addFlag(articleID, userID, positive.isPositive(), update, (a) -> a.isPositive(), (c, b) -> c.setPositive(b));

	Map<String, Object> result = generateResultMap(articleID, userID);

	result.put("positive", positive.isPositive());
	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/{articleID}/feedback/obsolete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addObsolete(@PathVariable String articleID, @RequestHeader(value = "UserID") String userID, @RequestBody ObsoletePostDTO obsolete) throws JsonProcessingException, GeneralRatingException
    {
	AtomicUpdate update = factory.createAtomicUpdate(FeedbackDataModel.OBSOLETE_COUNTER_NAME, FeedbackDataModel.SIZE_NAME);
	addFlag(articleID, userID, obsolete.isObsolete(), update, (a) -> a.isObsolete(), (c, b) -> c.setObsolete(b));

	Map<String, Object> result = generateResultMap(articleID, userID);

	result.put("obsolete", obsolete.isObsolete());
	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/{articleID}/feedback/obscene", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addObscene(@PathVariable String articleID, @RequestHeader(value = "UserID") String userID, @RequestBody ObscenePostDTO obscene) throws JsonProcessingException, GeneralRatingException
    {
	AtomicUpdate update = factory.createAtomicUpdate(FeedbackDataModel.OBSCENE_COUNTER_NAME, FeedbackDataModel.SIZE_NAME);
	addFlag(articleID, userID, obscene.isObscene(), update, (a) -> a.isObscene(), (c, b) -> c.setObscene(b));

	Map<String, Object> result = generateResultMap(articleID, userID);

	result.put("obscene", obscene.isObscene());
	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/{articleID}/feedback/copyright", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addCopyright(@PathVariable String articleID, @RequestHeader(value = "UserID") String userID, @RequestBody CopyrightPostDTO copyright) throws JsonProcessingException, IllegalArgumentException, GeneralRatingException
    {
	AtomicUpdate update = factory.createAtomicUpdate(FeedbackDataModel.COPYRIGHT_COUNTER_NAME, FeedbackDataModel.SIZE_NAME);
	addFlag(articleID, userID, copyright.isCopyright(), update, (a) -> a.isCopyright(), (c, b) -> c.setCopyright(b));

	Map<String, Object> result = generateResultMap(articleID, userID);

	result.put("copyright", copyright.isCopyright());
	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/{articleID}/feedback/wrong", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addWrong(@PathVariable String articleID, @RequestHeader(value = "UserID") String userID, @RequestBody WrongPostDTO wrong) throws JsonProcessingException, IllegalArgumentException, GeneralRatingException
    {
	AtomicUpdate update = factory.createAtomicUpdate(FeedbackDataModel.WRONG_COUNTER_NAME, FeedbackDataModel.SIZE_NAME);
	addFlag(articleID, userID, wrong.isWrong(), update, (a) -> a.isWrong(), (c, b) -> c.setWrong(b));

	Map<String, Object> result = generateResultMap(articleID, userID);

	result.put("wrong", wrong.isWrong());
	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    private void addFlag(String articleID, String userID, boolean flag, AtomicUpdate update, Function<FeedbackEntryDataModel, Boolean> pGetter, BiConsumer<FeedbackEntryDataModel, Boolean> pSetter) throws IllegalArgumentException, GeneralRatingException
    {
	RepeatedDynamoDBAction action = new RepeatedDynamoDBAction(mapper, pGetter, pSetter);
	updateDatabaseWithFlag(articleID, userID, action, flag, update);
    }

    // there could be a scenario in which there will be a slight inconsistence when the second DB-access fails (no transaction capability), 
    // but at least there is thrown an exception
    private synchronized void updateDatabaseWithFlag(String articleID, String userID, RepeatedDynamoDBAction action, boolean flag, AtomicUpdate update) throws IllegalArgumentException, GeneralRatingException
    {
	Integer entryCounterUpdate = 0;

	FeedbackEntryDataModel entry = mapper.load(FeedbackEntryDataModel.class, articleID, userID);
	if (entry == null)
	{
	    entry = new FeedbackEntryDataModel(ZonedDateTime.now(), articleID, userID);
	    entryCounterUpdate = 1;

	    log.info("New FeedbackEntry for article " + articleID + " created.");
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
	    GeneralRatingException ex = new GeneralRatingException("Updating conditionally FeedbackEntry failed after " + MAX_ATTEMPTS + " attempts. UserID: " + userID + ", ArticleID: " + articleID + ".", e);
	    log.error(ex.getError(), e);
	    
	    throw ex;
	}
    }

    private Map<String, Object> generateResultMap(String articleID, String userID)
    {
	Map<String, Object> result = new LinkedHashMap<>();
	result.put("articleID", articleID);
	result.put("userID", userID);

	Timestamp now = new Timestamp(new Date().getTime());
	result.put("timestamp", now);
	result.put("status", 200);

	String url = baseurl + articleID + "/feedback/" + userID;
	result.put("feedbackURL", url);
	return result;
    }
}
