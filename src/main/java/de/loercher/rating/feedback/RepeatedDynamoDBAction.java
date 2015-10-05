/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 * @author Jimmy
 */
public class RepeatedDynamoDBAction
{
    private final DynamoDBMapper mapper;
    private Boolean lastFlagValue = null;
    private final Function<FeedbackEntryDataModel, Boolean> getter;
    private final BiConsumer<FeedbackEntryDataModel, Boolean> setter;
    
    public RepeatedDynamoDBAction(DynamoDBMapper pMapper, Function<FeedbackEntryDataModel, Boolean> pGetter, BiConsumer<FeedbackEntryDataModel, Boolean> pSetter)
    {
	mapper = pMapper;
	
	getter = pGetter;
	setter = pSetter;
    }
    
    public void saveEntryConditionally(final boolean newFlag, FeedbackEntryDataModel pEntry, Integer attempts) throws AmazonServiceException
    {
	lastFlagValue = getter.apply(pEntry);
	
	// If the entry is already set to flag there has not to be done anything anymore. (Neither set flag nor raise the counter)
	if (newFlag == lastFlagValue)
	{
	    return;
	}

	setter.accept(pEntry, newFlag);

	try
	{
	    mapper.save(pEntry);
	} catch (AmazonServiceException e)
	{
	    if (attempts == 1)
	    {
		throw e;
	    } else
	    {
		FeedbackEntryDataModel entry = mapper.load(pEntry);
		saveEntryConditionally(newFlag, entry, attempts - 1);
	    }
	}
    }
    
    public Boolean getLastFlagValue()
    {
	return lastFlagValue;
    }
}
