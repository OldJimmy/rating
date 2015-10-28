/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.commons;

/**
 *
 * @author Jimmy
 */
public class UserResourceNotFoundException extends Exception
{
    private final String userID;

    public UserResourceNotFoundException(String pUserID, String pError)
    {
	super(pError);
	userID = pUserID;
    }
    
    public String getUserID()
    {
	return userID;
    }
}
