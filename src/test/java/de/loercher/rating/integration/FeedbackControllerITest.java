/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.loercher.rating.commons.RatingProperties;
import de.loercher.rating.commons.SecurityHelper;
import de.loercher.rating.commons.exception.GeneralRatingException;
import de.loercher.rating.feedback.DynamoDBConnector;
import de.loercher.rating.feedback.FeedbackController;
import de.loercher.rating.feedback.FeedbackDataModel;
import de.loercher.rating.feedback.FeedbackEntryDataModel;
import de.loercher.rating.feedback.FlagHandlerFactory;
import de.loercher.rating.feedback.dto.CopyrightPostDTO;
import de.loercher.rating.feedback.dto.ObscenePostDTO;
import de.loercher.rating.feedback.dto.ObsoletePostDTO;
import de.loercher.rating.feedback.dto.PositivePostDTO;
import de.loercher.rating.feedback.dto.WrongPostDTO;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Jimmy
 */
public class FeedbackControllerITest extends DBITest
{
    private FeedbackController controller;
    
    public FeedbackControllerITest()
    {
    }
    
    @Before
    @Override
    public void setUp()
    {
	super.setUp();
	try
	{
	    SecurityHelper helper = new SecurityHelper();
	    RatingProperties properties = new RatingProperties(helper);
	    DynamoDBConnector connector = new DynamoDBConnector(properties);
	    ObjectMapper objectMapper = new ObjectMapper();
	    FlagHandlerFactory factory = new FlagHandlerFactory(connector);
	    
	    controller = new FeedbackController(connector, objectMapper, properties, factory);
	} catch (IOException ex)
	{
	    Logger.getLogger(FeedbackControllerITest.class.getName()).log(Level.SEVERE, null, ex);
	}
    }
    
    @After
    public void tearDown()
    {
	DynamoDBFactory.deleteTables();
    }

    @Test
    public void testAddObscene() throws JsonProcessingException, GeneralRatingException 
    {
	controller.addObscene(articleId, userId, new ObscenePostDTO(true));
	
	FeedbackDataModel feedback = mapper.load(FeedbackDataModel.class, articleId);
	assertTrue("Obscene counter must have been updated!", feedback.getObsceneCounter() == 1);
	assertTrue("Counter must have been updated!", feedback.getSize() == 1);
	
	assertTrue("Obscene flag not set!", mapper.load(FeedbackEntryDataModel.class, articleId, userId).isObscene());
    }
    
    @Test
    public void testAddObsolete() throws JsonProcessingException, GeneralRatingException 
    {
	controller.addObsolete(articleId, userId, new ObsoletePostDTO(true));
	
	FeedbackDataModel feedback = mapper.load(FeedbackDataModel.class, articleId);
	assertTrue("Obsolete counter must have been updated!", feedback.getObsoleteCounter() == 1);
	assertTrue("Counter must have been updated!", feedback.getSize() == 1);
	
	assertTrue("Obsolete flag not set!", mapper.load(FeedbackEntryDataModel.class, articleId, userId).isObsolete());
    }
    
    @Test
    public void testAddCopyright() throws JsonProcessingException, IllegalArgumentException, GeneralRatingException 
    {
	controller.addCopyright(articleId, userId, new CopyrightPostDTO(true));
	
	FeedbackDataModel feedback = mapper.load(FeedbackDataModel.class, articleId);
	assertTrue("Copyright counter must have been updated!", feedback.getCopyrightCounter() == 1);
	assertTrue("Counter must have been updated!", feedback.getSize() == 1);
	
	assertTrue("Copyright flag not set!", mapper.load(FeedbackEntryDataModel.class, articleId, userId).isCopyright());
    }
    
    @Test
    public void testAddWrong() throws JsonProcessingException, IllegalArgumentException, GeneralRatingException 
    {
	controller.addWrong(articleId, userId, new WrongPostDTO(true));
	
	FeedbackDataModel feedback = mapper.load(FeedbackDataModel.class, articleId);
	assertTrue("Wrong counter must have been updated!", feedback.getWrongCounter()== 1);
	assertTrue("Counter must have been updated!", feedback.getSize() == 1);
	
	assertTrue("Wrong flag not set!", mapper.load(FeedbackEntryDataModel.class, articleId, userId).isWrong());
    }
    
    @Test
    public void testAddPositive() throws Exception
    {
	FeedbackEntryDataModel model = new FeedbackEntryDataModel(time, articleId, userId);
	mapper.save(model);
	
	FeedbackDataModel feedback = mapper.load(FeedbackDataModel.class, articleId);
	assertTrue(feedback.getPositiveCounter() == 0);
	
	controller.addPositive(articleId, userId, new PositivePostDTO(true));
	
	feedback = mapper.load(FeedbackDataModel.class, articleId);
	assertTrue("Positive Counter must have been updated!", feedback.getPositiveCounter() == 1);
	
	controller.addPositive(articleId, userId, new PositivePostDTO(true));
	controller.addPositive(articleId, userId, new PositivePostDTO(true));
	controller.addPositive(articleId, userId, new PositivePostDTO(true));
	
	controller.addPositive(articleId, userId, new PositivePostDTO(true));
	controller.addPositive(articleId, userId, new PositivePostDTO(true));
	
	feedback = mapper.load(FeedbackDataModel.class, articleId);
	assertTrue("Positive Counter must not be updated more than once foreach user! ( " + feedback.getPositiveCounter() + " )" , feedback.getPositiveCounter() == 1);
	
	String newUser = "abc";
	controller.addPositive(articleId, newUser, new PositivePostDTO(true));
	
	feedback = mapper.load(FeedbackDataModel.class, articleId);
	assertTrue("Positive Counter has to be 2 now!", feedback.getPositiveCounter() == 2);
	
	assertNotNull(mapper.load(FeedbackEntryDataModel.class, articleId, newUser));
    }
    
    @Test
    public void testAddDifferentFlags() throws Exception
    {
	FeedbackEntryDataModel model = new FeedbackEntryDataModel(time, articleId, userId);
	mapper.save(model);
	
	FeedbackDataModel feedback = mapper.load(FeedbackDataModel.class, articleId);
	assertTrue(feedback.getPositiveCounter()== 0);
	
	controller.addPositive(articleId, userId, new PositivePostDTO(false));
	
	feedback = mapper.load(FeedbackDataModel.class, articleId);
	assertTrue("Positive Counter must not have been updated!", feedback.getPositiveCounter() == 0);
	
	controller.addPositive(articleId, userId, new PositivePostDTO(true));
	controller.addPositive(articleId, userId, new PositivePostDTO(false));
	controller.addPositive(articleId, userId, new PositivePostDTO(true));
	
	controller.addPositive(articleId, userId, new PositivePostDTO(false));
	controller.addPositive(articleId, userId, new PositivePostDTO(false));
	
	feedback = mapper.load(FeedbackDataModel.class, articleId);
	assertTrue("Positive Counter must be back to 0! ( " + feedback.getPositiveCounter() + " )" , feedback.getPositiveCounter() == 0);
	
	assertFalse("The flag must be set to false after all!", mapper.load(FeedbackEntryDataModel.class, articleId, userId).isPositive());
    }
    
    @Test
    public void testNewEntry() throws Exception
    {
	String newArticleID = "ABCD";
	String newUserID = "ebel";
	
	FeedbackEntryDataModel model = mapper.load(FeedbackEntryDataModel.class, newArticleID, newUserID);
	assertNull("There shouldn't be a model for new article id defined!", model);
	
	controller.addObscene(newArticleID, newUserID, new ObscenePostDTO(true));
	
	model = mapper.load(FeedbackEntryDataModel.class, newArticleID, newUserID);
	assertNotNull("After adding flag there has to be a model!", model);
	
	assertTrue("Flag 'obscene' has to be updated!", model.isObscene());
	
	FeedbackDataModel feedbackModel = mapper.load(FeedbackDataModel.class, newArticleID);
	assertTrue("Counter has to be 1 now!", feedbackModel.getSize() == 1);
	assertTrue("Obscene counter has to be 1 now!", feedbackModel.getObsceneCounter() == 1);
	
	assertTrue("Wrong place counter not set!", feedbackModel.getWrongPlaceCounter() == 0);
	assertTrue("Wrong category counter not set!", feedbackModel.getWrongCategoryCounter() == 0);
	
	String secondUserID = "alfich";
	controller.addObscene(newArticleID, secondUserID, new ObscenePostDTO(true));
	
	feedbackModel = mapper.load(FeedbackDataModel.class, newArticleID);
	assertTrue("Counter has to be 2 now!", feedbackModel.getSize() == 2);
	assertTrue("Obscene counter has to be 2 now!", feedbackModel.getObsceneCounter() == 2);
	
	String thirdUserID = "TBC";
	controller.addObscene(newArticleID, thirdUserID, new ObscenePostDTO(false));
	
	feedbackModel = mapper.load(FeedbackDataModel.class, newArticleID);
	assertTrue("Counter has to be 3 now!", feedbackModel.getSize() == 3);
	assertTrue("Obscene counter has to be 2 like before!", feedbackModel.getObsceneCounter() == 2);
	
	controller.addObscene(newArticleID, secondUserID, new ObscenePostDTO(false));
	
	feedbackModel = mapper.load(FeedbackDataModel.class, newArticleID);
	assertTrue("Counter has to be 3 like before!", feedbackModel.getSize() == 3);
	assertTrue("Obscene counter has to be reduced to 1 now!", feedbackModel.getObsceneCounter() == 1);
	
	model = mapper.load(FeedbackEntryDataModel.class, newArticleID, secondUserID);
	assertFalse("Flag 'obscene' not properly saved to false!", model.isObscene());
    }
    
}
