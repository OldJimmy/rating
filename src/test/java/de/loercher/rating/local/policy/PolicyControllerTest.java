/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.local.policy;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.loercher.rating.feedback.FeedbackController;
import de.loercher.rating.feedback.FeedbackDataModel;
import de.loercher.rating.integration.DBITest;
import de.loercher.rating.commons.exception.InappropriateContentException;
import de.loercher.rating.policy.PolicyController;
import de.loercher.rating.commons.exception.ArticleResourceNotFoundException;
import java.time.ZonedDateTime;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Jimmy
 */
public class PolicyControllerTest
{

    private PolicyController policy;
    private FeedbackController feedback;
    private final ZonedDateTime now = ZonedDateTime.now();
    private FeedbackDataModel dummy;
    private final String articleID = DBITest.articleId;

    @Before
    public void setUp()
    {
	feedback = mock(FeedbackController.class);
	policy = new PolicyController(feedback, new ObjectMapper());
	dummy = new FeedbackDataModel(DBITest.articleId, now);
	when(feedback.getFeedback(DBITest.articleId)).thenReturn(dummy);
    }

    @Test
    public void testDelayedEntries() throws InappropriateContentException, ArticleResourceNotFoundException
    {
	dummy.setPositiveCounter(10);

	Double firstRating = policy.calculateRating(DBITest.articleId);
	dummy.setTimeOfPressEntry(now.minusDays(1));

	assertTrue("Older Entry should be lower rated than newer one!", firstRating > policy.calculateRating(DBITest.articleId));
    }

    @Test
    public void testForbidden() throws ArticleResourceNotFoundException
    {
	dummy.setSize(10);
	dummy.setObsceneCounter(5);

	try
	{
	    policy.calculateRating(DBITest.articleId);
	    fail("Such a high obscene counter has to result in an InappropriateContentException!");
	} catch (InappropriateContentException i)
	{
	}

	dummy.setObsceneCounter(0);
	try
	{
	    policy.calculateRating(DBITest.articleId);
	} catch (InappropriateContentException i)
	{
	    fail("Obscene counter should be back to normal!");
	}

	dummy.setObsoleteCounter(5);

	try
	{
	    policy.calculateRating(DBITest.articleId);
	    fail("Such a high obsolete counter has to result in an InappropriateContentException!");
	} catch (InappropriateContentException i)
	{
	}

	dummy.setObsoleteCounter(0);
	try
	{
	    policy.calculateRating(DBITest.articleId);
	} catch (InappropriateContentException i)
	{
	    fail("Obsolete counter should be back to normal!");
	}

	dummy.setCopyrightCounter(5);

	try
	{
	    policy.calculateRating(DBITest.articleId);
	    fail("Such a high copyright counter has to result in an InappropriateContentException!");
	} catch (InappropriateContentException i)
	{
	}
    }

    @Test
    public void testJustCreatedEntry() throws InappropriateContentException, ArticleResourceNotFoundException
    {
	dummy.setPositiveCounter(2);

	Double firstRating = policy.calculateRating(DBITest.articleId);

	dummy.setPositiveCounter(3);
	Double secondRating = policy.calculateRating(DBITest.articleId);

	assertTrue("Adding a positive feedback should result in higher rating!", secondRating > firstRating);
	assertTrue("First Rating should be near to 2.0!", isSimilar(firstRating, 2.0));
	assertTrue("Second Rating should be near to 3.0!", isSimilar(secondRating, 3.0));

    }

    @Test
    public void testNullValues() throws InappropriateContentException, ArticleResourceNotFoundException
    {
	dummy.setPositiveCounter(0);

	Double firstRating = policy.calculateRating(articleID);
	assertTrue("Rating should be roundabout 0.0!", isSimilar(firstRating, 0.0));
    }

    @Test
    public void testWrongEntries() throws InappropriateContentException, ArticleResourceNotFoundException
    {
	dummy.setSize(10);
	dummy.setWrongCounter(5);

	Double firstRating = policy.calculateRating(articleID);
	assertTrue("Rating should be less than 0!", firstRating < 0);
	
	dummy.setWrongCounter(0);
	dummy.setPositiveCounter(20);
	
	firstRating = policy.calculateRating(articleID);
	
	dummy.setWrongCounter(1);
	assertTrue("Rating should be less with an additional wrong flag!", policy.calculateRating(articleID) < firstRating);
    }

    private boolean isSimilar(Double a, Double b)
    {
	Double difference = a - b;
	return (Math.abs(difference) < 0.000001);
    }
}
