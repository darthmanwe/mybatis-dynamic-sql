/**
 *    Copyright 2016-2020 the original author or authors.
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
 */
package org.mybatis.dynamic.sql.util;

import java.util.Objects;

import org.mybatis.dynamic.sql.SqlColumn;

public class PropertyMapping<T> extends AbstractColumnMapping<T> {
    private String property;
    
    protected PropertyMapping(SqlColumn<T> column, String property) {
        super(column);
        this.property = Objects.requireNonNull(property);
    }
    
    public String property() {
        return property;
    }

    @Override
    public <R> R accept(ColumnMappingVisitor<R> visitor) {
        return visitor.visit(this);
    }
    
    public static <T> PropertyMapping<T> of(SqlColumn<T> column, String property) {
        return new PropertyMapping<>(column, property);
    }
}
