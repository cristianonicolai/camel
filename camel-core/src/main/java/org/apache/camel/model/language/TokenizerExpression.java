/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.model.language;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.camel.CamelContext;
import org.apache.camel.Expression;
import org.apache.camel.Predicate;
import org.apache.camel.language.tokenizer.TokenizeLanguage;
import org.apache.camel.spi.Metadata;
import org.apache.camel.util.ExpressionToPredicateAdapter;

/**
 * For expressions and predicates using a body or header tokenizer.
 *
 * @see TokenizeLanguage
 */
@Metadata(label = "language")
@XmlRootElement(name = "tokenize")
@XmlAccessorType(XmlAccessType.FIELD)
public class TokenizerExpression extends ExpressionDefinition {
    @XmlAttribute(required = true)
    private String token;
    @XmlAttribute
    private String endToken;
    @XmlAttribute
    private String inheritNamespaceTagName;
    @XmlAttribute
    private String headerName;
    @XmlAttribute
    private Boolean regex;
    @XmlAttribute
    private Boolean xml;
    @XmlAttribute
    private Boolean includeTokens;
    @XmlAttribute
    private Integer group;

    public TokenizerExpression() {
    }

    @Override
    public String getLanguage() {
        return "tokenize";
    }

    public String getToken() {
        return token;
    }

    /**
     * The (start) token to use as tokenizer, for example \n for a new line token
     */
    public void setToken(String token) {
        this.token = token;
    }

    public String getEndToken() {
        return endToken;
    }

    /**
     * The end token to use as tokenizer if using start/end token pairs.
     */
    public void setEndToken(String endToken) {
        this.endToken = endToken;
    }

    public String getHeaderName() {
        return headerName;
    }

    /**
     * Name of header to tokenize instead of using the message body.
     */
    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    /**
     * If the token is a regular expression pattern.
     * <p/>
     * The default value is false
     */
    public void setRegex(boolean regex) {
        this.regex = regex;
    }

    public Boolean getRegex() {
        return regex;
    }

    public String getInheritNamespaceTagName() {
        return inheritNamespaceTagName;
    }

    /**
     * To inherit namepaces from a root/parent tag name
     */
    public void setInheritNamespaceTagName(String inheritNamespaceTagName) {
        this.inheritNamespaceTagName = inheritNamespaceTagName;
    }

    public Boolean getXml() {
        return xml;
    }

    /**
     * Whether the input is XML messages.
     * This option must be set to true if working with XML payloads.
     */
    public void setXml(Boolean xml) {
        this.xml = xml;
    }

    public Boolean getIncludeTokens() {
        return includeTokens;
    }

    /**
     * Whether to include the tokens in the parts
     * <p/>
     * The default value is false
     */
    public void setIncludeTokens(Boolean includeTokens) {
        this.includeTokens = includeTokens;
    }

    public Integer getGroup() {
        return group;
    }

    /**
     * To group N parts together, for example to split big files into chunks of 1000 lines.
     */
    public void setGroup(Integer group) {
        this.group = group;
    }

    @Override
    public Expression createExpression(CamelContext camelContext) {
        // special for new line tokens, if defined from XML then its 2 characters, so we replace that back to a single char
        if (token.startsWith("\\n")) {
            token = '\n' + token.substring(2);
        }

        TokenizeLanguage language = new TokenizeLanguage();
        language.setToken(token);
        language.setEndToken(endToken);
        language.setInheritNamespaceTagName(inheritNamespaceTagName);
        language.setHeaderName(headerName);
        if (regex != null) {
            language.setRegex(regex);
        }
        if (xml != null) {
            language.setXml(xml);
        }
        if (includeTokens != null) {
            language.setIncludeTokens(includeTokens);
        }
        if (group != null) {
            if (group <= 0) {
                throw new IllegalArgumentException("Group must be a positive number, was: " + group);
            }
            language.setGroup(group);
        }
        return language.createExpression();
    }

    @Override
    public Predicate createPredicate(CamelContext camelContext) {
        Expression exp = createExpression(camelContext);
        return ExpressionToPredicateAdapter.toPredicate(exp);
    }

    @Override
    public String toString() {
        if (endToken != null) {
            return "tokenize{body() using tokens: " + token + "..." + endToken + "}";
        } else {
            return "tokenize{" + (headerName != null ? "header: " + headerName : "body()") + " using token: " + token + "}";
        }
    }
}