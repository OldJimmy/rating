/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import java.time.ZonedDateTime;

/**
 *
 * @author Jimmy
 */
@DynamoDBTable(tableName = "Feedback")
public class FeedbackDataModel
{
    public static final String POSITIVE_COUNTER_NAME = "positiveCounter";
    public static final String COPYRIGHT_COUNTER_NAME = "copyrightCounter";
    public static final String OBSCENE_COUNTER_NAME = "obsceneCounter";
    public static final String OBSOLETE_COUNTER_NAME = "obsoleteCounter";
    public static final String WRONG_COUNTER_NAME = "falseCounter";
    
    public static final String WRONG_CATEGORY_COUNTER_NAME = "wrongCategoryCounter";
    public static final String WRONG_PLACE_COUNTER_NAME = "wrongPlaceCounter";
    
    public static final String SIZE_NAME = "size";

    private String articleID;

    private Integer size = 0;

    private Integer positiveCounter = 0;
    private Integer obsoleteCounter = 0;
    private Integer obsceneCounter = 0;
    private Integer copyrightCounter = 0;
    private Integer wrongCounter = 0;

    private Integer wrongCategoryCounter = 0;
    private Integer wrongPlaceCounter = 0;

    private ZonedDateTime timeOfPressEntry;

    public FeedbackDataModel()
    {
    }

    public FeedbackDataModel(String pArticleID, ZonedDateTime pTimeOfPressEntry)
    {
	timeOfPressEntry = pTimeOfPressEntry;
	articleID = pArticleID;
    }

    @DynamoDBHashKey(attributeName = "ArticleID")
    public String getArticleID()
    {
	return articleID;
    }

    public void setArticleID(String articleID)
    {
	this.articleID = articleID;
    }

    @DynamoDBMarshalling(marshallerClass = DateTimeConverter.class)
    public ZonedDateTime getTimeOfPressEntry()
    {
	return timeOfPressEntry;
    }

    public void setTimeOfPressEntry(ZonedDateTime timeOfPressEntry)
    {
	this.timeOfPressEntry = timeOfPressEntry;
    }

    public Integer getSize()
    {
	return size;
    }

    public void setSize(Integer size)
    {
	this.size = size;
    }

    public Integer getWrongCategoryCounter()
    {
	return wrongCategoryCounter;
    }

    public void setWrongCategoryCounter(Integer wrongCategoryCounter)
    {
	this.wrongCategoryCounter = wrongCategoryCounter;
    }

    public Integer getWrongPlaceCounter()
    {
	return wrongPlaceCounter;
    }

    public void setWrongPlaceCounter(Integer wrongPlaceCounter)
    {
	this.wrongPlaceCounter = wrongPlaceCounter;
    }

    public Integer getPositiveCounter()
    {
	return positiveCounter;
    }

    public void setPositiveCounter(Integer positiveCounter)
    {
	this.positiveCounter = positiveCounter;
    }

    public Integer getObsoleteCounter()
    {
	return obsoleteCounter;
    }

    public void setObsoleteCounter(Integer obsoleteCounter)
    {
	this.obsoleteCounter = obsoleteCounter;
    }

    public Integer getObsceneCounter()
    {
	return obsceneCounter;
    }

    public void setObsceneCounter(Integer obsceneCounter)
    {
	this.obsceneCounter = obsceneCounter;
    }

    public Integer getCopyrightCounter()
    {
	return copyrightCounter;
    }

    public void setCopyrightCounter(Integer copyrightCounter)
    {
	this.copyrightCounter = copyrightCounter;
    }
    
     public Integer getWrongCounter()
    {
	return wrongCounter;
    }

    public void setWrongCounter(Integer falseCounter)
    {
	this.wrongCounter = falseCounter;
    }
}
