/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

/**
 *
 * @author Jimmy
 */
public class FeedbackHelper
{
    public Integer calculateAddend(boolean oldEntry, boolean newEntry)
    {
	if (oldEntry == newEntry)
	{
	    return 0;
	}

	if (oldEntry && !newEntry)
	{
	    return -1;
	}

	if (newEntry && !oldEntry)
	{
	    return 1;
	}

	return null;
    }
}
