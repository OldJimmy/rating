/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.integration;

import de.loercher.rating.feedback.FeedbackController;
import de.loercher.rating.feedback.FeedbackDataModel;
import de.loercher.rating.feedback.FeedbackEntryDataModel;
import static org.junit.Assert.assertTrue;

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
	controller = new FeedbackController(time, articleId);
    }
    
    @After
    public void tearDown()
    {
	DynamoDBFactory.deleteTables();
    }

    @Test
    public void testAddPositive() throws Exception
    {
	FeedbackEntryDataModel model = new FeedbackEntryDataModel(time, articleId, userId);
	mapper.save(model);
	
	FeedbackDataModel feedback = mapper.load(FeedbackDataModel.class, articleId);
	assertTrue(feedback.getObsoleteCounter() == 0);
	
	controller.addPositive(true, articleId, userId);
	
	feedback = mapper.load(FeedbackDataModel.class, articleId);
	assertTrue("Positive Counter must have been updated!", feedback.getPositiveCounter() == 1);
	
	controller.addPositive(true, articleId, userId);
	controller.addPositive(true, articleId, userId);
	controller.addPositive(true, articleId, userId);
	
	controller.addPositive(true, articleId, userId);
	controller.addPositive(true, articleId, userId);
	
	feedback = mapper.load(FeedbackDataModel.class, articleId);
	assertTrue("Positive Counter must not be updated more than once foreach user! ( " + feedback.getPositiveCounter() + " )" , feedback.getPositiveCounter() == 1);
    }
    
}
