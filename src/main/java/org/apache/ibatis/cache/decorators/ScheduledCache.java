/**
<<<<<<< HEAD
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
=======
 *    Copyright 2009-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
>>>>>>> mybatis-3-trunk/master
 */
package org.apache.ibatis.cache.decorators;

import java.util.concurrent.TimeUnit;

import org.apache.ibatis.cache.Cache;

/**
 * 实现 Cache 接口，定时清空整个容器的 Cache 实现类
 * <p>
 * 每次缓存操作时，都调用 #clearWhenStale() 方法，根据情况，是否清空全部缓存。
 *
 * @author Clinton Begin
 */
public class ScheduledCache implements Cache {
  /**
   * 被装饰的 Cache 对象
   */
  private final Cache delegate;
  /**
   * 清空间隔，单位：毫秒
   */
  protected long clearInterval;
  /**
   * 最后清空时间，单位：毫秒
   */
  protected long lastClear;

  public ScheduledCache(Cache delegate) {
    this.delegate = delegate;
<<<<<<< HEAD
    // 1 hour
    this.clearInterval = 60 * 60 * 1000;
=======
    this.clearInterval = TimeUnit.HOURS.toMillis(1);
>>>>>>> mybatis-3-trunk/master
    this.lastClear = System.currentTimeMillis();
  }

  public void setClearInterval(long clearInterval) {
    this.clearInterval = clearInterval;
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    // 判断是否要全部清空
    clearWhenStale();
    return delegate.getSize();
  }

  @Override
  public void putObject(Object key, Object object) {
    // 判断是否要全部清空
    clearWhenStale();
    delegate.putObject(key, object);
  }

  @Override
  public Object getObject(Object key) {
    // 判断是否要全部清空
    return clearWhenStale() ? null : delegate.getObject(key);
  }

  @Override
  public Object removeObject(Object key) {
    // 判断是否要全部清空
    clearWhenStale();
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    //清空时， 记录清空时间
    lastClear = System.currentTimeMillis();
    // 全部清空
    delegate.clear();
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

  /**
   * 判断是否要全部清空
   *
   * @return 是否全部清空
   */
  private boolean clearWhenStale() {
    //当前时间减去删词清除时间是否大于一个小时，如果大于，则清空缓存
    if (System.currentTimeMillis() - lastClear > clearInterval) {
      // 清空
      clear();
      return true;
    }
    return false;
  }

}
