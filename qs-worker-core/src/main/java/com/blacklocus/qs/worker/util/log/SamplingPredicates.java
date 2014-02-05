package com.blacklocus.qs.worker.util.log;

import com.blacklocus.qs.worker.model.QSTaskModel;
import com.google.common.base.Predicate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SamplingPredicates {

    /**
     * @return a thread-safe predicate that returns true on every 'nth' comparison where n is the given `multiple` size.
     * e.g. given a multiple of 5, the 0th, 5th, 10th, 15th, ... calls to {@link Predicate#apply(Object)} will return true
     */
    public static Predicate<QSTaskModel> everyNthEntry(final int multiple) {
        return new Predicate<QSTaskModel>() {

            final AtomicLong counter = new AtomicLong();

            @Override
            public boolean apply(QSTaskModel input) {
                long count = counter.getAndIncrement();
                return count % multiple == 0;
            }
        };
    }

    /**
     * @return a thread-safe predicate that returns true at most once per duration. e.g. given 5 seconds,
     * :00 true, :05 true, :10 true, :11 false, :15 true, :22 true, :25 false, :27 true. The soonest possible true
     * must be at least 5 seconds after the previous true.
     */
    public static Predicate<QSTaskModel> noSoonerThan(final int duration, final TimeUnit unit) {
        return new Predicate<QSTaskModel>() {

            final long nanoStep = unit.toNanos(duration);
            final AtomicLong nextLegal = new AtomicLong(System.nanoTime());

            @Override
            public boolean apply(QSTaskModel input) {
                long now = System.nanoTime();
                long nextLegal = this.nextLegal.get();
                // If the first expression is false, then it's been 'too soon since last true'.
                return now >= nextLegal &&
                        // We have a chance at the current time slot. It's ours only if this thread owns the atomic
                        // update (true). If a competing thread takes it from us then we lose and did not get the
                        // available time slot. So this attempt counts as 'too soon since last true' (false).
                        this.nextLegal.compareAndSet(nextLegal, now + nanoStep);
            }
        };
    }
}
