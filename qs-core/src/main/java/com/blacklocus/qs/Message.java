/**
 * Copyright 2013 BlackLocus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blacklocus.qs;

import java.util.Map;

/**
 * This is a simple message abstraction.
 */
public interface Message {

    /**
     * Return the unique identifier for this message.
     */
    public String getId();

    /**
     * Return the unique message receipt used to signal an instance of this
     * message was received and can be acknowledged or deleted.
     */
    public String getReceipt();

    /**
     * Return the message body content.
     */
    public String getBody();

    /**
     * Return the message attributes as a map.
     */
    public Map<String, String> getAttributes();
}
