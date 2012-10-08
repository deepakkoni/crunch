/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.crunch.lib;

import java.util.List;

import org.apache.crunch.DoFn;
import org.apache.crunch.Emitter;
import org.apache.crunch.PCollection;
import org.apache.crunch.PGroupedTable;
import org.apache.crunch.PTable;
import org.apache.crunch.Pair;
import org.apache.crunch.types.PGroupedTableType;
import org.apache.crunch.types.PTableType;
import org.apache.crunch.types.PType;

import com.google.common.collect.Lists;

/**
 * Methods for performing common operations on PTables.
 * 
 */
public class PTables {

  public static <K, V> PCollection<K> keys(PTable<K, V> ptable) {
    return ptable.parallelDo("PTables.keys", new DoFn<Pair<K, V>, K>() {
      @Override
      public void process(Pair<K, V> input, Emitter<K> emitter) {
        emitter.emit(input.first());
      }
    }, ptable.getKeyType());
  }

  public static <K, V> PCollection<V> values(PTable<K, V> ptable) {
    return ptable.parallelDo("PTables.values", new DoFn<Pair<K, V>, V>() {
      @Override
      public void process(Pair<K, V> input, Emitter<V> emitter) {
        emitter.emit(input.second());
      }
    }, ptable.getValueType());
  }

  /**
   * Create a detached value for a table {@link Pair}.
   * 
   * @param tableType The table type
   * @param value The value from which a detached value is to be created
   * @return The detached value
   * @see PType#getDetachedValue(Object)
   */
  public static <K, V> Pair<K, V> getDetachedValue(PTableType<K, V> tableType, Pair<K, V> value) {
    return Pair.of(tableType.getKeyType().getDetachedValue(value.first()), tableType.getValueType()
        .getDetachedValue(value.second()));
  }

  /**
   * Created a detached value for a {@link PGroupedTable} value.
   * 
   * 
   * @param groupedTableType The grouped table type
   * @param value The value from which a detached value is to be created
   * @return The detached value
   * @see PType#getDetachedValue(Object)
   */
  public static <K, V> Pair<K, Iterable<V>> getGroupedDetachedValue(
      PGroupedTableType<K, V> groupedTableType, Pair<K, Iterable<V>> value) {

    PTableType<K, V> tableType = groupedTableType.getTableType();
    List<V> detachedIterable = Lists.newArrayList();
    PType<V> valueType = tableType.getValueType();
    for (V v : value.second()) {
      detachedIterable.add(valueType.getDetachedValue(v));
    }
    return Pair.of(tableType.getKeyType().getDetachedValue(value.first()),
        (Iterable<V>) detachedIterable);
  }
}
