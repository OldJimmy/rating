/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

/**
 *
 * @author Jimmy
 */
public class AtomicUpdate
{
    private final DynamoDB dynamoDB;
    private final String counterName;
    private final String sizeAttribute; 
    private final String tableName;

    public AtomicUpdate(DynamoDBConnector pDynamoDB, String pCounterName, String pSizeAttribute, String pTableName)
    {
	dynamoDB = pDynamoDB.getDynamoDB();
	counterName = pCounterName;
	sizeAttribute = pSizeAttribute;
	tableName = pTableName;
    }

    public UpdateItemOutcome updateCounter(String articleId, Integer entryCounterUpdate, Integer counterUpdate)
    {
	Table table = dynamoDB.getTable(tableName);

	UpdateItemSpec spec = new UpdateItemSpec()
		.withPrimaryKey("ArticleID", articleId)
		.withUpdateExpression("set #field = #field + :one, #counter = #counter + :newcounter")
		.withNameMap(new NameMap().with("#field", counterName).with("#counter", sizeAttribute))
		.withValueMap(new ValueMap()
			.withNumber(":newcounter", entryCounterUpdate)
			.withNumber(":one", counterUpdate));

	return table.updateItem(spec);
    }

}
