/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.integration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author Jimmy
 */
public class DBITest
{
    private static DynamoDBMapper mapper;
    private final String articleId = "123562qdf14";
    private final String userId = "alfons";

    @BeforeClass
    public static void setUpClass()
    {
	DynamoDBFactory.connectToDB();
	mapper = new DynamoDBMapper(DynamoDBFactory.getClient());
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
	DynamoDBFactory.createTables();
    }

    @After
    public void tearDown()
    {
	DynamoDBFactory.deleteTables();
    }
}
