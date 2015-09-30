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
    private FeedbackController rating;
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
	rating = new FeedbackController(ZonedDateTime.now(), "42");

	policy.setFeedback(rating);
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
	rating.addRating(firstRate);
	Double ratingBefore = policy.getRating();

	FeedbackEntryDataModel secondRate = new FeedbackEntryDataModel(ZonedDateTime.now(), "5511234", USER);
	secondRate.setPositive();
	rating.addRating(secondRate);

	assertTrue("Different count of ratings despite from self user!", rating.getRatingCount() == 1);

	Double difference = ratingBefore - policy.getRating();
	assertTrue("Rating doesn't change though user changed his rating!", Math.abs(difference) > 0.000001);
    }

    @Test
    public void testMultipleUserEntriesSecondNegative()
    {
	firstRate.setPositive();
	rating.addRating(firstRate);
	Double ratingBefore = policy.getRating();

	FeedbackEntryDataModel secondRate = new FeedbackEntryDataModel(ZonedDateTime.now(), "192784", USER);
	secondRate.setPositive(false);
	rating.addRating(secondRate);

	assertTrue("Rating not smaller after negative Rating!", ratingBefore > policy.getRating());
    }

    @Test
    public void testAddPositiveEntry()
    {
	firstRate.setPositive();
	rating.addRating(firstRate);

	assertTrue("Positive Rating doesn't effect rate positively!", oldRate < policy.getRating());
    }

    @Test
    public void testAddObsoleteEntry()
    {
	firstRate.setObsolete();
	rating.addRating(firstRate);

	assertTrue("Obsolete rating should have effect on legit flag!", !(policy.isLegit()));

	rating.addObsolete(false, USER);
	assertTrue("After reversing obsolete rating rating should be legit!", policy.isLegit());
    }

    @Test
    public void testAddObsceneEntry()
    {
	firstRate.setObscene();
	rating.addRating(firstRate);

	assertTrue("Obscene rating should have effect on legit flag!", !(policy.isLegit()));

	rating.addObscene(false, USER);
	assertTrue("After reversing obscene rating rating should be legit!", policy.isLegit());
    }

    @Test
    public void testAddCopyrighteEntry()
    {
	firstRate.setCopyright();
	rating.addRating(firstRate);

	assertTrue("Copyright infringement should have effect on legit flag!", !(policy.isLegit()));

	rating.addCopyright(false, USER);
	assertTrue("After reversing copyright infringement rating rating should be legit!", policy.isLegit());
    }

    @Test
    public void testAgeOfReleaseDate()
    {
	firstRate.setPositive();
	rating.addRating(firstRate);
	oldRate = policy.getRating();

	rating.setTimeOfPressEntry(rating.getTimeOfPressEntry().minusDays(1));

	assertTrue("Change in time doesn't effect rate negatively!", oldRate > policy.getRating());
    }

    @Test
    public void testObscene()
    {
	firstRate.setObscene();
	rating.addRating(firstRate);
	oldRate = policy.getRating();

	rating.addObscene(true, USER);
	assertTrue("Obscene count should remain constant after inserting obscene twice from same user!", rating.getObsceneCounter() == 1);
	//    assertTrue("Rating should remain constant after inserting obscene twice from same user!", isSimilar(oldRate, ratingManager.calculateRating()));

	rating.addObscene(false, USER);
	assertTrue("Obscene count should be back to 0 after reverting obscene from user!", rating.getObsceneCounter() == 0);

	rating.addObscene(true, OTHERUSER);
	assertTrue("Obscene count should be 1 after another user added obscene flag!", rating.getObsceneCounter() == 1);

	rating.addObscene(true, USER);
	assertTrue("Obscene count should be 2 after user changed his mind the second time!", rating.getObsceneCounter() == 2);

	System.out.println(rating.toJSON());
    }

    @Test
    public void testObsolete()
    {
	firstRate.setObsolete();
	firstRate.setObscene();
	rating.addRating(firstRate);
	oldRate = policy.getRating();

	rating.addObsolete(true, USER);
	assertTrue("Obsolete count should remain constant after inserting obsolete twice from same user!", rating.getObsoleteCounter() == 1);
	//    assertTrue("Rating should remain constant after inserting obscene twice from same user!", isSimilar(oldRate, ratingManager.calculateRating()));

	rating.addObsolete(false, USER);
	assertTrue("Obsolete count should be back to 0 after reverting obsolete from user!", rating.getObsoleteCounter() == 0);

	rating.addObsolete(true, OTHERUSER);
	assertTrue("Obsolete count should be 1 after another user added obsolete flag!", rating.getObsoleteCounter() == 1);

	rating.addObsolete(true, USER);
	assertTrue("Obsolete count should be 2 after user changed his mind the second time!", rating.getObsoleteCounter() == 2);
	
	assertTrue("Obscene count should be 1 after another user added obscene flag!", rating.getObsceneCounter() == 1);
    }

    @Test
    public void testCopyright()
    {
	firstRate.setCopyright();
	rating.addRating(firstRate);
	oldRate = policy.getRating();

	rating.addCopyright(true, USER);
	assertTrue("Obscene count should remain constant after inserting obscene twice from same user!", rating.getCopyrightCounter() == 1);
	//    assertTrue("Rating should remain constant after inserting obscene twice from same user!", isSimilar(oldRate, ratingManager.calculateRating()));

	rating.addCopyright(false, USER);
	assertTrue("Obscene count should be back to 0 after reverting obscene from user!", rating.getCopyrightCounter() == 0);

	rating.addCopyright(true, OTHERUSER);
	assertTrue("Obscene count should be 1 after another user added obscene flag!", rating.getCopyrightCounter() == 1);

	rating.addCopyright(true, USER);
	assertTrue("Obscene count should be 2 after user changed his mind the second time!", rating.getCopyrightCounter() == 2);
    }

    @Test
    public void testPositive()
    {
	firstRate.setPositive();
	rating.addRating(firstRate);
	oldRate = policy.getRating();

	rating.addPositive(true, USER);
	assertTrue("Obscene count should remain constant after inserting obscene twice from same user!", rating.getPositiveCounter() == 1);
	//    assertTrue("Rating should remain constant after inserting obscene twice from same user!", isSimilar(oldRate, ratingManager.calculateRating()));

	rating.addPositive(false, USER);
	assertTrue("Obscene count should be back to 0 after reverting obscene from user!", rating.getPositiveCounter() == 0);

	rating.addPositive(true, OTHERUSER);
	assertTrue("Obscene count should be 1 after another user added obscene flag!", rating.getPositiveCounter() == 1);

	rating.addPositive(true, USER);
	assertTrue("Obscene count should be 2 after user changed his mind the second time!", rating.getPositiveCounter() == 2);
    }

    private boolean isSimilar(Double a, Double b)
    {
	Double difference = a - b;
	return (Math.abs(difference) < 0.000001);
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
    
    @Test
    public void getJSONFromFeedback()
    {
	System.out.println("RATING ALS JSON: " + rating.toJSON());
    }
}
