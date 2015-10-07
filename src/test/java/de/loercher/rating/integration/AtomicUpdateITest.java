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

/**
 *
 * @author Jimmy
 */
public class AtomicUpdateITest extends DBITest
{
    
    public AtomicUpdateITest()
    {
    }
    
    
    @After
    public void tearDown()
    {
	DynamoDBFactory.deleteTables();
    }

    /**
     * Test of updateCounter method, of class AtomicUpdate.
     */
    @Test
    public void testUpdateCounter()
    {
	Integer entryCounter = 1;
	Integer counterUpdate = 2;
	AtomicUpdate instance;
	instance = new AtomicUpdate(new DynamoDBConnector(), "obsoleteCounter", "size", "Feedback");
	instance.updateCounter(articleId, entryCounter, counterUpdate);
    }
    
}
