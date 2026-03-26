package com.project.evaluation.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.project.evaluation.service.SensitiveWordService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SensitiveWordFilter extends OncePerRequestFilter {
    @Autowired
    private SensitiveWordService sensitiveWordService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!sensitiveWordService.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }
        HttpServletRequest wrapped = new SanitizedRequestWrapper(request, sensitiveWordService, objectMapper);
        filterChain.doFilter(wrapped, response);
    }

    private static class SanitizedRequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, String[]> sanitizedParameterMap;
        private final byte[] sanitizedBody;

        SanitizedRequestWrapper(HttpServletRequest request, SensitiveWordService service, ObjectMapper objectMapper)
                throws IOException {
            super(request);
            this.sanitizedParameterMap = sanitizeParameters(request.getParameterMap(), service);
            this.sanitizedBody = sanitizeBody(request, service, objectMapper);
        }

        @Override
        public String getParameter(String name) {
            String[] values = sanitizedParameterMap.get(name);
            return values == null || values.length == 0 ? null : values[0];
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Collections.unmodifiableMap(sanitizedParameterMap);
        }

        @Override
        public String[] getParameterValues(String name) {
            return sanitizedParameterMap.get(name);
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream bais = new ByteArrayInputStream(sanitizedBody);
            return new ServletInputStream() {
                @Override
                public int read() {
                    return bais.read();
                }

                @Override
                public boolean isFinished() {
                    return bais.available() <= 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }

        private static Map<String, String[]> sanitizeParameters(Map<String, String[]> original,
                                                                SensitiveWordService service) {
            if (original == null || original.isEmpty()) return Collections.emptyMap();
            Map<String, String[]> map = new LinkedHashMap<>(original.size());
            for (Map.Entry<String, String[]> entry : original.entrySet()) {
                String key = entry.getKey();
                String[] arr = entry.getValue();
                if (arr == null) {
                    map.put(key, null);
                    continue;
                }
                String[] copied = new String[arr.length];
                for (int i = 0; i < arr.length; i++) {
                    copied[i] = service.sanitizeByField(key, arr[i]);
                }
                map.put(key, copied);
            }
            return map;
        }

        private static byte[] sanitizeBody(HttpServletRequest request,
                                           SensitiveWordService service,
                                           ObjectMapper objectMapper) throws IOException {
            String contentType = request.getContentType();
            if (contentType == null || !contentType.toLowerCase().contains("application/json")) {
                return StreamUtils.copyToByteArray(request.getInputStream());
            }
            byte[] body = StreamUtils.copyToByteArray(request.getInputStream());
            if (body.length == 0) return body;
            try {
                JsonNode root = objectMapper.readTree(body);
                JsonNode sanitized = sanitizeJsonNode(root, null, service);
                return objectMapper.writeValueAsBytes(sanitized);
            } catch (Exception ex) {
                return body;
            }
        }

        private static JsonNode sanitizeJsonNode(JsonNode node, String fieldName, SensitiveWordService service) {
            if (node == null) return null;
            if (node.isObject()) {
                ObjectNode obj = (ObjectNode) node;
                obj.fieldNames().forEachRemaining(name -> obj.set(name, sanitizeJsonNode(obj.get(name), name, service)));
                return obj;
            }
            if (node.isArray()) {
                ArrayNode arr = (ArrayNode) node;
                for (int i = 0; i < arr.size(); i++) {
                    arr.set(i, sanitizeJsonNode(arr.get(i), fieldName, service));
                }
                return arr;
            }
            if (node.isTextual()) {
                String oldVal = node.asText();
                String newVal = service.sanitizeByField(fieldName, oldVal);
                return TextNode.valueOf(newVal);
            }
            return node;
        }
    }
}

