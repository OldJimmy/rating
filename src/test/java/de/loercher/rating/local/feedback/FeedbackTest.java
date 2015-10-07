/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.rating.local.feedback;

import com.google.gson.Gson;
import de.loercher.rating.feedback.FeedbackController;
import de.loercher.rating.feedback.FeedbackEntryDataModel;
import de.loercher.rating.policy.Policy;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jimmy
 */
public class FeedbackTest
{

    private Policy policy;
    private FeedbackController feedback;
    private Double oldRate;
    private FeedbackEntryDataModel firstRate;
    private final String USER = "max";
    private final String OTHERUSER = "moritz";

    public FeedbackTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
	policy = new Policy();
	feedback = new FeedbackController(ZonedDateTime.now(), "42");

	policy.setFeedback(feedback);
	oldRate = policy.getRating();

	firstRate = new FeedbackEntryDataModel(ZonedDateTime.now(), "83247", USER);
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testNotNull()
    {
	assertNotNull("Rating is null!!", policy.getRating());
    }

    @Test
    public void testMultipleUserEntries()
    {
	feedback.addRating(firstRate);
	Double ratingBefore = policy.getRating();

	FeedbackEntryDataModel secondRate = new FeedbackEntryDataModel(ZonedDateTime.now(), "5511234", USER);
	secondRate.setPositive();
	feedback.addRating(secondRate);

	assertTrue("Different count of ratings despite from self user!", feedback.getRatingCount() == 1);

	Double difference = ratingBefore - policy.getRating();
	assertTrue("Rating doesn't change though user changed his rating!", Math.abs(difference) > 0.000001);
    }

    @Test
    public void testMultipleUserEntriesSecondNegative()
    {
	firstRate.setPositive();
	feedback.addRating(firstRate);
	Double ratingBefore = policy.getRating();

	FeedbackEntryDataModel secondRate = new FeedbackEntryDataModel(ZonedDateTime.now(), "192784", USER);
	secondRate.setPositive(false);
	feedback.addRating(secondRate);

	assertTrue("Rating not smaller after negative Rating!", ratingBefore > policy.getRating());
    }

    @Test
    public void testAddPositiveEntry()
    {
	firstRate.setPositive();
	feedback.addRating(firstRate);

	assertTrue("Positive Rating doesn't effect rate positively!", oldRate < policy.getRating());
    }

    @Test
    public void testAddObsoleteEntry()
    {
	firstRate.setObsolete();
	feedback.addRating(firstRate);

	assertTrue("Obsolete rating should have effect on legit flag!", !(policy.isLegit()));

	feedback.addObsolete(false, USER);
	assertTrue("After reversing obsolete rating rating should be legit!", policy.isLegit());
    }

    @Test
    public void testAddObsceneEntry()
    {
	firstRate.setObscene();
	feedback.addRating(firstRate);

	assertTrue("Obscene rating should have effect on legit flag!", !(policy.isLegit()));

	feedback.addObscene(false, USER);
	assertTrue("After reversing obscene rating rating should be legit!", policy.isLegit());
    }

    @Test
    public void testAddCopyrighteEntry()
    {
	firstRate.setCopyright();
	feedback.addRating(firstRate);

	assertTrue("Copyright infringement should have effect on legit flag!", !(policy.isLegit()));

	feedback.addCopyright(false, USER);
	assertTrue("After reversing copyright infringement rating rating should be legit!", policy.isLegit());
    }

    @Test
    public void testAgeOfReleaseDate()
    {
	firstRate.setPositive();
	feedback.addRating(firstRate);
	oldRate = policy.getRating();

	feedback.setTimeOfPressEntry(feedback.getTimeOfPressEntry().minusDays(1));

	assertTrue("Change in time doesn't effect rate negatively!", oldRate > policy.getRating());
    }

    @Test
    public void testObscene()
    {
	firstRate.setObscene();
	feedback.addRating(firstRate);
	oldRate = policy.getRating();

	feedback.addObscene(true, USER);
	assertTrue("Obscene count should remain constant after inserting obscene twice from same user!", feedback.getObsceneCounter() == 1);
	//    assertTrue("Rating should remain constant after inserting obscene twice from same user!", isSimilar(oldRate, ratingManager.calculateRating()));

	feedback.addObscene(false, USER);
	assertTrue("Obscene count should be back to 0 after reverting obscene from user!", feedback.getObsceneCounter() == 0);

	feedback.addObscene(true, OTHERUSER);
	assertTrue("Obscene count should be 1 after another user added obscene flag!", feedback.getObsceneCounter() == 1);

	feedback.addObscene(true, USER);
	assertTrue("Obscene count should be 2 after user changed his mind the second time!", feedback.getObsceneCounter() == 2);

	System.out.println(feedback.toJSON());
    }

    @Test
    public void testObsolete()
    {
	firstRate.setObsolete();
	firstRate.setObscene();
	feedback.addRating(firstRate);
	oldRate = policy.getRating();

	feedback.addObsolete(true, USER);
	assertTrue("Obsolete count should remain constant after inserting obsolete twice from same user!", feedback.getObsoleteCounter() == 1);
	//    assertTrue("Rating should remain constant after inserting obscene twice from same user!", isSimilar(oldRate, ratingManager.calculateRating()));

	feedback.addObsolete(false, USER);
	assertTrue("Obsolete count should be back to 0 after reverting obsolete from user!", feedback.getObsoleteCounter() == 0);

	feedback.addObsolete(true, OTHERUSER);
	assertTrue("Obsolete count should be 1 after another user added obsolete flag!", feedback.getObsoleteCounter() == 1);

	feedback.addObsolete(true, USER);
	assertTrue("Obsolete count should be 2 after user changed his mind the second time!", feedback.getObsoleteCounter() == 2);
	
	assertTrue("Obscene count should be 1 after another user added obscene flag!", feedback.getObsceneCounter() == 1);
    }

    @Test
    public void testCopyright()
    {
	firstRate.setCopyright();
	feedback.addRating(firstRate);
	oldRate = policy.getRating();

	feedback.addCopyright(true, USER);
	assertTrue("Obscene count should remain constant after inserting obscene twice from same user!", feedback.getCopyrightCounter() == 1);
	//    assertTrue("Rating should remain constant after inserting obscene twice from same user!", isSimilar(oldRate, ratingManager.calculateRating()));

	feedback.addCopyright(false, USER);
	assertTrue("Obscene count should be back to 0 after reverting obscene from user!", feedback.getCopyrightCounter() == 0);

	feedback.addCopyright(true, OTHERUSER);
	assertTrue("Obscene count should be 1 after another user added obscene flag!", feedback.getCopyrightCounter() == 1);

	feedback.addCopyright(true, USER);
	assertTrue("Obscene count should be 2 after user changed his mind the second time!", feedback.getCopyrightCounter() == 2);
    }

    @Test
    public void testPositive()
    {
	firstRate.setPositive();
	feedback.addRating(firstRate);
	oldRate = policy.getRating();

	feedback.addPositive(true, USER);
	assertTrue("Obscene count should remain constant after inserting obscene twice from same user!", feedback.getPositiveCounter() == 1);
	//    assertTrue("Rating should remain constant after inserting obscene twice from same user!", isSimilar(oldRate, ratingManager.calculateRating()));

	feedback.addPositive(false, USER);
	assertTrue("Obscene count should be back to 0 after reverting obscene from user!", feedback.getPositiveCounter() == 0);

	feedback.addPositive(true, OTHERUSER);
	assertTrue("Obscene count should be 1 after another user added obscene flag!", feedback.getPositiveCounter() == 1);

	feedback.addPositive(true, USER);
	assertTrue("Obscene count should be 2 after user changed his mind the second time!", feedback.getPositiveCounter() == 2);
    }


    @Test
    public void loadFeedbackEntries() throws UnsupportedEncodingException, URISyntaxException, IOException
    {
	ClassLoader classLoader = getClass().getClassLoader();
	File file = new File(classLoader.getResource("feedback").getFile());
	String json = new String(Files.readAllBytes(file.toPath()), "UTF8");
	
	Gson gson = new Gson();
	Map michi = gson.fromJson(json, Map.class);
	
	System.out.println("Geladenes JSON: " + json);
    }
    
    private boolean isSimilar(Double a, Double b)
    {
	Double difference = a - b;
	return (Math.abs(difference) < 0.000001);
    }
}
