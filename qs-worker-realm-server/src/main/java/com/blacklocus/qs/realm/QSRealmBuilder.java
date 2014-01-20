package com.blacklocus.qs.realm;

import com.blacklocus.qs.realm.web.QSWorkerRealmService;
import com.google.common.base.Preconditions;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSRealmBuilder {

    private QSInfoService infoService;

    public QSRealmBuilder infoService(QSInfoService infoService) {
        this.infoService = infoService;
        return this;
    }

    public void validate() {
        Preconditions.checkNotNull(infoService, "A QSInfoService implementation is required.");
    }

    public void run() throws Exception {
        validate();
        new QSWorkerRealmService(infoService).run(new String[]{"server"});
    }
}
