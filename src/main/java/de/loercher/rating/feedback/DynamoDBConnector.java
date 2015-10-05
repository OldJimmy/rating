/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

/**
 *
 * @author Jimmy
 */
public class DynamoDBConnector
{
    private final AmazonDynamoDBClient client = new AmazonDynamoDBClient(new BasicAWSCredentials("AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"));
    private final DynamoDB dynamoDB;
    
    public DynamoDBConnector()
    {
	client.setEndpoint("http://localhost:8000");
	dynamoDB = new DynamoDB(client);
    }
    
    public DynamoDB getDynamoDB()
    {
	return dynamoDB;
    }
    
    public AmazonDynamoDBClient getClient()
    {
	return client;
    }
    
    
}
