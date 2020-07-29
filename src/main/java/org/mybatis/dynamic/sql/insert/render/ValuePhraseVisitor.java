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
package org.mybatis.dynamic.sql.insert.render;

import java.util.Optional;
import java.util.function.Function;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.util.ConstantMapping;
import org.mybatis.dynamic.sql.util.InsertMappingVisitor;
import org.mybatis.dynamic.sql.util.NullMapping;
import org.mybatis.dynamic.sql.util.PropertyMapping;
import org.mybatis.dynamic.sql.util.PropertyWhenPresentMapping;
import org.mybatis.dynamic.sql.util.StringConstantMapping;

public class ValuePhraseVisitor extends InsertMappingVisitor<Optional<FieldAndValue>> {
    
    protected RenderingStrategy renderingStrategy;
    
    public ValuePhraseVisitor(RenderingStrategy renderingStrategy) {
        this.renderingStrategy = renderingStrategy;
    }

    @Override
    public <T> Optional<FieldAndValue> visit(NullMapping<T> mapping) {
        return FieldAndValue.withFieldName(mapping.mapColumn(SqlColumn::name))
                .withValuePhrase("null") //$NON-NLS-1$
                .buildOptional();
    }

    @Override
    public <T> Optional<FieldAndValue> visit(ConstantMapping<T> mapping) {
        return FieldAndValue.withFieldName(mapping.mapColumn(SqlColumn::name))
                .withValuePhrase(mapping.constant())
                .buildOptional();
    }

    @Override
    public <T> Optional<FieldAndValue> visit(StringConstantMapping<T> mapping) {
        return FieldAndValue.withFieldName(mapping.mapColumn(SqlColumn::name))
                .withValuePhrase("'" + mapping.constant() + "'") //$NON-NLS-1$ //$NON-NLS-2$
                .buildOptional();
    }
    
    @Override
    public <T> Optional<FieldAndValue> visit(PropertyMapping<T> mapping) {
        return FieldAndValue.withFieldName(mapping.mapColumn(SqlColumn::name))
                .withValuePhrase(mapping.mapColumn(toJdbcPlaceholder(mapping.property())))
                .buildOptional();
    }
    
    @Override
    public <T> Optional<FieldAndValue> visit(PropertyWhenPresentMapping<T> mapping) {
        if (mapping.shouldRender()) {
            return visit((PropertyMapping<T>) mapping);
        } else {
            return Optional.empty();
        }
    }

    private Function<SqlColumn<?>, String> toJdbcPlaceholder(String parameterName) {
        return column -> column.renderingStrategy().orElse(renderingStrategy)
                .getFormattedJdbcPlaceholder(column, "record", parameterName); //$NON-NLS-1$
    }
}
