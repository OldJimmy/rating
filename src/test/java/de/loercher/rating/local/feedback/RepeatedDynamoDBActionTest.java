/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.local.feedback;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import de.loercher.rating.feedback.FeedbackEntryDataModel;
import de.loercher.rating.feedback.RepeatedDynamoDBAction;
import java.time.ZonedDateTime;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Jimmy
 */
public class RepeatedDynamoDBActionTest
{

    private DynamoDBMapper mapper = mock(DynamoDBMapper.class);
    private FeedbackEntryDataModel dummyModel;
    private FeedbackEntryDataModel positiveDummyModel;
    private FeedbackEntryDataModel positiveReturnDummyModel;

    @Before
    public void setUp()
    {
	dummyModel = new FeedbackEntryDataModel(ZonedDateTime.now(), "12345", "alfons");
	dummyModel.setCopyright(Boolean.FALSE);
	positiveDummyModel = new FeedbackEntryDataModel(ZonedDateTime.now(), "12345", "alfons");
	positiveDummyModel.setCopyright(Boolean.TRUE);
	positiveReturnDummyModel = new FeedbackEntryDataModel(ZonedDateTime.now(), "12345", "alfons");
	positiveReturnDummyModel.setCopyright(Boolean.TRUE);
    }

    @Test
    public void testRepetition()
    {
	doThrow(new AmazonServiceException("BLABLA")).when(mapper).save(dummyModel);
	doThrow(new AmazonServiceException("BLABLA")).when(mapper).save(anyObject());

	when(mapper.load(dummyModel)).thenReturn(dummyModel);

	RepeatedDynamoDBAction action = new RepeatedDynamoDBAction(mapper, (a) -> a.isCopyright(), (b, c) -> b.setCopyright(c));
	try
	{
	    action.saveEntryConditionally(true, dummyModel, 1);
	    fail("There should have been a reiteration exception!");
	} catch (AmazonServiceException a)
	{
	}

	mapper = mock(DynamoDBMapper.class);

	doThrow(new AmazonServiceException("BLABLA")).when(mapper).save(dummyModel);
	doThrow(new AmazonServiceException("BLABLA")).when(mapper).save(anyObject());

	when(mapper.load(dummyModel)).thenReturn(positiveReturnDummyModel);
	when(mapper.load(positiveDummyModel)).thenReturn(positiveReturnDummyModel);
	when(mapper.load(FeedbackEntryDataModel.class, dummyModel.getArticleID(), dummyModel.getUserID())).thenReturn(positiveReturnDummyModel.cloneEntry());

	RepeatedDynamoDBAction actionTwo = new RepeatedDynamoDBAction(mapper, (a) -> a.isCopyright(), (b, c) -> b.setCopyright(c));
	try
	{
	    actionTwo.saveEntryConditionally(false, positiveDummyModel, 10);
	    fail("There should have been a retrial exception!");
	} catch (AmazonServiceException a)
	{
	}
    }
}
