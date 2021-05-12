
package com.xxl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Callable;

public class TryCatchUtil {

    private static final Logger logger = LoggerFactory.getLogger(TryCatchUtil.class);

    public static boolean run(String message, OpenRunnable runnable) {
        try {
            runnable.run();
            return true;
        } catch (Exception e) {
            logger.error("exception happened in " + message, e);
            return false;
        }
    }

    public static <T> Optional<T> run(String message, Callable<T> callable) {
        try {
            return Optional.ofNullable(callable.call());
        } catch (Exception e) {
            logger.error("exception happened in " + message, e);
            return Optional.empty();
        }
    }
}
