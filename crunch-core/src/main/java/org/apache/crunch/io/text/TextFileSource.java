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
package org.apache.crunch.io.text;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.crunch.ReadableData;
import org.apache.crunch.impl.mr.run.RuntimeParameters;
import org.apache.crunch.io.ReadableSource;
import org.apache.crunch.io.impl.FileSourceImpl;
import org.apache.crunch.types.PType;
import org.apache.crunch.types.avro.AvroTypeFamily;
import org.apache.crunch.types.avro.AvroUtf8InputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

public class TextFileSource<T> extends FileSourceImpl<T> implements ReadableSource<T> {

  private static <S> Class<? extends FileInputFormat<?, ?>> getInputFormat(PType<S> ptype) {
    if (ptype.getFamily().equals(AvroTypeFamily.getInstance())) {
      return AvroUtf8InputFormat.class;
    } else {
      return TextInputFormat.class;
    }
  }

  public TextFileSource(Path path, PType<T> ptype) {
    this(Collections.singletonList(path), ptype);
  }

  public TextFileSource(List<Path> paths, PType<T> ptype) {
    super(paths, ptype, getInputFormat(ptype));
    inputBundle.set(RuntimeParameters.DISABLE_COMBINE_FILE, Boolean.FALSE.toString());
  }

  @Override
  public String toString() {
    return "Text(" + pathsAsString() + ")";
  }

  @Override
  public Iterable<T> read(Configuration conf) throws IOException {
    return read(conf, new TextFileReaderFactory<T>(LineParser.forType(ptype)));
  }

  @Override
  public ReadableData<T> asReadable() {
    return new TextReadableData<T>(paths, ptype);
  }
}
