/**
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db.model;

public class C8KVReadValuesOptions extends MixinBase implements C8KVKeysMixin<C8KVReadValuesOptions>,
        PaginationMixin<C8KVReadValuesOptions>, GroupIdMixin<C8KVReadValuesOptions>,
        StrongConsistencyMixin<C8KVReadValuesOptions>, StreamTransactionIdMixin<C8KVReadValuesOptions> {}
