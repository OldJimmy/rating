/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.commons.exception;

import java.time.ZonedDateTime;

/**
 *
 * @author Jimmy
 */

public class InappropriateContentException extends ArticleResourceNotFoundException
{
    private final ZonedDateTime release;
    
    public InappropriateContentException(String pArticleID, String pError, ZonedDateTime pRelease)
    {
	super(pArticleID, pError);
	release = pRelease;
    }

    public ZonedDateTime getRelease()
    {
	return release;
    }
}
