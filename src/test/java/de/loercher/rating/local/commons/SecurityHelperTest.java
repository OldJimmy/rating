/*
 * Copyright 2015 Pivotal Software, Inc..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.loercher.rating.local.commons;

import de.loercher.rating.commons.SecurityHelper;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jimmy
 */
public class SecurityHelperTest
{

    private SecurityHelper helper;

    public SecurityHelperTest()
    {

    }

    @Before
    public void setUp()
    {
	helper = new SecurityHelper();
    }

    @Test
    public void testSomeStrings()
    {
	String input = "abcd";
	String obfuscated = helper.obfuscateString(input);
	String unobfuscated = helper.unobfuscateString(obfuscated);

	assertNotEquals("Obfuscated string has to be different to original input!", input);
	assertEquals("Unobfuscated string has to be reconverted to the original input!", input, unobfuscated);

	input = "$1?=;ü+!,.-°^1jg?ß";
	obfuscated = helper.obfuscateString(input);
	unobfuscated = helper.unobfuscateString(obfuscated);

	assertNotEquals("Obfuscated string has to be different to original input!", input);
	assertEquals("Unobfuscated string has to be reconverted to the original input!", input, unobfuscated);
    }
    
    @Test
    public void testNull()
    {
	String input = null;
	assertNull("Null should be handled tolerantly. Return value should be null!", helper.obfuscateString(input));

	assertNull("Null should be handled tolerantly. Return value should be null!", helper.unobfuscateString(input));
    }

}
