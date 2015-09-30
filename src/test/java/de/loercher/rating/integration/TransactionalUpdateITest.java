/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.integration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import de.loercher.rating.feedback.FeedbackDataModel;
import de.loercher.rating.feedback.FeedbackEntryDataModel;
import de.loercher.rating.feedback.TransactionalUpdate;
import java.time.ZonedDateTime;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Jimmy
 */
public class TransactionalUpdateITest
{

    private static final String OBSOLETE_COUNTER = "obsoleteCounter";
    private static DynamoDBMapper mapper;
    private final String ARTICLEID = "123562qdf14";
    private final String USERID = "alfons";

    public TransactionalUpdateITest()
    {
    }

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
    public void testMultipleUpdatesSameEntry()
    {
	ZonedDateTime now = ZonedDateTime.now();

	FeedbackDataModel feedback = new FeedbackDataModel(ARTICLEID, now);
	mapper.save(feedback);

	FeedbackEntryDataModel entry = new FeedbackEntryDataModel(now, ARTICLEID, USERID);

	entry.setContentRating(1);
	entry.setStyleRating(1);

	TransactionalUpdate update = new TransactionalUpdate(ARTICLEID, OBSOLETE_COUNTER, (a) -> a.getObsolete(), (b, c) -> b.setObsolete(c));
	entry = update.updateFlag(entry, Boolean.TRUE, 1);

	assertTrue("Entry should have set obsolete flag!", entry.getObsolete());

	feedback = mapper.load(feedback);
	assertTrue("Obsolete counter should be increased by 1!", feedback.getObsoleteCounter() == 1);

	update = new TransactionalUpdate(ARTICLEID, OBSOLETE_COUNTER, (a) -> a.getObsolete(), (b, c) -> b.setObsolete(c));
	entry = update.updateFlag(entry, Boolean.TRUE, 1);

	feedback = mapper.load(feedback);
	assertTrue("Obsolete counter shouldn't have changed since flag keeps being the same!", feedback.getObsoleteCounter() == 1);

	TransactionalUpdate update2 = new TransactionalUpdate(ARTICLEID, OBSOLETE_COUNTER, (a) -> a.getObsolete(), (b, c) -> b.setObsolete(c));
	entry = update2.updateFlag(entry, Boolean.FALSE, 1);

	feedback = mapper.load(feedback);
	assertTrue("Obsolete counter should be back to 0!", feedback.getObsoleteCounter() == 0);

	update2 = new TransactionalUpdate(ARTICLEID, OBSOLETE_COUNTER, (a) -> a.getObsolete(), (b, c) -> b.setObsolete(c));
	update2.updateFlag(entry, Boolean.FALSE, 1);

	feedback = mapper.load(feedback);
	assertTrue("Obsolete counter shouldn't have changed since flag keeps being the same!", feedback.getObsoleteCounter() == 0);
    }

    @Test
    public void testMultipleUpdatesCheckCounter()
    {
	ZonedDateTime now = ZonedDateTime.now();

	FeedbackDataModel feedback = new FeedbackDataModel(ARTICLEID, now);
	mapper.save(feedback);

	FeedbackEntryDataModel entry = new FeedbackEntryDataModel(now, ARTICLEID, USERID);

	TransactionalUpdate update = new TransactionalUpdate(ARTICLEID, OBSOLETE_COUNTER, (a) -> a.getObsolete(), (b, c) -> b.setObsolete(c));
	entry = update.updateFlag(entry, Boolean.TRUE, 5);

	feedback = mapper.load(feedback);
	assertTrue("Database entity should have correct obsolete counter (5)!", feedback.getSize() == 5);

	Integer oldSize = feedback.getSize();
	Integer oldObsoleteSize = feedback.getObsoleteCounter();
	
	try
	{
	    TransactionalUpdate update2 = new TransactionalUpdate(ARTICLEID, OBSOLETE_COUNTER, (a) -> a.getObsolete(), (b, c) -> b.setObsolete(c));
	    entry = update.updateFlag(entry, Boolean.FALSE, -5);
	    
	    fail("TransactionUpdate update MUST NOT accept non-positive values!");
	} catch (IllegalArgumentException e){}
	
	
	feedback = mapper.load(feedback);
	assertTrue("Flag MUST NOT be changed if non-positive value is forwarded as size!", entry.getObsolete());
	
	assertTrue("Size counter MUST NOT be changed if non-positive value is forwarded as size!", feedback.getSize().equals(oldSize));
	assertTrue("Obsolete counter MUST NOT be changed if non-positive value is forwarded as size!", feedback.getObsoleteCounter().equals(oldObsoleteSize));
	
	try
	{
	    TransactionalUpdate update2 = new TransactionalUpdate(ARTICLEID, OBSOLETE_COUNTER, (a) -> a.getObsolete(), (b, c) -> b.setObsolete(c));
	    entry = update.updateFlag(entry, Boolean.FALSE, 0);
	    
	    fail("TransactionUpdate update MUST NOT accept 0!");
	} catch (IllegalArgumentException e){}
    }

}
