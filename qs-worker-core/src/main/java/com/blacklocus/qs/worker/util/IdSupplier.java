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
package com.blacklocus.qs.worker.util;

import com.github.rholder.fauxflake.IdGenerators;
import com.github.rholder.fauxflake.api.IdGenerator;

public class IdSupplier {

    public static final IdGenerator ID_GENERATOR = IdGenerators.newSnowflakeIdGenerator();

    /**
     * Throws no checked exceptions; just a runtime exception if an ID can't be generated
     * within 50ms. Which seems like it should usually be enough time...
     *
     * @return a new ID
     */
    public static String newId() {
        Long id;
        try {
            id = ID_GENERATOR.generateId(50).asLong();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Couldn't generate an ID");
        }
        return id.toString();
    }

}
