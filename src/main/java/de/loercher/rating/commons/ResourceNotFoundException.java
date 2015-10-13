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

public class ResourceNotFoundException extends Exception
{
    private String id;

    public String getId()
    {
	return id;
    }

    public void setId(String id)
    {
	this.id = id;
    }
    
    public ResourceNotFoundException(String pError)
    {
	super(pError);
    }
}
