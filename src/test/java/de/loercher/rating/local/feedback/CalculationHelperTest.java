/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.local.feedback;

import de.loercher.rating.feedback.FeedbackHelper;
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
public class CalculationHelperTest
{
    
    public CalculationHelperTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown()
    {
    }

    @Test
    public void testSameEntriesFalse()
    {
	boolean oldEntry = false;
	boolean newEntry = false;
	FeedbackHelper instance = new FeedbackHelper();
	
	Integer expResult = 0;
	Integer result = instance.calculateAddend(oldEntry, newEntry);
	
	assertEquals("By using false either as new and old entry, the result has to be 0!", expResult, result);
    }
    
    @Test
    public void testSameEntriesTrue()
    {
	boolean oldEntry = true;
	boolean newEntry = true;
	FeedbackHelper instance = new FeedbackHelper();
	
	Integer expResult = 0;
	Integer result = instance.calculateAddend(oldEntry, newEntry);
	
	assertEquals("By using true either as new and old entry, the result has to be 0!", expResult, result);
    }
    
    @Test
    public void testEntriesOldTrueNewFalse()
    {
	boolean oldEntry = true;
	boolean newEntry = false;
	FeedbackHelper instance = new FeedbackHelper();
	
	Integer expResult = -1;
	Integer result = instance.calculateAddend(oldEntry, newEntry);
	
	assertEquals("By using false as new and true as old entry, the result has to be -1!", expResult, result);
    }
    
    @Test
    public void testEntriesOldFalseNewTrue()
    {
	boolean oldEntry = false;
	boolean newEntry = true;
	FeedbackHelper instance = new FeedbackHelper();
	
	Integer expResult = 1;
	Integer result = instance.calculateAddend(oldEntry, newEntry);
	
	assertEquals("By using true as new and false as old entry, the result has to be 1!", expResult, result);
    }
}
