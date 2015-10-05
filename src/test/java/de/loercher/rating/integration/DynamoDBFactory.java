/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.integration;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import de.loercher.rating.feedback.FeedbackController;
import de.loercher.rating.feedback.FeedbackDataModel;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Jimmy
 */
public class DynamoDBFactory
{

    private final static Logger log = Logger.getLogger(FeedbackController.class);
    private static DynamoDBMapper mapper;

    public static DynamoDB dynamoDB;
    public static AmazonDynamoDBClient client;

    public static final String TEST_ARTICLE_ID = "12345";
    public static final String TEST_USER_ID = "billy";
    public static final ZonedDateTime TEST_TIME = ZonedDateTime.of(2000, 10, 3, 16, 10, 0, 0, ZoneId.of("Europe/Berlin"));

    public static void connectToDB()
    {
	client = new AmazonDynamoDBClient(new BasicAWSCredentials("AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"));
	client.setEndpoint("http://localhost:8000");

	dynamoDB = new DynamoDB(client);
	mapper = new DynamoDBMapper(client);
    }

    public static AmazonDynamoDBClient getClient()
    {
	return client;
    }

    public static void createTables()
    {
	/*
	 * Create table FeedbackEntry
	 */
	String tableName = "FeedbackEntry";

	try
	{

	    ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
	    attributeDefinitions.add(new AttributeDefinition()
		    .withAttributeName("ArticleID")
		    .withAttributeType("S"));

	    attributeDefinitions.add(new AttributeDefinition()
		    .withAttributeName("UserID")
		    .withAttributeType("S"));

	    ArrayList<KeySchemaElement> keySchema = new ArrayList<>();
	    keySchema.add(new KeySchemaElement()
		    .withAttributeName("ArticleID")
		    .withKeyType(KeyType.HASH));

	    keySchema.add(new KeySchemaElement()
		    .withAttributeName("UserID")
		    .withKeyType(KeyType.RANGE));

	    CreateTableRequest request = new CreateTableRequest()
		    .withTableName(tableName)
		    .withKeySchema(keySchema)
		    .withAttributeDefinitions(attributeDefinitions)
		    .withProvisionedThroughput(new ProvisionedThroughput()
			    .withReadCapacityUnits(5L)
			    .withWriteCapacityUnits(6L));

	    log.info("Issuing CreateTable request for " + tableName);
	    Table table = dynamoDB.createTable(request);

	    log.info("Waiting for " + tableName + " to be created...this may take a while...");

	    table.waitForActive();

	} catch (Exception e)
	{
	    System.err.println("CreateTable request failed for " + tableName);
	    System.err.println(e.getMessage());
	}

	/*
	 * Create table Feedback
	 */
	tableName = "Feedback";
	try
	{

	    ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
	    attributeDefinitions.add(new AttributeDefinition()
		    .withAttributeName("ArticleID")
		    .withAttributeType("S"));

	    ArrayList<KeySchemaElement> keySchema = new ArrayList<>();
	    keySchema.add(new KeySchemaElement()
		    .withAttributeName("ArticleID")
		    .withKeyType(KeyType.HASH));

	    CreateTableRequest request = new CreateTableRequest()
		    .withTableName(tableName)
		    .withKeySchema(keySchema)
		    .withAttributeDefinitions(attributeDefinitions)
		    .withProvisionedThroughput(new ProvisionedThroughput()
			    .withReadCapacityUnits(5L)
			    .withWriteCapacityUnits(6L));

	    log.info("Issuing CreateTable request for " + tableName);
	    Table table = dynamoDB.createTable(request);

	    log.info("Waiting for " + tableName + " to be created...this may take a while...");

	    table.waitForActive();

	} catch (Exception e)
	{
	    System.err.println("CreateTable request failed for " + tableName);
	    System.err.println(e.getMessage());
	}

	//Initial setup table Feedback
	FeedbackDataModel model = new FeedbackDataModel(TEST_ARTICLE_ID, TEST_TIME);
	mapper.save(model);
	
    }

    public static void deleteTables()
    {
	Table table = dynamoDB.getTable("FeedbackEntry");

	table.delete();

	try
	{
	    table.waitForDelete();
	} catch (InterruptedException ex)
	{
	    log.error(ex.getCause());
	}

	table = dynamoDB.getTable("Feedback");

	table.delete();

	try
	{
	    table.waitForDelete();
	} catch (InterruptedException ex)
	{
	    log.error(ex.getCause());
	}
    }

}
