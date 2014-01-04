package com.blacklocus.qs.worker.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.blacklocus.qs.worker.QSTaskService;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.google.common.collect.ImmutableMap;

import java.util.Iterator;
import java.util.UUID;

/**
 * Not thread safe.
 */
public class AmazonS3TaskService implements QSTaskService {

    public static final String PARAM_OBJECT = "value";

    public static final long RESTART_DELAY_MS = 5 * 60 * 1000;

    private final String taskHandlerIdentifier;
    private final String bucket;
    private final String prefix;
    private final String delimiter;
    private final AmazonS3 s3;

    private ObjectListing objectListing;
    private String listingBatchId;
    private Iterator<S3ObjectSummary> iterator;

    public AmazonS3TaskService(String taskHandlerIdentifier, String bucket, String prefix, String delimiter, AmazonS3 s3) {
        this.taskHandlerIdentifier = taskHandlerIdentifier;
        this.bucket = bucket;
        this.prefix = prefix;
        this.delimiter = delimiter;
        this.s3 = s3;

        this.objectListing = s3.listObjects(new ListObjectsRequest(bucket, prefix, null, delimiter, 1000));
        this.listingBatchId = UUID.randomUUID().toString().replace("-", "");
        this.iterator = objectListing.getObjectSummaries().iterator();

    }

    @Override
    public QSTaskModel getAvailableTask() {
        if (!iterator.hasNext()) {
            pageForward();
        }
        S3ObjectSummary obj = iterator.next();
        assert obj != null;
        return new QSTaskModel(listingBatchId, "" + UUID.randomUUID().toString().replace("-", ""),
                taskHandlerIdentifier, ImmutableMap.of(PARAM_OBJECT, obj));
    }

    @Override
    public void resetTask(QSTaskModel task) {
        // do nothing
    }

    @Override
    public void commitTask(QSTaskModel task) {
        // Do nothing, external process will decide if the object should be moved/deleted out of the matching listing
        // so that it does not appear again.
    }

    private void pageForward() {
        while (!iterator.hasNext()) {
            if (objectListing.isTruncated()) {
                // next page of the current listing
                objectListing = s3.listNextBatchOfObjects(objectListing);

            } else {
                // reset back to the beginning after quiet period
                try {
                    Thread.sleep(RESTART_DELAY_MS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                objectListing = s3.listObjects(new ListObjectsRequest(bucket, prefix, null, delimiter, 1000));
            }

            iterator = objectListing.getObjectSummaries().iterator();
        }
    }

}