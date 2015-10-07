/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.local.feedback;

import de.loercher.rating.feedback.FeedbackEntryDataModel;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jimmy
 */
public class FeedbackEntryTest
{

    private FeedbackEntryDataModel entry;

    public FeedbackEntryTest()
    {
    }

    @Before
    public void setUp()
    {
	entry = new FeedbackEntryDataModel(ZonedDateTime.of(LocalDateTime.of(2015, Month.MARCH, 11, 12, 42), ZoneId.systemDefault()), "2134", "elfrich");
	entry.setCopyright();
	entry.setObsolete();
    }

    @Test
    public void testWrongContentRatingInput()
    {
	try
	{
	    entry.setContentRating(-1);
	    fail("ContentRating accepts negativ rating");
	} catch (IllegalArgumentException e)
	{
	}

	try
	{
	    entry.setContentRating(FeedbackEntryDataModel.MAXRATING + 1);
	    fail("ContentRating accepts rating bigger than its max ");
	} catch (IllegalArgumentException e)
	{
	}

	try
	{
	    entry.setStyleRating(-1);
	    fail("StyleRating accepts negativ rating");
	} catch (IllegalArgumentException e)
	{
	}

	try
	{
	    entry.setStyleRating(FeedbackEntryDataModel.MAXRATING + 1);
	    fail("StyleRating accepts rating bigger than its max ");
	} catch (IllegalArgumentException e)
	{
	}
    }

}
