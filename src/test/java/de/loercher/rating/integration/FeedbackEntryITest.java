/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.integration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.Item;
import de.loercher.rating.feedback.Feedback;
import de.loercher.rating.feedback.FeedbackEntry;
import de.loercher.rating.feedback.TransactionalUpdate;
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
public class FeedbackEntryITest
{

    private static DynamoDBMapper mapper;
    private final String articleId = "123562qdf14";
    private final String userId = "alfons";

    public FeedbackEntryITest()
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
	//DynamoDBFactory.deleteTables();
    }

    @Test
    public void testTimezones()
    {
	LocalDateTime localTime = LocalDateTime.of(2000, 12, 01, 0, 0, 0, 0);
	ZonedDateTime then = ZonedDateTime.of(localTime, ZoneId.of("Asia/Karachi"));

	FeedbackEntry entry = new FeedbackEntry(then, articleId, userId);

	mapper.save(entry);

	FeedbackEntry newEntry = mapper.load(FeedbackEntry.class, articleId, userId);
	
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

	FeedbackEntry entry = new FeedbackEntry(now, articleId, userId);

	entry.setObsolete();
	entry.setContentRating(1);
	entry.setStyleRating(1);
	
	mapper.save(entry);

	FeedbackEntry neuesEntry = mapper.load(FeedbackEntry.class, articleId, userId);

	assertFalse("Entry attribute obscene wasn't set yet. Should be false! ", neuesEntry.getObscene());

	entry.setObscene();
	mapper.save(entry);

	neuesEntry = mapper.load(FeedbackEntry.class, "123562qdf14", "alfons");
	assertTrue("Entry attribute obscene not overwritten as expected! ", neuesEntry.getObscene());
    }
    
    @Test
    public void testAtomicFeedbackUpdate()
    {
	
	
	ZonedDateTime now = ZonedDateTime.now();
	
	//Feedback feedback = new Feedback(now, articleId );
	
	FeedbackEntry entry = new FeedbackEntry(now, articleId, userId);

	entry.setObsolete();
	entry.setContentRating(1);
	entry.setStyleRating(1);
	
	TransactionalUpdate obsoleteUpdate = new TransactionalUpdate(articleId, "obsoleteCounter", (a) -> a.getObsolete(), (b, c) -> b.setObsolete(c));
	obsoleteUpdate.updateFlag(entry, true, 0);
    }
}
