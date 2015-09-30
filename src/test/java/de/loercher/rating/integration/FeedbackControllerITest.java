/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.integration;

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
public class FeedbackControllerITest extends DBITest
{
    
    public FeedbackControllerITest()
    {
    }
    
    @After
    public void tearDown()
    {
	DynamoDBFactory.deleteTables();
    }

}
