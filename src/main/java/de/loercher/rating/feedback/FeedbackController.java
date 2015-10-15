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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.loercher.rating.commons.RatingProperties;
import de.loercher.rating.feedback.dto.CopyrightPostDTO;
import de.loercher.rating.feedback.dto.ObscenePostDTO;
import de.loercher.rating.feedback.dto.ObsoletePostDTO;
import de.loercher.rating.feedback.dto.PositivePostDTO;
import de.loercher.rating.feedback.dto.WrongPostDTO;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.apache.log4j.Logger;
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
    private final static Logger log = Logger.getLogger(FeedbackController.class);
    private final static Integer MAX_ATTEMPTS = 5;

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

    @RequestMapping(value = "/{articleID}/feedback/{userID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getFeedback(@PathVariable String articleID, @PathVariable String userID) throws JsonProcessingException
    {
	FeedbackEntryDataModel model = mapper.load(FeedbackEntryDataModel.class, articleID, userID);
	Map<String, Object> result = new HashMap<>();
	result.put("articleID", articleID);
	result.put("userID", userID);
	Timestamp now = new Timestamp(new Date().getTime());
	result.put("timestamp", now);

	if (model == null)
	{
	    result.put("status", 404);
	    result.put("error", "Not Found");
	    
	    return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.NOT_FOUND);
	}

	result.put("status", 200);
	result.put("feedback", model.toMap());

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/{articleID}/feedback/positive", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addPositive(@PathVariable String articleID, @RequestHeader(value = "UserID") String userID, @RequestBody PositivePostDTO positive) throws JsonProcessingException
    {
	AtomicUpdate update = factory.createAtomicUpdate("positiveCounter", "size");
	addFlag(articleID, userID, positive.isPositive(), update, (a) -> a.isPositive(), (c, b) -> c.setPositive(b));

	Map<String, Object> result = generateResultMap(articleID, userID);

	result.put("positive", positive.isPositive());
	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);

    }

    @RequestMapping(value = "/{articleID}/feedback/obsolete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addObsolete(@PathVariable String articleID, @RequestHeader(value = "UserID") String userID, @RequestBody ObsoletePostDTO obsolete) throws JsonProcessingException
    {
	AtomicUpdate update = factory.createAtomicUpdate("obsoleteCounter", "size");
	addFlag(articleID, userID, obsolete.isObsolete(), update, (a) -> a.isObsolete(), (c, b) -> c.setObsolete(b));

	Map<String, Object> result = generateResultMap(articleID, userID);

	result.put("obsolete", obsolete.isObsolete());
	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/{articleID}/feedback/obscene", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addObscene(@PathVariable String articleID, @RequestHeader(value = "UserID") String userID, @RequestBody ObscenePostDTO obscene) throws JsonProcessingException
    {
	AtomicUpdate update = factory.createAtomicUpdate("obsceneCounter", "size");
	addFlag(articleID, userID, obscene.isObscene(), update, (a) -> a.isObscene(), (c, b) -> c.setObscene(b));

	Map<String, Object> result = generateResultMap(articleID, userID);

	result.put("obscene", obscene.isObscene());
	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/{articleID}/feedback/copyright", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addCopyright(@PathVariable String articleID, @RequestHeader(value = "UserID") String userID, @RequestBody CopyrightPostDTO copyright) throws JsonProcessingException
    {
	AtomicUpdate update = factory.createAtomicUpdate("copyrightCounter", "size");
	addFlag(articleID, userID, copyright.isCopyright(), update, (a) -> a.isCopyright(), (c, b) -> c.setCopyright(b));

	Map<String, Object> result = generateResultMap(articleID, userID);

	result.put("copyright", copyright.isCopyright());
	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/{articleID}/feedback/wrong", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addWrong(@PathVariable String articleID, @RequestHeader(value = "UserID") String userID, @RequestBody WrongPostDTO wrong) throws JsonProcessingException
    {
	AtomicUpdate update = factory.createAtomicUpdate("wrongCounter", "size");
	addFlag(articleID, userID, wrong.isWrong(), update, (a) -> a.isWrong(), (c, b) -> c.setWrong(b));

	Map<String, Object> result = generateResultMap(articleID, userID);

	result.put("wrong", wrong.isWrong());
	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
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

    private Map<String, Object> generateResultMap(String articleID, String userID)
    {
	Map<String, Object> result = new HashMap<>();
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
