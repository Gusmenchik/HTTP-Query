package org.example;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream body;
    private final Map<String, List<String>> queryParams;

    public Request(String method, String path, Map<String, String> headers, InputStream body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.queryParams = parseQueryParams();
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getBody() {
        return body;
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public String getQueryParam(String name) {
        List<String> values = queryParams.get(name);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    private Map<String, List<String>> parseQueryParams() {
        try {
            URI uri = new URI(path);
            String query = uri.getQuery();
            if (query != null) {
                List<NameValuePair> params = URLEncodedUtils.parse(new URI("http://example.com/?" + query), String.valueOf(StandardCharsets.UTF_8));

                return params.stream()
                        .collect(
                                HashMap::new,
                                (map, pair) -> map.computeIfAbsent(pair.getName(), k -> new ArrayList<>()).add(pair.getValue()),
                                HashMap::putAll
                        );
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}



