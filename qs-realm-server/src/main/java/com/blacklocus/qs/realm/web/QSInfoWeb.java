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
package com.blacklocus.qs.realm.web;

import com.blacklocus.qs.realm.FindLogs;
import com.blacklocus.qs.realm.FindTasks;
import com.blacklocus.qs.realm.FindWorkers;
import com.blacklocus.qs.realm.QSInfoService;
import com.blacklocus.qs.worker.model.QSLogModel;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.blacklocus.qs.worker.model.QSWorkerModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class QSInfoWeb {

    private final QSInfoService infoService;

    public QSInfoWeb(QSInfoService infoService) {
        this.infoService = infoService;
    }

    @GET
    @Path("tasks")
    public List<QSTaskModel> findTasks() {
        return infoService.findTasks(new FindTasks());
    }

    @GET
    @Path("logs")
    public List<QSLogModel> findLogs() {
        return infoService.findLogs(new FindLogs());
    }

    @GET
    @Path("workers")
    public List<QSWorkerModel> findWorkers() {
        return infoService.findWorkers(new FindWorkers());
    }
}
