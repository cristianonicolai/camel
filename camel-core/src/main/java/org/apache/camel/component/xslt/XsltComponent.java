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
package org.apache.camel.component.xslt;

import java.util.Map;
import javax.xml.transform.URIResolver;

import org.apache.camel.Endpoint;
import org.apache.camel.builder.xml.XsltUriResolver;
import org.apache.camel.converter.jaxp.XmlConverter;
import org.apache.camel.impl.UriEndpointComponent;
import org.apache.camel.util.ResourceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An <a href="http://camel.apache.org/xslt.html">XSLT Component</a>
 * for performing XSLT transforms of messages
 */
public class XsltComponent extends UriEndpointComponent {

    private static final Logger LOG = LoggerFactory.getLogger(XsltComponent.class);

    private XmlConverter xmlConverter;
    private URIResolver uriResolver;
    private boolean contentCache = true;
    private boolean saxon;

    public XsltComponent() {
        super(XsltEndpoint.class);
    }

    public XmlConverter getXmlConverter() {
        return xmlConverter;
    }

    public void setXmlConverter(XmlConverter xmlConverter) {
        this.xmlConverter = xmlConverter;
    }

    public URIResolver getUriResolver() {
        return uriResolver;
    }

    public void setUriResolver(URIResolver uriResolver) {
        this.uriResolver = uriResolver;
    }

    public boolean isContentCache() {
        return contentCache;
    }

    public void setContentCache(boolean contentCache) {
        this.contentCache = contentCache;
    }

    public boolean isSaxon() {
        return saxon;
    }

    public void setSaxon(boolean saxon) {
        this.saxon = saxon;
    }

    protected Endpoint createEndpoint(String uri, final String remaining, Map<String, Object> parameters) throws Exception {
        XsltEndpoint endpoint = new XsltEndpoint(uri, this);
        endpoint.setConverter(getXmlConverter());
        endpoint.setContentCache(isContentCache());
        endpoint.setSaxon(isSaxon());

        String resourceUri = remaining;

        // if its a http uri, then append additional parameters as they are part of the uri
        if (ResourceHelper.isHttpUri(resourceUri)) {
            resourceUri = ResourceHelper.appendParameters(resourceUri, parameters);
        }
        LOG.debug("{} using schema resource: {}", this, resourceUri);
        endpoint.setResourceUri(resourceUri);

        // lookup custom resolver to use
        URIResolver resolver = resolveAndRemoveReferenceParameter(parameters, "uriResolver", URIResolver.class);
        if (resolver == null) {
            // not in endpoint then use component specific resolver
            resolver = getUriResolver();
        }
        if (resolver == null) {
            // fallback to use a Camel specific resolver
            resolver = new XsltUriResolver(getCamelContext().getClassResolver(), remaining);
        }
        endpoint.setUriResolver(resolver);

        setProperties(endpoint, parameters);
        if (!parameters.isEmpty()) {
            // additional parameters need to be stored on endpoint as they can be used to configure xslt builder additionally
            endpoint.setParameters(parameters);
        }

        return endpoint;
    }

}
