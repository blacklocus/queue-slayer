package com.blacklocus.qs.worker.aws;

import com.blacklocus.qs.worker.QSWorkerIdService;
import com.blacklocus.qs.worker.simple.HostNameQSWorkerIdService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class AmazonEC2WorkerIdService implements QSWorkerIdService {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonEC2WorkerIdService.class);


    public static final String META_PFX = "http://169.254.169.254/latest/meta-data/";
    public static final String META_INSTANCE_ID = "instance-id";

    public static final String PROP_SKIP_EC2_META = "com.blacklocus.qs.aws.disableEC2Meta";


    private final QSWorkerIdService fallback = new HostNameQSWorkerIdService();

    @Override
    public String getWorkerId() {
        return getEC2Meta(META_INSTANCE_ID, false);
    }

    public String getEC2Meta(String metaKey, boolean suppressError) {
        String id = null;

        if (!Boolean.valueOf(System.getProperty(PROP_SKIP_EC2_META, "false"))) {
            String inputLine;
            BufferedReader in = null;
            try {
                URL ec2metadata = new URL(META_PFX + metaKey);
                URLConnection connection = ec2metadata.openConnection();
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    id = inputLine;
                }
            } catch (Exception e) {
                if (!suppressError) {
                    LOG.warn("Problem retrieving EC2 metadata: " + metaKey + "\n" +
                            "\tIs this an EC2 machine? If not, set " + PROP_SKIP_EC2_META + " to true to skip this lookup.", e);
                }
            } finally {
                IOUtils.closeQuietly(in);
            }
        }

        if (StringUtils.isBlank(id)) {
            id = fallback.getWorkerId();
        }

        return id;
    }

}
