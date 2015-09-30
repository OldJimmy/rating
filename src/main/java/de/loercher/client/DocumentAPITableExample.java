// Copyright 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache License, Version 2.0.
package de.loercher.client;

import com.amazonaws.auth.BasicAWSCredentials;
import java.util.ArrayList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

public class DocumentAPITableExample
{

    //static AmazonDynamoDB dynamoDB = new AmazonDynamoDBClient(new BasicAWSCredentials("AKIAJZJOCIGRKVLSJHXA", "u332zfVskOLa3O2DqKDnwibCVM9rBvNdt4HBA5Ei"));
    static AmazonDynamoDB dynamoDB = new AmazonDynamoDBClient(new BasicAWSCredentials("AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"));
//
//new BasicAWSCredentials("AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY")));
    static String tableName = "ExampleTable";

    public static void main(String[] args) throws Exception
    {

	dynamoDB.setEndpoint("http://localhost:8000");

	final String[] localArgs =
	{
	    "-inMemory",
	    "sharedDb"
	   // "-sqlite4java.library.path=C:\\Users\\Jimmy\\.m2\\repository\\com\\almworks\\sqlite4java\\sqlite4java\\1.0.392\\sqlite4java-1.0.392.jar"
	};

//	AmazonDynamoDB dynamodb = null;
//        try {
//            // Create an in-memory and in-process instance of DynamoDB Local that skips HTTP
//            dynamodb = DynamoDBEmbedded.create();
//            // use the DynamoDB API with DynamoDBEmbedded
//            listTables(dynamodb.listTables(), "DynamoDB Embedded");
//        } finally {
//            // Shutdown the thread pools in DynamoDB Local / Embedded
//            if(dynamodb != null) {
//                dynamodb.shutdown();
//            }
//        }
        
	//dynamodb.listTables();

	//DynamoDBProxyServer server = ServerRunner.createServerFromCommandLineArgs(localArgs);
	//server.start();

	/*AmazonDynamoDB dynamodb = new AmazonDynamoDBClient(new BasicAWSCredentials("AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"));
	 dynamodb.setEndpoint("http://localhost:8000");
	 dynamodb.listTables();*/
	//server.stop();
	//Thread.sleep(1000);
	/*createExampleTable();
	 listMyTables();
	 getTableInformation();
	 updateExampleTable();

	 deleteExampleTable();*/
	//server.stop();
    }
    
    public static void listTables(ListTablesResult result, String method) {
        System.out.println("found " + Integer.toString(result.getTableNames().size()) + " tables with " + method);
        for(String table : result.getTableNames()) {
            System.out.println(table);
        }
    }

    static void createExampleTable()
    {

	try
	{

	    ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
	    attributeDefinitions.add(new AttributeDefinition()
		    .withAttributeName("Id")
		    .withAttributeType("N"));

	    ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
	    keySchema.add(new KeySchemaElement()
		    .withAttributeName("Id")
		    .withKeyType(KeyType.HASH));

	    CreateTableRequest request = new CreateTableRequest()
		    .withTableName(tableName)
		    .withKeySchema(keySchema)
		    .withAttributeDefinitions(attributeDefinitions)
		    .withProvisionedThroughput(new ProvisionedThroughput()
			    .withReadCapacityUnits(5L)
			    .withWriteCapacityUnits(6L));

	    System.out.println("Issuing CreateTable request for " + tableName);
	    CreateTableResult table = dynamoDB.createTable(request);

	    System.out.println("Waiting for " + tableName + " to be created...this may take a while...");

	    getTableInformation();

	} catch (Exception e)
	{
	    System.err.println("CreateTable request failed for " + tableName);
	    System.err.println(e.getMessage());
	}

    }

    static void listMyTables()
    {
	ListTablesResult table = dynamoDB.listTables();

	System.out.println("Listing table names");
	System.out.println(table.getTableNames());
    }

    static void getTableInformation()
    {

	/*System.out.println("Describing " + tableName);

	 TableDescription tableDescription = dynamoDB.
	 getTable(tableName).describe();
	 System.out.format("Name: %s:\n" + "Status: %s \n"
	 + "Provisioned Throughput (read capacity units/sec): %d \n"
	 + "Provisioned Throughput (write capacity units/sec): %d \n",
	 tableDescription.getTableName(),
	 tableDescription.getTableStatus(),
	 tableDescription.getProvisionedThroughput().getReadCapacityUnits(),
	 tableDescription.getProvisionedThroughput().getWriteCapacityUnits());*/
    }

    static void updateExampleTable()
    {

	/*Table table = dynamoDB.getTable(tableName);
	 System.out.println("Modifying provisioned throughput for " + tableName);

	 try
	 {
	 table.updateTable(new ProvisionedThroughput()
	 .withReadCapacityUnits(6L).withWriteCapacityUnits(7L));

	 table.waitForActive();
	 } catch (Exception e)
	 {
	 System.err.println("UpdateTable request failed for " + tableName);
	 System.err.println(e.getMessage());
	 }*/
    }

    static void deleteExampleTable()
    {

	/*Table table = dynamoDB.getTable(tableName);
	 try
	 {
	 System.out.println("Issuing DeleteTable request for " + tableName);
	 table.delete();

	 System.out.println("Waiting for " + tableName
	 + " to be deleted...this may take a while...");

	 table.waitForDelete();
	 } catch (Exception e)
	 {
	 System.err.println("DeleteTable request failed for " + tableName);
	 System.err.println(e.getMessage());
	 }
	 */
    }

}
