package net.n.example.high_concurrent_java_server.draft.web.model;

import lombok.Data;

@Data
public class Response {
    private String code;
    private Object data;
}
