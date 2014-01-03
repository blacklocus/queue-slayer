package com.blacklocus.qs.worker;

import org.apache.commons.configuration.Configuration;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public interface QSWorker {

    Object undertake(Configuration params, QSTaskLogger taskLogger);

}