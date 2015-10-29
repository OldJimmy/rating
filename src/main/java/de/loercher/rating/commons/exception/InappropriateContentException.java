/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.commons.exception;

/**
 *
 * @author Jimmy
 */

public class InappropriateContentException extends ArticleResourceNotFoundException
{
    public InappropriateContentException(String pArticleID, String pError)
    {
	super(pArticleID, pError);
    }
}
