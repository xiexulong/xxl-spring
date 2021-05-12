package com.xxl.concurrent.distributed;

public class DistributedManagerFactory {
    public static final String SOURCE_ZK = "ZK";
    public static final String SOURCE_REDIS = "REDIS";

    public static final String DEFAULT_PROP_PREFIX = "global";

    public static final String PROP_SOURCE = "distributed.source";
    public static final String PROP_ZK_ADDRESS = "zk.address";
    public static final String PROP_REDIS_ADDRESS = "redis.address";

    public static DistributedManager create(org.springframework.core.env.Environment env) {
        String source = getProperty(env, DEFAULT_PROP_PREFIX, PROP_SOURCE);
        return create(source, env, DEFAULT_PROP_PREFIX);
    }

    public static DistributedManager create(String source, org.springframework.core.env.Environment env) {
        return create(source, env, DEFAULT_PROP_PREFIX);
    }
    public static DistributedManager create(String source,
                                            org.springframework.core.env.Environment env,
                                            String propertyPrefix) {
        switch (source) {
            case SOURCE_ZK:
                return new ZookeeperManager(
                        getProperty(env, propertyPrefix, PROP_ZK_ADDRESS)
                );
            case SOURCE_REDIS:
                return new RedisManager(
                        getProperty(env, propertyPrefix, PROP_REDIS_ADDRESS)
                );
            default:
                throw new IllegalArgumentException("unsupported storage " + source);
        }
    }

    private static String getProperty(org.springframework.core.env.Environment env, String prefix, String prop) {
        String k = prefix + "." + prop;
        String v = env.getProperty(k);
        if (v == null) {
            throw new IllegalArgumentException("property " + k + " required");
        }
        return v;
    }
}
