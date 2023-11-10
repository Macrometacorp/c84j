/**
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db.velocystream;

import com.arangodb.velocypack.VPackSlice;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class BinaryRequestBody implements RequestBody {

    private Collection<Item> items;

    @Data
    @AllArgsConstructor
    public static class Item {

        private VPackSlice meta;

        private byte[] value;

    }

}
