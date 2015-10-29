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
public class ArticleResourceNotFoundException extends Exception
{
    private final String articleID;

    public String getArticleID()
    {
	return articleID;
    }
    
    public ArticleResourceNotFoundException(String pArticleID, String pError)
    {
	super(pError);
	articleID = pArticleID;
    }
}
