package com.xxl.util.retry;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import com.xxl.util.retry.exception.RetryException;
import com.xxl.util.retry.exception.RetryExecutionException;
import com.xxl.util.retry.exception.RetryInterruptedException;
import com.xxl.util.retry.exception.RetryMaxCallableException;
import com.xxl.util.retry.exception.RetryPredicateFailedException;
import com.xxl.util.retry.exception.RetryPredicateFalseException;
import interfaces.OpenRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RetryUtil {

    private static final Logger logger = LoggerFactory.getLogger(RetryUtil.class);

    public static final long FOREVER_RETRIES = Long.MAX_VALUE;
    public static final long DEFAULT_RETRIES = 3L;
    public static final long DEFAULT_RETRIES_EXCEPTION = 3L;
    public static final long DEFAULT_DELAY = 5L;
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;

    public static <T> T retryForever(String message, Callable<T> callable) throws RetryException {
        return retry(message, callable, t -> true, FOREVER_RETRIES, DEFAULT_DELAY, DEFAULT_UNIT);
    }

    public static <T> T retryForever(String message, Callable<T> callable, long delay, TimeUnit unit)
            throws RetryException {
        return retry(message, callable, t -> true, FOREVER_RETRIES, delay, unit);
    }

    public static void retryForever(String message, OpenRunnable runnable) throws RetryException {
        retry(message, runnable, () -> true, FOREVER_RETRIES, DEFAULT_DELAY, DEFAULT_UNIT);
    }

    public static void retryForever(String message, OpenRunnable runnable, long delay, TimeUnit unit)
            throws RetryException {
        retry(message, runnable, () -> true, FOREVER_RETRIES, delay, unit);
    }

    public static <T> T retryUntil(String message, Callable<T> callable, Predicate<T> until)
            throws RetryException {
        return retry(message, callable, until, FOREVER_RETRIES, DEFAULT_DELAY, DEFAULT_UNIT);
    }

    public static <T> T retryUntil(String message, Callable<T> callable, Predicate<T> until, long maxRetries)
            throws RetryException {
        return retry(message, callable, until, maxRetries, DEFAULT_DELAY, DEFAULT_UNIT);
    }

    public static <T> T retryUntil(String message, Callable<T> callable, Predicate<T> until, long delay, TimeUnit unit)
            throws RetryException {
        return retry(message, callable, until, FOREVER_RETRIES, delay, unit);
    }

    public static <T> T retryUntil(String message, Callable<T> callable, Predicate<T> until,
                                   long maxRetiesWhenCallableException,long delay, TimeUnit unit)
            throws RetryException {
        return retry(message, callable, until, FOREVER_RETRIES, maxRetiesWhenCallableException, delay, unit);
    }

    public static void retryUntil(String message, OpenRunnable runnable, RetryPredicate until)
            throws RetryException {
        retry(message, runnable, until, FOREVER_RETRIES, DEFAULT_DELAY, DEFAULT_UNIT);
    }

    public static void retryUntil(String message, OpenRunnable runnable, RetryPredicate until, long maxRetries)
            throws RetryException {
        retry(message, runnable, until, maxRetries, DEFAULT_DELAY, DEFAULT_UNIT);
    }

    public static void retryUntil(String message, OpenRunnable runnable, RetryPredicate until, long delay,
                                  TimeUnit unit) throws RetryException {
        retry(message, runnable, until, FOREVER_RETRIES, delay, unit);
    }

    public static <T> T retry(String message, Callable<T> callable) throws RetryException {
        return retry(message, callable, t -> true, DEFAULT_RETRIES, DEFAULT_DELAY, DEFAULT_UNIT);
    }

    public static <T> T retry(String message, Callable<T> callable, long maxRetries) throws RetryException {
        return retry(message, callable, t -> true, maxRetries, DEFAULT_DELAY, DEFAULT_UNIT);
    }

    public static <T> T retry(String message, Callable<T> callable, long maxRetries, long delay, TimeUnit unit)
            throws RetryException {
        return retry(message, callable, t -> true, maxRetries, delay, unit);
    }

    public static void retry(String message, OpenRunnable runnable) throws RetryException {
        retry(message, runnable, () -> true, DEFAULT_RETRIES, DEFAULT_DELAY, DEFAULT_UNIT);
    }

    public static void retry(String message, OpenRunnable runnable, long maxRetries) throws RetryException {
        retry(message, runnable, () -> true, maxRetries, DEFAULT_DELAY, DEFAULT_UNIT);
    }

    public static void retry(String message, OpenRunnable runnable, long maxRetries, long delay, TimeUnit unit)
            throws RetryException {
        retry(message, runnable, () -> true, maxRetries, delay, unit);
    }

    /**
     * Retry if any exception is thrown in 'callable', or the retry predicate returns false in 'until'.
     * The result will be returned if success.
     *
     * @param message message to print in retry
     * @param callable the real operation
     * @param until the retry predicate, returns false if next retry is required
     * @param maxRetries max times of retry
     * @param maxRetriesWhenCallableException  max times of retry for callable exception.
     * @param delay delay between retries
     * @param unit delay unit
     * @param <T> the return type
     * @return the callable result
     * @throws RetryException if retry failed finally
     */
    public static <T> T retry(String message, Callable<T> callable, Predicate<T> until,
                              long maxRetries, long maxRetriesWhenCallableException,
                              long delay, TimeUnit unit) throws RetryException {
        long exceptionCounter = 0;
        for (long i = 1; i <= maxRetries; i++) {
            T result;
            try {
                result = callable.call();
            } catch (InterruptedException e) {
                logger.warn(message + " retry " + i + " interrupted, " + e.toString());
                Thread.currentThread().interrupt();
                throw new RetryInterruptedException(message + " retry " + i + " interrupted", e);
            } catch (RetryInterruptedException e) {
                throw e;
            } catch (Throwable e) {

                exceptionCounter++;
                if (exceptionCounter == maxRetriesWhenCallableException) {
                    throw new RetryMaxCallableException(message + " retry " + i
                            + " failed finally case by exception. exception count: " + exceptionCounter, e);
                }

                logger.error(message + " retry " + i + " failed, " + e.toString());
                if (i == maxRetries) {
                    throw new RetryExecutionException(message + " retry " + i + " failed finally", e);
                }
                sleep(delay, unit, message + " retry " + i + " interrupted");
                continue;
            }
            try {
                if (until.test(result)) {
                    return result;
                }
                logger.warn(message + " retry " + i + " the retry predicate returns false");
                if (i == maxRetries) {
                    throw new RetryPredicateFalseException(result, message + " retry " + i + " failed finally");
                }
            } catch (RetryPredicateFalseException e) {
                throw e;
            } catch (Throwable e) {
                exceptionCounter++;
                logger.error(message + " retry " + i + " failed, " + e.toString());
                if (i == maxRetries) {
                    throw new RetryPredicateFailedException(result, message + " retry " + i + " failed finally", e);
                }
            }
            sleep(delay, unit, message + " retry " + i + " interrupted");
        }
        // impossible to be here
        return null;
    }

    /**
     * Retry if any exception is thrown in 'runnable', or the retry predicate returns false in 'until'.
     *
     * @param message message to print in retry
     * @param runnable the real operation
     * @param until the retry predicate, returns false if next retry is required
     * @param maxRetries max times of retry
     * @param delay delay between retries
     * @param unit delay unit
     * @throws RetryException if retry failed finally
     */
    public static void retry(String message, OpenRunnable runnable, RetryPredicate until,
                             long maxRetries,long delay, TimeUnit unit) throws RetryException {
        // forward to the above method
        retry(message, () -> {
            runnable.run();
            return true;
        }, b -> until.test(), maxRetries, FOREVER_RETRIES, delay, unit);
    }

    public static <T> T retry(String message, Callable<T> callable, Predicate<T> until,
                             long maxRetries,long delay, TimeUnit unit) throws RetryException {
        // forward to the above method
        return retry(message, callable, until, maxRetries, FOREVER_RETRIES, delay, unit);
    }

    private static void sleep(long delay, TimeUnit unit, String message) throws RetryInterruptedException {
        try {
            unit.sleep(delay);
        } catch (InterruptedException e) {
            logger.warn(message + ", " + e.toString());
            Thread.currentThread().interrupt();
            throw new RetryInterruptedException(message, e);
        }
    }
}
