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

package org.apache.flink.table.gateway.service.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;

/** Test the print style. */
class PrinterTest {

    private OutputStream outputStream;
    private Printer printer;

    @BeforeEach
    void beforeEach() {
        outputStream = new ByteArrayOutputStream(1024);
        printer = new Printer(outputStream);
    }

    @AfterEach
    void afterEach() throws Exception {
        outputStream.close();
    }

    @Test
    void testPrintSingleInput() {
        printer.print("This is a sentence.\n");
        assertThat(outputStream.toString()).isEqualTo("Flink SQL> This is a sentence.\n");
    }

    @Test
    void testPrintMultipleLineInputs() {
        printer.print("This is a sentence.\n\n" + "This is another sentence.\n");
        assertThat(outputStream.toString())
                .isEqualTo("Flink SQL> This is a sentence.\n> \n> This is another sentence.\n");
    }
}
