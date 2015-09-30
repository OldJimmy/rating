/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.google.gson.Gson;
import java.time.ZonedDateTime;

/**
 * @author Jimmy
 */
@DynamoDBTable(tableName = "FeedbackEntry")
public class FeedbackEntryDataModel
{
    public static final Integer MAXRATING = 5;

    private String articleID;
    private String userID;

    private boolean positive = false;
    private boolean obsolete = false;
    private boolean obscene = false;
    private boolean copyright = false;

    private ZonedDateTime releaseDate;
    private Integer contentRating = null;
    private Integer styleRating = null;
    
    public FeedbackEntryDataModel(){}

    public FeedbackEntryDataModel(ZonedDateTime date, String pArticleID, String pUserID) throws IllegalArgumentException
    {
	if ((date == null) || (pArticleID == null) || (pUserID == null))
	    throw new IllegalArgumentException("Creation time, articleID and userID have to be set. ");
	
	articleID = pArticleID;
	userID = pUserID;
	
	releaseDate = date;
    }

    @DynamoDBHashKey(attributeName="ArticleID")
    public String getArticleID()
    {
	return articleID;
    }

    public void setArticleID(String ArticleID)
    {
	this.articleID = ArticleID;
    }
    
    @DynamoDBRangeKey(attributeName = "UserID")
    public String getUserID()
    {
	return userID;
    }

    public void setUserID(String userID)
    {
	this.userID = userID;
    }

    public Boolean getPositive()
    {
	return positive;
    }

    public void setPositive()
    {
	this.positive = true;
    }

    public void setPositive(Boolean positive)
    {
	this.positive = positive;
    }

    public Boolean getObsolete()
    {
	return obsolete;
    }

    public void setObsolete()
    {
	this.obsolete = true;
    }

    public void setObsolete(Boolean obsolete)
    {
	this.obsolete = obsolete;
    }

    public Boolean getObscene()
    {
	return obscene;
    }

    public void setObscene()
    {
	this.obscene = true;
    }

    public void setObscene(Boolean obscene)
    {
	this.obscene = obscene;
    }

    public Boolean getCopyright()
    {
	return copyright;
    }

    public void setCopyright(Boolean copyright)
    {
	this.copyright = copyright;
    }

    public void setCopyright()
    {
	this.copyright = true;
    }

    @DynamoDBMarshalling(marshallerClass = DateTimeConverter.class) 
    public ZonedDateTime getReleaseDate()
    {
	return releaseDate;
    }

    public void setReleaseDate(ZonedDateTime releaseDate)
    {
	this.releaseDate = releaseDate;
    }

    public Integer getContentRating()
    {
	return contentRating;
    }

    public void setContentRating(Integer contentRating) throws IllegalArgumentException
    {
	throwIfOutOfBounds(contentRating);

	this.contentRating = contentRating;
    }

    public Integer getStyleRating()
    {
	return styleRating;
    }

    public void setStyleRating(Integer styleRating) throws IllegalArgumentException
    {
	throwIfOutOfBounds(styleRating);

	this.styleRating = styleRating;
    }

    private void throwIfOutOfBounds(Integer rate) throws IllegalArgumentException
    {
	if (rate <= 0)
	{
	    throw new IllegalArgumentException("Rating number smaller than minimum:" + rate);
	}
	if (rate > FeedbackEntryDataModel.MAXRATING)
	{
	    throw new IllegalArgumentException("Rating number greater than maximum:" + rate);
	}
    }

    public String toJSON()
    {
	Gson gson = new Gson();

	String json = gson.toJson(this);
	System.out.println(json);

	return json;
    }
}
