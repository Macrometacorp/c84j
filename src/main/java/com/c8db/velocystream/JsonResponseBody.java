/**
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db.velocystream;

import com.arangodb.velocypack.VPackSlice;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonResponseBody implements ResponseBody {

    private VPackSlice value;

}
