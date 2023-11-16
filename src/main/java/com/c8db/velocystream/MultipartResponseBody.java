/**
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db.velocystream;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class MultipartResponseBody implements ResponseBody {

    private Collection<Item> items;

    @Data
    @AllArgsConstructor
    public static class Item {

        private Object value;

        private String contentType;

        private String name;

    }

}
