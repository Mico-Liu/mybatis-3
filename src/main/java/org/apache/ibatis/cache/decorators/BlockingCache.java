/**
 * Copyright 2009-2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.cache.decorators;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 实现 Cache 接口，阻塞的 Cache 实现类
 * <p>
 * 这里的阻塞比较特殊，当线程去获取缓存值时，如果不存在，则会阻塞后续的其他线程去获取该缓存。
 * <p>
 * 为什么这么有这样的设计呢？因为当线程 A 在获取不到缓存值时，
 * <p>
 * 一般会去设置对应的缓存值，这样就避免其他也需要该缓存的线程 B、C 等，重复添加缓存。
 * <p>
 * Simple blocking decorator
 * <p>
 * Simple and inefficient version of EhCache's BlockingCache decorator. It sets a lock over a cache key when the element
 * is not found in cache. This way, other threads will wait until this element is filled instead of hitting the
 * database.
 *
 * @author Eduardo Macarron
 */
public class BlockingCache implements Cache {
  /**
   * 阻塞等待超时时间
   */
  private long timeout;
  /**
   * 装饰的 Cache 对象
   */
  private final Cache delegate;
  /**
   * 缓存键与 ReentrantLock 对象的映射
   */
  private final ConcurrentHashMap<Object, ReentrantLock> locks;

  public BlockingCache(Cache delegate) {
    this.delegate = delegate;
    this.locks = new ConcurrentHashMap<>();
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    return delegate.getSize();
  }

  @Override
  public void putObject(Object key, Object value) {
    try {
      // <2.1> 添加缓存
      delegate.putObject(key, value);
    } finally {
      // <2.2> 释放锁
      releaseLock(key);
    }
  }

  @Override
  public Object getObject(Object key) {
    // <1.1> 获得锁。这样其它线程来获取该值时，将被阻塞等待。那岂不是有问题？答案在 <1.3> 处。
    acquireLock(key);
    // <1.2> 获得缓存值
    Object value = delegate.getObject(key);
    // <1.3> 释放锁。
    //获得缓存值成功时，会释放锁，这样被阻塞等待的其他线程就可以去获取缓存了。
    // 但是，如果获得缓存值失败时，就需要在 #putObject(Object key, Object value) 方法中，
    // 添加缓存时，才会释放锁，这样被阻塞等待的其它线程就不会重复添加缓存了。
    if (value != null) {
      releaseLock(key);
    }
    return value;
  }

  /**
   * 和方法名字有所“冲突”，不会移除对应的缓存，只会移除锁
   *
   * @param key The key
   * @return
   */
  @Override
  public Object removeObject(Object key) {
    // despite of its name, this method is called only to release locks
    // 释放锁
    releaseLock(key);
    return null;
  }

  @Override
  public void clear() {
    delegate.clear();
  }

  /**
   * 获得 ReentrantLock 对象。如果不存在，进行添加
   *
   * @param key 缓存键
   * @return ReentrantLock 对象
   */
  private ReentrantLock getLockForKey(Object key) {

    return locks.computeIfAbsent(key, k -> new ReentrantLock());
  }

  /**
   * 获取锁
   *
   * @param key
   */
  private void acquireLock(Object key) {
    // 获得 ReentrantLock 对象。
    Lock lock = getLockForKey(key);
    // 如果设置了等待时间再获取锁
    if (timeout > 0) {
      try {
        //尝试获取锁。等到timeout时间，堵塞住，直到超时
        boolean acquired = lock.tryLock(timeout, TimeUnit.MILLISECONDS);
        //未获取到锁。直接抛出异常
        if (!acquired) {
          throw new CacheException(
            "Couldn't get a lock in " + timeout + " for the key " + key + " at the cache " + delegate.getId());
        }
      } catch (InterruptedException e) {
        throw new CacheException("Got interrupted while trying to acquire lock for key " + key, e);
      }
    }
    //马上获取锁
    else {
      lock.lock();
    }
  }

  /**
   * 释放锁
   *
   * @param key
   */
  private void releaseLock(Object key) {
    // 获得 ReentrantLock 对象
    ReentrantLock lock = locks.get(key);
    // 如果当前线程持有，进行释放
    if (lock.isHeldByCurrentThread()) {
      lock.unlock();
    }
  }

  public long getTimeout() {
    return timeout;
  }

  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }
}
