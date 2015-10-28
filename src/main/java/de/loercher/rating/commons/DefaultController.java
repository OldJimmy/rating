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

import com.amazonaws.services.simpleworkflow.model.UnknownResourceException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Jimmy
 */
@RestController
@RequestMapping("localpress/")
public class DefaultController
{
    @RequestMapping(value = "/**/**", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void defaultRequest()
    {
	throw new UnknownResourceException("There is no resource on this path.");
    }
}
