/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.phoenix.expression.function.gio;

import io.growing.bitmap.CBitMap;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.phoenix.expression.Expression;
import org.apache.phoenix.expression.function.ScalarFunction;
import org.apache.phoenix.parse.FunctionParseNode.Argument;
import org.apache.phoenix.parse.FunctionParseNode.BuiltInFunction;
import org.apache.phoenix.schema.tuple.Tuple;
import org.apache.phoenix.schema.types.PDataType;
import org.apache.phoenix.schema.types.PVarbinary;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@BuiltInFunction(name = CBitMapOrFunction.NAME,
        args = {@Argument(allowedTypes = {PVarbinary.class}),
                @Argument(allowedTypes = {PVarbinary.class})})
public class CBitMapOrFunction extends ScalarFunction {

    public static final String NAME = "CBITMAP_OR";

    public CBitMapOrFunction() {
    }

    public CBitMapOrFunction(List<Expression> children) throws SQLException {
        super(children);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean evaluate(Tuple tuple, ImmutableBytesWritable ptr) {
        try {
            if (!children.get(0).evaluate(tuple, ptr)) return false;
            CBitMap left = new CBitMap(ptr.copyBytes());
            if (!children.get(1).evaluate(tuple, ptr)) return false;
            CBitMap right = new CBitMap(ptr.copyBytes());
            left.or(right);
            ptr.set(left.getBytes());
            return true;
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    @Override
    public PDataType getDataType() {
        return children.get(0).getDataType();
    }
}