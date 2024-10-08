/*
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

package org.apache.flink.api.java.io;

import org.apache.flink.api.common.io.BinaryInputFormat;
import org.apache.flink.api.common.io.BinaryOutputFormat;
import org.apache.flink.api.common.io.BlockInfo;
import org.apache.flink.api.common.io.SequentialFormatTestBase;
import org.apache.flink.api.common.serialization.SerializerConfigImpl;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.memory.DataOutputView;

import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/** Tests for type serialization format. */
class TypeSerializerFormatTest extends SequentialFormatTestBase<Tuple2<Integer, String>> {

    TypeInformation<Tuple2<Integer, String>> resultType;

    private final TypeSerializer<Tuple2<Integer, String>> serializer;

    private BlockInfo block;

    TypeSerializerFormatTest() {

        resultType = TypeExtractor.getForObject(getRecord(0));

        serializer = resultType.createSerializer(new SerializerConfigImpl());
    }

    @BeforeEach
    void setup() {
        block = createInputFormat().createBlockInfo();
    }

    @Override
    protected BinaryInputFormat<Tuple2<Integer, String>> createInputFormat() {
        Configuration configuration = new Configuration();

        final TypeSerializerInputFormat<Tuple2<Integer, String>> inputFormat =
                new TypeSerializerInputFormat<>(resultType);
        inputFormat.setFilePath(this.tempFile.toURI().toString());

        inputFormat.configure(configuration);
        return inputFormat;
    }

    @Override
    protected BinaryOutputFormat<Tuple2<Integer, String>> createOutputFormat(
            String path, Configuration configuration) throws IOException {
        TypeSerializerOutputFormat<Tuple2<Integer, String>> outputFormat =
                new TypeSerializerOutputFormat<>();

        outputFormat.setSerializer(serializer);
        outputFormat.setOutputFilePath(new Path(path));
        outputFormat.setWriteMode(FileSystem.WriteMode.OVERWRITE);

        configuration = configuration == null ? new Configuration() : configuration;

        outputFormat.configure(configuration);
        outputFormat.open(0, 1);

        return outputFormat;
    }

    @Override
    protected int getInfoSize() {
        return block.getInfoSize();
    }

    @Override
    protected Tuple2<Integer, String> getRecord(int index) {
        return new Tuple2<>(index, String.valueOf(index));
    }

    @Override
    protected Tuple2<Integer, String> createInstance() {
        return new Tuple2<>();
    }

    @Override
    protected void writeRecord(Tuple2<Integer, String> record, DataOutputView outputView)
            throws IOException {
        serializer.serialize(record, outputView);
    }

    @Override
    protected void checkEquals(Tuple2<Integer, String> expected, Tuple2<Integer, String> actual) {
        assertThat(actual.f0).isEqualTo(expected.f0);
        assertThat(actual.f1).isEqualTo(expected.f1);
    }
}
