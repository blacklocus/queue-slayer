package com.blacklocus.qs.worker.es;

import com.blacklocus.jres.Jres;
import com.blacklocus.jres.request.index.JresCreateIndex;
import com.blacklocus.jres.request.index.JresIndexExists;
import com.blacklocus.jres.request.mapping.JresPutMapping;
import com.blacklocus.jres.request.mapping.JresTypeExists;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

import static com.blacklocus.qs.worker.es.ElasticSearchQSLogService.INDEX_TYPE_TASK;
import static com.blacklocus.qs.worker.es.ElasticSearchQSLogService.INDEX_TYPE_TASK_LOG;
import static com.blacklocus.qs.worker.es.ElasticSearchQSLogService.INDEX_TYPE_WORKER;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class ElasticSearchInitializer {

    private final String index;
    private final Jres jres;

    public ElasticSearchInitializer(String index, Jres jres) {
        this.index = index;
        this.jres = jres;
    }

    public void verifyElasticSearchMappings() {
        if (!jres.bool(new JresIndexExists(index)).verity()) {
            jres.quest(new JresCreateIndex(index));
        }
        if (!jres.bool(new JresTypeExists(index, INDEX_TYPE_TASK)).verity()) {
            jres.quest(new JresPutMapping(index, INDEX_TYPE_TASK, getElasticSearchJson("/task.mapping.json")));
        }
        if (!jres.bool(new JresTypeExists(index, INDEX_TYPE_TASK_LOG)).verity()) {
            jres.quest(new JresPutMapping(index, INDEX_TYPE_TASK_LOG, getElasticSearchJson("/taskLog.mapping.json")));
        }
        if (!jres.bool(new JresTypeExists(index, INDEX_TYPE_WORKER)).verity()) {
            jres.quest(new JresPutMapping(index, INDEX_TYPE_WORKER, getElasticSearchJson("/worker.mapping.json")));
        }
    }

    private static String getElasticSearchJson(String file) {
        try {
            return IOUtils.toString(ElasticSearchQSLogService.class.getResource(file).openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}