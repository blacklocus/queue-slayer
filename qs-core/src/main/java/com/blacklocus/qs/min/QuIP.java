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
package com.blacklocus.qs.min;

import com.blacklocus.qs.QueueItemProvider;

import java.util.Collection;
import java.util.Iterator;

/**
 * Identical to {@link QueueItemProvider} but much shorter class name.
 *
 * @author Jason Dunkelberger (dirkraft)
 */
public interface QuIP<Q> extends Iterator<Collection<Q>>, Iterable<Collection<Q>> {

    /**
     * Generally returns <code>this</code>. QueueItemProviders are not generally reusable/resettable since the
     * underlying message source will often not be reversible. Mashing the two interfaces together simplifies
     * implementations a bit, and also supports any typical Iterable usage.
     */
    @Override
    Iterator<Collection<Q>> iterator();
}
