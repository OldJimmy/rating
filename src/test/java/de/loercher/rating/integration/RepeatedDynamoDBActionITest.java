/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.integration;

import com.amazonaws.AmazonServiceException;
import de.loercher.rating.feedback.FeedbackEntryDataModel;
import de.loercher.rating.feedback.RepeatedDynamoDBAction;
import static de.loercher.rating.integration.DBITest.mapper;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import java.time.ZonedDateTime;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author Jimmy
 */
public class RepeatedDynamoDBActionITest extends DBITest
{

    private RepeatedDynamoDBAction action;

//    @Override
//    @Before
//    public void setUp()
//    {
//	super.setUp();
//	
//    }

    @After
    public void tearDown()
    {
	DynamoDBFactory.deleteTables();
    }

    @Test
    public void testRepetition()
    {
	FeedbackEntryDataModel entry = new FeedbackEntryDataModel(ZonedDateTime.now(), articleId, userId);

	mapper.save(entry);
	
	final FeedbackEntryDataModel oldFeedback = mapper.load(FeedbackEntryDataModel.class, articleId, userId);

	FeedbackEntryDataModel newFeedback = mapper.load(FeedbackEntryDataModel.class, articleId, userId);

	newFeedback.setCopyright();
	mapper.save(newFeedback);

	action = new RepeatedDynamoDBAction(mapper, (a) -> a.getPositive(), (b, c) -> b.setPositive(c));

	try
	{
	    action.saveEntryConditionally(true, oldFeedback, 5);
	} catch (AmazonServiceException e)
	{
	    fail("Thrown unexpected exception after save an obsolete Entry.");
	}
	
	newFeedback = mapper.load(FeedbackEntryDataModel.class, articleId, userId);
	assertTrue("FeedbackEntry should have been updated!", newFeedback.getPositive());
    }
    
    @Test
    public void testRepetitionUnsetFlag()
    {
	FeedbackEntryDataModel entry = new FeedbackEntryDataModel(ZonedDateTime.now(), articleId, userId);
	entry.setPositive(Boolean.TRUE);
	
	mapper.save(entry);
	
	final FeedbackEntryDataModel oldFeedback = mapper.load(FeedbackEntryDataModel.class, articleId, userId);

	FeedbackEntryDataModel newFeedback = mapper.load(FeedbackEntryDataModel.class, articleId, userId);

	newFeedback.setCopyright();
	mapper.save(newFeedback);

	action = new RepeatedDynamoDBAction(mapper, (a) -> a.getPositive(), (b, c) -> b.setPositive(c));

	try
	{
	    action.saveEntryConditionally(false, oldFeedback, 5);
	} catch (AmazonServiceException e)
	{
	    fail("Thrown unexpected exception after save an obsolete Entry.");
	}
	
	newFeedback = mapper.load(FeedbackEntryDataModel.class, articleId, userId);
	assertTrue("FeedbackEntry should have been updated!", !newFeedback.getPositive());
    }
}
