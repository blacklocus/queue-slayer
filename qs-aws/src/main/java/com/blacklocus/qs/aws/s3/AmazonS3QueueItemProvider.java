package com.blacklocus.qs.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.blacklocus.qs.QueueItemProvider;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class AmazonS3QueueItemProvider implements QueueItemProvider<S3ObjectSummary> {

    private final String bucket;
    private final String prefix;
    private final String delimiter;
    private final AmazonS3 s3;

    private ObjectListing page;

    public AmazonS3QueueItemProvider(String bucket, AmazonS3 s3) {
        this(bucket, null, null, s3);
    }

    public AmazonS3QueueItemProvider(String bucket, String prefix, String delimiter, AmazonS3 s3) {
        this.bucket = bucket;
        this.prefix = prefix;
        this.delimiter = delimiter;
        this.s3 = s3;
    }

    @Override
    public Iterator<Collection<S3ObjectSummary>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return page == null || page.isTruncated();
    }

    @Override
    public Collection<S3ObjectSummary> next() {
        if (page == null) {
            page = s3.listObjects(new ListObjectsRequest(bucket, prefix, null, delimiter, 1000));
        } else {
            assert page.isTruncated();
            page = s3.listNextBatchOfObjects(page);
        }
        return page.getObjectSummaries();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
