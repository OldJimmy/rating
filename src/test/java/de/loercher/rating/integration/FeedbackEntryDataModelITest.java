/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.integration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import de.loercher.rating.feedback.FeedbackEntryDataModel;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jimmy
 */
public class FeedbackEntryDataModelITest
{

    private static DynamoDBMapper mapper;
    private final String articleId = "123562qdf14";
    private final String userId = "alfons";

    public FeedbackEntryDataModelITest()
    {}

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

    @Test
    public void testTimezones()
    {
	LocalDateTime localTime = LocalDateTime.of(2000, 12, 01, 0, 0, 0, 0);
	ZonedDateTime then = ZonedDateTime.of(localTime, ZoneId.of("Asia/Karachi"));

	FeedbackEntryDataModel entry = new FeedbackEntryDataModel(then, articleId, userId);

	mapper.save(entry);

	FeedbackEntryDataModel newEntry = mapper.load(FeedbackEntryDataModel.class, articleId, userId);
	
	ZonedDateTime time = newEntry.getReleaseDate();
	LocalDateTime localNewEntryTime = time.toLocalDateTime();
	
	assertEquals("From database loaded entry contains other date than saved!", localTime, localNewEntryTime);

	ZoneId here = ZoneId.of("Europe/Paris");
	ZonedDateTime hereTime = time.withZoneSameInstant(here);
	LocalDateTime europeEntryTime = hereTime.toLocalDateTime();
	
	assertEquals("The difference between timezones (Asia/Karachi) and (Europe/Paris) is wrong!", localNewEntryTime.getHour() + 20, europeEntryTime.getHour());
    }

    @Test
    public void testMultipleWrites()
    {
	ZonedDateTime now = ZonedDateTime.now();

	FeedbackEntryDataModel entry = new FeedbackEntryDataModel(now, articleId, userId);

	entry.setObsolete();
	entry.setContentRating(1);
	entry.setStyleRating(1);
	
	mapper.save(entry);

	FeedbackEntryDataModel neuesEntry = mapper.load(FeedbackEntryDataModel.class, articleId, userId);

	assertFalse("Entry attribute obscene wasn't set yet. Should be false! ", neuesEntry.isObscene());

	entry.setObscene();
	mapper.save(entry);

	neuesEntry = mapper.load(FeedbackEntryDataModel.class, "123562qdf14", "alfons");
	assertTrue("Entry attribute obscene not overwritten as expected! ", neuesEntry.isObscene());
    }
    
}
