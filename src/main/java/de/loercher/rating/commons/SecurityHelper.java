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
package de.loercher.rating.commons;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jimmy
 */
@Component
public class SecurityHelper
{
    private final static String OBFUSCATION_KEY = "lalk490piu2";
    
    public SecurityHelper()
    {
	Security.addProvider(new BouncyCastleProvider());
    }
    
    public String unobfuscateString(String input)
    {
	StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();

	enc.setProviderName(BouncyCastleProvider.PROVIDER_NAME);
	enc.setPassword(OBFUSCATION_KEY);

	return enc.decrypt(input);
    }
    
    public String obfuscateString(String input)
    {
	StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();

	enc.setProviderName(BouncyCastleProvider.PROVIDER_NAME);
	enc.setPassword(OBFUSCATION_KEY);

	return enc.encrypt(input);
    }
}
