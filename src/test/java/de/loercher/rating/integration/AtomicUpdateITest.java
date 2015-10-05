/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.integration;

import de.loercher.rating.feedback.AtomicUpdate;
import de.loercher.rating.feedback.DynamoDBConnector;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jimmy
 */
public class AtomicUpdateITest extends DBITest
{
    
    private static String TABLENAME = "Feedback";
    
    public AtomicUpdateITest()
    {
    }
    
    
    @After
    public void tearDown()
    {
	//DynamoDBFactory.deleteTables();
    }

    /**
     * Test of updateCounter method, of class AtomicUpdate.
     */
    @Test
    public void testUpdateCounter() throws Exception
    {
	String articleId = DynamoDBFactory.TEST_ARTICLE_ID;
	Integer entryCounter = 1;
	Integer counterUpdate = 2;
	AtomicUpdate instance;
	instance = new AtomicUpdate(new DynamoDBConnector(), "obsoleteCounter", "size", "Feedback");
	instance.updateCounter(articleId, entryCounter, counterUpdate);
	
    }
    
}
