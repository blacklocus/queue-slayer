/**
 * Copyright 2013 BlackLocus
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blacklocus.qs;

import java.util.Collection;
import java.util.Iterator;

/**
 * The superest class inside of the com.blacklocus.qs namespace which can provide items to a {@link QueueReader}.
 * Note that the QueueReader actually accepts any <code>Iterable&lt;Collection&lt;Q&gt;&gt; provider</code>, so this is
 * useful more as a marker interface.
 *
 * @author Jason Dunkelberger (dirkraft)
 */
public interface QueueItemProvider<Q> extends Iterator<Collection<Q>>, Iterable<Collection<Q>> {

    /**
     * @return true if the reading {@link QueueReader} should continue processing messages from this provider.
     */
    @Override
    boolean hasNext();

    /**
     * @return the next batch of messages, or an empty collection if none were available. Should never return
     * <code>null</code>. The reading {@link QueueReader} may sleep after receiving an empty batch depending on its
     * configuration.
     */
    @Override
    Collection<Q> next();

    /**
     * A means to delete the message because it is done or should not be processed again. If the provider does not
     * care or has no such notion, should not throw an exception. An exception may be thrown if the provider
     * does recognize remove semantics, but somehow failed in executing that intention.
     */
    @Override
    void remove();

    /**
     * Generally returns <code>this</code>. QueueItemProviders are not generally reusable/resettable since the
     * underlying message source will often not be reversible. Mashing the two interfaces together simplifies
     * implementations a bit, and also supports any typical Iterable usage.
     */
    @Override
    Iterator<Collection<Q>> iterator();
}
