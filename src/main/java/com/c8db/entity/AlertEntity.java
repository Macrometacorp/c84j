/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */

package com.c8db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class AlertEntity implements Serializable {

    private static final long serialVersionUID = -1681192230942530690L;

    private String _key;
    private String version;
    private String description;
    private String fabric;
    private String region;
    private String severity;
    private String tenant;
    private Long timestamp;
    private Long timestampTtl;
    private String user;
    private String code;
    private String source;
    private Boolean acknowledged;
    private Boolean resolved;
    private Boolean notify;
    private String subject;
    private String entityType;
    private String entityName;
    private Map<String, Object> data;
}
