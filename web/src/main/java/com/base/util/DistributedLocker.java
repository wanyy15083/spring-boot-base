package com.base.util;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * It implements the distribute locker to lock the global resource across server. The acquirer should complete
 * the task in the timeout and release the lock explicitly. If it fails to complete the task, the lock will be
 * released after the time is out.
 * <p>
 * The locker solution refers "http://redisTemplate.io/topics/distlock". See the link for the details.
 */
@Component
public class DistributedLocker {

    private final static Logger logger = LoggerFactory.getLogger(DistributedLocker.class);

    private final static String DIST_LOCK_KEY_PREFIX = "dist_lock:";

    /**
     * 6h
     */
    private static final int MAX_LOCKER_TIME = 3600 * 6;

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;
    @Autowired(required = false)
    private RedisScript<Long> redisScript;

    /**
     * acquire the distribute locker
     *
     * @param name    the locker name, such as: appname:locker
     * @param timeout the timeout (seconds, 1~3600*6). After that, the locker will be released
     * @return the locker id if the locker is acquired or null
     */
    public String lock(final String name, final int timeout) {
        if (timeout < 1 || timeout > MAX_LOCKER_TIME) {
            logger.warn("invalid timeout,it must be between 1s and 6h.");
            return null;
        }
        final String key = getLockerKey(name);
        if (CommonUtils.isNotBlank(redisTemplate.opsForValue().get(key))) {
            logger.debug("failed to acquire the locker '{}' because it's locked by other.", name);
            // process the expire exception case
            Long expireSeconds = redisTemplate.getExpire(key);
            if (expireSeconds != null && expireSeconds == -1) {
                logger.warn("reset the timeout of bad distribute locker '{}' ", key);
                redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
            }
            return null;
        }
        try {
            final String lockId = CommonUtils.generateUUID();
            Boolean succ = redisTemplate.opsForValue().setIfAbsent(key, lockId, timeout, TimeUnit.SECONDS);
            if (succ!= null && succ) {
//            if (this.setIfAbsent(key, lockId, timeout, TimeUnit.SECONDS)) {
                return lockId;
            } else {
                logger.debug("failed to create the locker key '{}' because it exists.", key);
            }
        } catch (Exception e) {
            logger.warn("catch exception in locking: {}", e);
            redisTemplate.delete(key);
        }
        return null;
    }

    public Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        final byte[] rawKey = this.serialize(key);
        final byte[] rawValue = this.serialize(value);
        //
        final Expiration expiration = Expiration.from(timeout, unit);
        return redisTemplate.execute(connection -> connection.set(rawKey, rawValue, expiration, RedisStringCommands.SetOption.ifAbsent()), true);
    }

    private byte[] serialize(@Nullable String string) {
        try {
            return (string == null ? null : string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * unlock the global lock
     *
     * @param name     the locker name
     * @param lockerId the locker id returned by lock()
     */
    public void unlock(final String name, final String lockerId) {
        final String key = getLockerKey(name);
        final String value = redisTemplate.opsForValue().get(key);
        if (CommonUtils.isBlank(value)) {
            logger.info("the locker '{}' doesn't exists", name);
            return;
        }
        if (!value.equals(lockerId)) {
            logger.warn("invalid locker id!");
            return;
        }
        redisTemplate.delete(key);
        logger.debug("the locker '{}' is unlocked", name);
    }

    /**
     * unlock the global lock
     *
     * @param name     the locker name
     * @param lockerId the locker id returned by lock()
     */
    public void unlockLua(final String name, final String lockerId) {
        final String key = getLockerKey(name);
        redisTemplate.execute(redisScript, ImmutableList.of(key), lockerId);
        logger.debug("the locker '{}' is unlocked", name);
    }

    public boolean isLocked(String name) {
        return redisTemplate.opsForValue().get(getLockerKey(name)) != null;
    }

    /**
     * update the locker expire time to (now + timeout)
     *
     * @param lockerName     the locker's name
     * @param timeoutSeconds the timeout seconds
     * @return true if success or null if the locker doesn't exist and failed to update
     */
    public boolean update(String lockerName, int timeoutSeconds) {
        final String key = getLockerKey(lockerName);
        if (CommonUtils.isNotBlank(redisTemplate.opsForValue().get(key))) {
            redisTemplate.expire(key, timeoutSeconds, TimeUnit.SECONDS);
            return true;
        } else {
            logger.debug("the locker '{}' doesn't exist", lockerName);
            return false;
        }
    }

    private static String getLockerKey(String name) {
        if (name.startsWith(":")) {
            name = name.substring(1);
        }
        return DIST_LOCK_KEY_PREFIX + name;
    }

    /**
     * getset dis lock version not set expire time
     * @param name
     * @param timeout
     * @return
     */
    public String lockV1(final String name, final int timeout) {
        if (timeout < 1 || timeout > MAX_LOCKER_TIME) {
            logger.warn("invalid timeout,it must be between 1s and 6h.");
            return null;
        }
        final String key = getLockerKey(name);
        try {
            String newExpireTime = (System.currentTimeMillis() + timeout * 1000L) + "";
            Boolean succ = redisTemplate.opsForValue().setIfAbsent(key, newExpireTime);
            if (succ!= null && succ) {
                return newExpireTime;
            } else {
                String oldExpireTime = redisTemplate.opsForValue().get(key);
                if (CommonUtils.isNotBlank(oldExpireTime) && Long.valueOf(oldExpireTime) < System.currentTimeMillis()) {
                    String currentExpireTime = redisTemplate.opsForValue().getAndSet(key, newExpireTime);
                    if (CommonUtils.isNotBlank(currentExpireTime) && currentExpireTime.equals(oldExpireTime)) {
                        return newExpireTime;
                    } else {
                        logger.debug("failed to acquire the locker '{}' because it's locked by other.", name);
                    }
                }

            }
        } catch (Exception e) {
            logger.warn("catch exception in locking: {}", e);
            redisTemplate.delete(key);
        }
        return null;
    }

    public void unlockV1(final String name, final String value) {
        final String key = getLockerKey(name);
        final String expireTime = redisTemplate.opsForValue().get(key);
        if (CommonUtils.isBlank(expireTime)) {
            logger.info("the locker '{}' doesn't exists", name);
            return;
        }
        if (Long.valueOf(expireTime) > Long.valueOf(value)) {
            logger.warn("locker expire!");
            return;
        }
        redisTemplate.delete(key);
        logger.debug("the locker '{}' is unlocked", name);
    }
}
