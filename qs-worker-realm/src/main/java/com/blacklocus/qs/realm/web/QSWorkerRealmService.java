package com.blacklocus.qs.realm.web;

import com.blacklocus.qs.realm.QSInfoService;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSWorkerRealmService extends Service<Configuration> {

    private final QSInfoService infoService;

    public QSWorkerRealmService(QSInfoService infoService) {
        this.infoService = infoService;
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle());
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        environment.addResource(new QSInfoWeb(infoService));
    }
}
