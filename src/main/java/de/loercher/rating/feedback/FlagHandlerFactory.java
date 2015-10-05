/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jimmy
 */
@Component
public class FlagHandlerFactory
{
    public static final String FEEDBACK_TABLE_NAME = "Feedback";
    
    private final DynamoDBConnector connector;
    
    @Autowired
    public FlagHandlerFactory(DynamoDBConnector pDynamoDB)
    {
	connector = pDynamoDB;
    }
    
    public AtomicUpdate createAtomicUpdate(String pCounterName, String pSizeAttribute)
    {
	return new AtomicUpdate(connector, pCounterName, pSizeAttribute, FEEDBACK_TABLE_NAME);
    }
}
