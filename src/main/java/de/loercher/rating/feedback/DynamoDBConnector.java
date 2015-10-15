/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import de.loercher.rating.commons.RatingProperties;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jimmy
 */
@Component
public class DynamoDBConnector
{
    private final String ACCESS_KEY_NAME = "amazonAccessKey";
    private final String SECRET_KEY_NAME = "amazonSecretKey";
    private final String URL_NAME = "dynamodbUrl";

    private final RatingProperties ratingProperties;

    private final AmazonDynamoDB client;
    private final DynamoDB dynamoDB;

    @Autowired
    public DynamoDBConnector(RatingProperties properties) throws IOException
    {
	ratingProperties = properties;

	String accessKey = ratingProperties.getProp().getProperty(ACCESS_KEY_NAME);
	String secretKey = ratingProperties.getProp().getProperty(SECRET_KEY_NAME);
	client = new AmazonDynamoDBClient(new BasicAWSCredentials(accessKey, secretKey));
	
	client.setEndpoint(ratingProperties.getProp().getProperty(URL_NAME));
	dynamoDB = new DynamoDB(client);
    }

    public DynamoDB getDynamoDB()
    {
	return dynamoDB;
    }

    public AmazonDynamoDB getClient()
    {
	return client;
    }

}
