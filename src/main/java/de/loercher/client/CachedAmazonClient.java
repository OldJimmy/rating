/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.client;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import de.loercher.rating.feedback.FeedbackController;
import java.io.ByteArrayInputStream;

/**
 *
 * @author Jimmy
 */
public class CachedAmazonClient
{

    static String tableName = "ExampleTable";
    
   // private final AmazonS3 client;
    //  private final ClientConfiguration clientConfig;
    public CachedAmazonClient() throws Exception
    {
	final String[] localArgs =
	{
	    "-inMemory",
	    "-sharedDb",
	    "java.library.path=D:\\REST\\DynamoDB\\DynamoDBLocal_lib"
	};
	
//	DynamoDBProxyServer server = ServerRunner.createServerFromCommandLineArgs(localArgs);
//	server.start();
	
	AWSCredentials credentials = new BasicAWSCredentials("asdasd", "asdsd");
	AmazonDynamoDB dynamodb = new AmazonDynamoDBClient(credentials);
	dynamodb.setEndpoint("http://localhost:8000");
	//dynamodb.listTables();
	
//	server.stop();
    }
    
    public FeedbackController loadFromBucket(String pBucketName)
    {
	return null;
    }

    public void saveToBucket(String pBucketName, ByteArrayInputStream pInput)
    {
	//client.putObject(pBucketName, "testInpute", new ByteArrayInputStream("Hello World".getBytes()), new ObjectMetadata());
    }

    public static void main(String[] args) throws Exception
    {
	new CachedAmazonClient().saveToBucket("localpresstest", new ByteArrayInputStream("Helsslo World".getBytes()));
    }
}
