
package com.xxl.mq.demo1.common;


import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AlgoQueues {

    private static final Map<AlgoType, String> TASK_QUEUE_NAMES = new EnumMap<>(AlgoType.class);
    private static final Map<AlgoType, String> REPLY_QUEUE_NAMES = new EnumMap<>(AlgoType.class);

    static {
        for (AlgoType algo : AlgoType.values()) {
            TASK_QUEUE_NAMES.put(algo, "rdb-server-queue-" + algo.getName());
            REPLY_QUEUE_NAMES.put(algo, "rdb-server-queue-" + algo.getName() + "-reply");
        }
    }

    public static List<String> getReplyQueueNames() {
        List<String> names = new ArrayList<>();
        names.addAll(REPLY_QUEUE_NAMES.values());
        return names;
    }

    public static List<String> getAllQueueNames() {
        List<String> names = new ArrayList<>();
        names.addAll(TASK_QUEUE_NAMES.values());
        names.addAll(REPLY_QUEUE_NAMES.values());
        return names;
    }

    public static String getTaskQueueName(AlgoType algo) {
        return TASK_QUEUE_NAMES.get(algo);
    }

    public static String getReplyQueueName(AlgoType algo) {
        return REPLY_QUEUE_NAMES.get(algo);
    }
}
