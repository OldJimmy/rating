/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.integration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author Jimmy
 */
public class DBITest
{
    public static DynamoDBMapper mapper;
    public static final String articleId = DynamoDBFactory.TEST_ARTICLE_ID;
    public static final String userId = DynamoDBFactory.TEST_USER_ID;
    public static final ZonedDateTime time = ZonedDateTime.of(2000, 10, 3, 16, 10, 0, 0, ZoneId.of("Europe/Berlin"));

    @BeforeClass
    public static void setUpClass()
    {
	DynamoDBFactory.connectToDB();
	mapper = new DynamoDBMapper(DynamoDBFactory.getClient());
    }

    @Before
    public void setUp()
    {
	DynamoDBFactory.createTables();
    }
}