/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.integration;

import de.loercher.rating.commons.RatingProperties;
import de.loercher.rating.commons.SecurityHelper;
import de.loercher.rating.feedback.AtomicUpdate;
import de.loercher.rating.feedback.DynamoDBConnector;
import java.io.IOException;
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
     * @throws java.io.IOException
     */
    @Test
    public void testUpdateCounter() throws IOException
    {
	Integer entryCounter = 1;
	Integer counterUpdate = 2;
	AtomicUpdate instance;
	instance = new AtomicUpdate(new DynamoDBConnector(new RatingProperties(new SecurityHelper())), "obsoleteCounter", "size", "Feedback");
	instance.updateCounter(articleId, entryCounter, counterUpdate);
    }
    
}
