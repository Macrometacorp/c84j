/*
 * DISCLAIMER
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.c8db.entity;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 */
public class CursorEntities<T> {

    private List<CursorEntity<T>> cursorEntityList = new ArrayList<>();

    public CursorEntities(List<CursorEntity<T>> cursorEntityList){
        this.cursorEntityList = cursorEntityList;
    }

    public List<CursorEntity<T>> getCursorEntityList(){
        return unmodifiableList(this.cursorEntityList);
    }

}
