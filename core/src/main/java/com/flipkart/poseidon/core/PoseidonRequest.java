/*
 * Copyright 2015 Flipkart Internet, pvt ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.poseidon.core;

import com.google.common.collect.ImmutableMap;
import flipkart.lego.api.entities.Request;
import org.springframework.http.HttpMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PoseidonRequest implements Request {

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    private final String url;
    private final HttpMethod httpMethod;
    private final ImmutableMap<String, Cookie> cookies;
    private final ImmutableMap<String, Object> headers;


    public PoseidonRequest(HttpServletRequest httpRequest, HttpMethod method) {
        this.url = httpRequest.getPathInfo();
        this.httpMethod = method;
        headers = extractHeaders(httpRequest);
        cookies = extractCookies(httpRequest);

        if (httpRequest.getParameterMap() != null) {
            attributes.putAll(httpRequest.getParameterMap());
        }
    }

    public String getUrl() {
        return url;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);

    }

    @Override
    public void setAttributes(Map<String, Object> map) {
        if (attributes != null) {
            this.attributes.putAll(attributes);
        }
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public Map<String, Object> getAttributeMap() {
        return new HashMap<>(attributes);
    }

    @Override
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    private ImmutableMap<String, Object> extractHeaders(HttpServletRequest httpServletRequest) {
        Map<String, Object> headers = new HashMap<>();
        Enumeration headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            if (httpServletRequest.getHeader(key) != null) {
                headers.put(key, httpServletRequest.getHeader(key));
            }
        }

        return ImmutableMap.copyOf(headers);
    }

    private ImmutableMap<String, Cookie> extractCookies(HttpServletRequest httpServletRequest) {
        Map<String, Cookie> cookies = new HashMap<>();
        Cookie[] receivedCookies = httpServletRequest.getCookies();
        if(receivedCookies != null) {
            for (Cookie cookie : receivedCookies) {
                cookies.put(cookie.getName(), cookie);
            }
        }

        return ImmutableMap.copyOf(cookies);
    }

    public Cookie getCookie(String key) {
        return cloneCookie(cookies.get(key));
    }

    public Object getHeader(String key) {
        return headers.get(key);
    }

    private Cookie cloneCookie(Cookie cookie) {
        Cookie newCookie = null;
        if (cookie != null) {
            newCookie = new Cookie(cookie.getName(), cookie.getValue());
            newCookie.setSecure(cookie.getSecure());
            newCookie.setMaxAge(cookie.getMaxAge());
            newCookie.setVersion(cookie.getVersion());

            if (cookie.getDomain() != null) {
                newCookie.setDomain(cookie.getDomain());
            }
            if (cookie.getComment() != null) {
                newCookie.setComment(cookie.getComment());
            }
            if (cookie.getPath() != null) {
                newCookie.setPath(cookie.getPath());
            }
        }

        return newCookie;
    }
}
