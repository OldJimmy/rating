/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 * @author Jimmy
 */
public class TransactionalUpdate
{

    // static DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(new BasicAWSCredentials("AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY")));
    private static final String SIZEATTRIBUTE = "size";
    private static final String TABLENAME = "Feedback";

    private AmazonDynamoDBClient client = new AmazonDynamoDBClient(new BasicAWSCredentials("AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"));
    private DynamoDB dynamoDB;

    private final String articleId;
    private final String fieldName;
    private Integer counter = 0;

    private final Function<FeedbackEntryDataModel, Boolean> accessorMethod;
    private final BiConsumer<FeedbackEntryDataModel, Boolean> setterMethod;

    public TransactionalUpdate(String pArticleId, String pFieldName, Function<FeedbackEntryDataModel, Boolean> pAccessorMethod, BiConsumer<FeedbackEntryDataModel, Boolean> pSetterMethod)
    {
	articleId = pArticleId;
	fieldName = pFieldName;

	accessorMethod = pAccessorMethod;
	setterMethod = pSetterMethod;

	client.setEndpoint("http://localhost:8000");
	dynamoDB = new DynamoDB(client);
    }

    public FeedbackEntryDataModel updateFlag(FeedbackEntryDataModel entry, boolean newValue, Integer entryCounter) throws IllegalArgumentException
    {
	if (entryCounter < 1) throw new IllegalArgumentException("Only positive arguments allowad as overall size!");
	
	if (entry == null) return null;
	
	boolean oldValue = accessorMethod.apply(entry);
	counter = updateCounterFromDuplicate(counter, oldValue, newValue);

	Table table = dynamoDB.getTable(TABLENAME);

	Integer counterUpdate = calculateAddend(oldValue, newValue);

	UpdateItemSpec spec = new UpdateItemSpec()
		.withPrimaryKey("ArticleID", articleId)
		.withUpdateExpression("set #field = #field + :one, #counter = :newcounter")
		.withNameMap(new NameMap().with("#field", fieldName).with("#counter", SIZEATTRIBUTE))
		.withValueMap(new ValueMap()
			.withNumber(":newcounter", entryCounter)
			.withNumber(":one", counterUpdate));

	UpdateItemOutcome outcome = table.updateItem(spec);
	
	setterMethod.accept(entry, newValue);
	
	return entry;
    }

    private Integer calculateAddend(boolean oldEntry, boolean newEntry)
    {
	if (oldEntry == newEntry)
	{
	    return 0;
	}
	
	if (oldEntry && !newEntry)
	{
	    return -1;
	}
	
	if (newEntry && !oldEntry)
	{
	    return 1;
	}

	return null;
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

    public Integer getCounter()
    {
	return counter;
    }
}
