/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 * @author Jimmy
 */
public class TransactionalUpdate
{

   // static DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(new BasicAWSCredentials("AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY")));
    private static final String tableName = "Feedback";

    private final String articleId;
    private final String fieldName;
    private Integer counter = 0;

    private final Function<FeedbackEntry, Boolean> accessorMethod;
    private final BiConsumer<FeedbackEntry, Boolean> setterMethod;

    public TransactionalUpdate(String pArticleId, String pFieldName, Function<FeedbackEntry, Boolean> pAccessorMethod, BiConsumer<FeedbackEntry, Boolean> pSetterMethod)
    {
	articleId = pArticleId;
	fieldName = pFieldName;

	accessorMethod = pAccessorMethod;
	setterMethod = pSetterMethod;
    }

    public void updateFlag(FeedbackEntry entry, Boolean newValue, Integer entryCounter)
    {
	boolean oldValue = accessorMethod.apply(entry);
	counter = updateCounterFromDuplicate(counter, oldValue, newValue);

	AmazonDynamoDBClient client = new AmazonDynamoDBClient(new BasicAWSCredentials("AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"));
	client.setEndpoint("http://localhost:8000");

	DynamoDB dynamoDB = new DynamoDB(client);

	Table table = dynamoDB.getTable(tableName);
//
	Item item = new Item()
		.withPrimaryKey("ArticleID", "121")
		.withNumber("obsoleteCounter", 25)
		.withString("Title", "Book 120 Title")
		.withBoolean("InPublication", false)
		.withString("ProductCategory", "Book");
	
	table.putItem(item);

	UpdateItemSpec spec = new UpdateItemSpec()
		.withPrimaryKey("ArticleID", "121")
		.withUpdateExpression("set obsoleteCounter = obsoleteCounter + :p, abc = :p")
		//.withUpdateExpression("set abc = :p")
		//.withNameMap(new NameMap().with("#field", fieldName))
		.withValueMap(new ValueMap()
//		    .withString(":field", fieldName)
		    .withNumber(":p", 20));
//		    .withNumber(":counter", entryCounter));

	UpdateItemOutcome outcome = table.updateItem(spec);

	setterMethod.accept(entry, newValue);
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
