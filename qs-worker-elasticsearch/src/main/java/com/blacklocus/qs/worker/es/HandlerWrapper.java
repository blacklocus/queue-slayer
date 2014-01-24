package com.blacklocus.qs.worker.es;

import java.util.HashMap;
import java.util.Map;

/**
* @author Jason Dunkelberger (dirkraft)
*/
class HandlerWrapper extends HashMap<String, Map<?, ?>> {
    public HandlerWrapper(String handlerName, Map<?, ?> params) {
        put(handlerName, params);
    }
}
