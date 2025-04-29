package com.ruoyi.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 匿名访问不鉴权注解
 * 
 * @author ruoyi
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Anonymous
{

  
    /**
     * Set expiration time.
     */
    public CacheBuilder<K, V> expireNanos(long duration, TimeUnit unit) {
        checkExpireNanos(duration, unit);
        this.expireNanos = unit.toNanos(duration);
        return this;
    }
    
    private void checkExpireNanos(long duration, TimeUnit unit) {
        if (duration < 0) {
            throw new IllegalArgumentException("duration cannot be negative");
        }
        if (Objects.isNull(unit)) {
            throw new IllegalArgumentException("unit cannot be null");
        }
    }
    
    /**
     * Set the maximum capacity of the cache pair.
     * @param maximumSize maximum capacity
     */
    public CacheBuilder<K, V> maximumSize(int maximumSize) {
        if (maximumSize < 0) {
            throw new IllegalArgumentException("size cannot be negative");
        }
        this.maximumSize = maximumSize;
        return this;
    }
    
    /**
     * Set whether the cache method is synchronized.
     * @param sync if sync value is true, each method of the constructed cache is synchronized.
     */
    public CacheBuilder<K, V> sync(boolean sync) {
        this.sync = sync;
        return this;
    }
    
    /**
     * Does the constructed cache support lru.
     * @param lru If the cache built for true is an lru cache.
     */
    public CacheBuilder<K, V> lru(boolean lru) {
        this.lru = lru;
        return this;
    }
    
    /**
     * Set the initial capacity of the cache pair.
     * @param initializeCapacity initialize capacity
     */
    public CacheBuilder<K, V> initializeCapacity(int initializeCapacity) {
        if (initializeCapacity < 0) {
            throw new IllegalArgumentException("initializeCapacity cannot be negative");
        }
        this.initializeCapacity = initializeCapacity;
        return this;
    }
}
