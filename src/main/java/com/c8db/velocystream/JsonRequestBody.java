/**
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db.velocystream;

import com.arangodb.velocypack.VPackSlice;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonRequestBody implements RequestBody {

    private VPackSlice value;

}
