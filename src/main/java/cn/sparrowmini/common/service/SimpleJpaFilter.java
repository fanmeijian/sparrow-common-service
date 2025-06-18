package cn.sparrowmini.common.service;

import lombok.Data;

@Data
public class SimpleJpaFilter {
    private String name;
    private String operator; // 支持 "=", "like", ">", "<" 等
    private Object value;
}
