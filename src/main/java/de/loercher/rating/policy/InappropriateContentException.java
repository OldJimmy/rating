/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.policy;

/**
 *
 * @author Jimmy
 */
public class InappropriateContentException extends Exception
{
    private String error;
    
    public InappropriateContentException(String pError)
    {
	error = pError;
    }
}
