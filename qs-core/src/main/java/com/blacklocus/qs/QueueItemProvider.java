package com.blacklocus.qs;

import java.util.Collection;
import java.util.Iterator;

/**
 * More generalized version of {@link MessageProvider}
 *
 * @author Jason Dunkelberger (dirkraft)
 */
public interface QueueItemProvider<Q> extends Iterator<Collection<Q>>, Iterable<Collection<Q>> {

}
