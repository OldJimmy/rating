/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.feedback;

import de.loercher.rating.commons.DateTimeConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jimmy
 */
@DynamoDBTable(tableName = "FeedbackEntry")
public class FeedbackEntryDataModel implements Cloneable
{

    public static final String KEY_NAME = "ArticleID";
    public static final String RANGE_NAME = "UserID";

    public static final Integer MAXRATING = 5;

    private String articleID;
    private String userID;

    private boolean positive = false;
    private boolean obsolete = false;
    private boolean obscene = false;
    private boolean copyright = false;
    private boolean wrong = false;

    private ZonedDateTime releaseDate;
    private Integer contentRating = null;
    private Integer styleRating = null;

    private Long version;

    public FeedbackEntryDataModel()
    {
    }

    public FeedbackEntryDataModel(ZonedDateTime date, String pArticleID, String pUserID) throws IllegalArgumentException
    {
	if ((date == null) || (pArticleID == null) || (pUserID == null))
	{
	    throw new IllegalArgumentException("Creation time, articleID and userID have to be set. ");
	}

	articleID = pArticleID;
	userID = pUserID;

	releaseDate = date;
    }

    @DynamoDBHashKey(attributeName = KEY_NAME)
    public String getArticleID()
    {
	return articleID;
    }

    public void setArticleID(String ArticleID)
    {
	this.articleID = ArticleID;
    }

    @DynamoDBRangeKey(attributeName = RANGE_NAME)
    public String getUserID()
    {
	return userID;
    }

    public void setUserID(String userID)
    {
	this.userID = userID;
    }

    public Boolean isPositive()
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

    public Boolean isObsolete()
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

    public Boolean isObscene()
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

    public Boolean isCopyright()
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

    public Boolean isWrong()
    {
	return wrong;
    }

    public void setWrong(Boolean wrong)
    {
	this.wrong = wrong;
    }

    public void setWrong()
    {
	this.wrong = true;
    }

    @DynamoDBVersionAttribute
    public Long getVersion()
    {
	return version;
    }

    public void setVersion(Long version)
    {
	this.version = version;
    }

    @DynamoDBIgnore
    public FeedbackEntryDataModel cloneEntry()
    {
	FeedbackEntryDataModel newModel = new FeedbackEntryDataModel(releaseDate, articleID, userID);
	newModel.setCopyright(copyright);
	newModel.setObscene(obscene);
	newModel.setObsolete(obsolete);
	newModel.setPositive(positive);
	newModel.setVersion(version);
	newModel.setWrong(wrong);

	if (styleRating != null)
	{
	    newModel.setStyleRating(styleRating);
	}
	if (contentRating != null)
	{
	    newModel.setContentRating(contentRating);
	}

	return newModel;
    }

    public Map<String, Object> toMap()
    {
	Map<String, Object> result = new HashMap<>();
	result.put("positive", positive);
	result.put("obsolete", obsolete);
	result.put("obscene", obscene);
	result.put("copyright", copyright);
	result.put("wrong", wrong);
	result.put("releaseDate", new DateTimeConverter().marshall(releaseDate));

	if (contentRating == null)
	{
	    result.put("contentRating", "n/a");
	} else
	{
	    result.put("contentRating", contentRating);
	}

	if (styleRating == null)
	{
	    result.put("styleRating", "n/a");
	} else
	{
	    result.put("styleRating", styleRating);
	}

	return result;
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

}
