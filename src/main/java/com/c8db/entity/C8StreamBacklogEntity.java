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

/**
 *
 */
public class C8StreamBacklogEntity implements Entity {
    private Long storageSize;
    private Integer totalMessages;
    private Integer messageBacklog;
    private String brokerName;
    private String topicName;
    private Long statGeneratedAt;
    private CursorDetails cursorDetails;
    private List<LedgerDetails> dataLedgerDetails = new ArrayList<LedgerDetails>();
    
    public Long getStorageSize() {
        return storageSize;
    }

    public Integer getTotalMessages() {
        return totalMessages;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public String getTopicName() {
        return topicName;
    }

    public Long getStatGeneratedAt() {
        return statGeneratedAt;
    }

    public CursorDetails getCursorDetails() {
        return cursorDetails;
    }

    public List<LedgerDetails> getDataLedgerDetails() {
        return dataLedgerDetails;
    }

    public static class CursorDetails {
        private long cursorBacklog;
        private long cursorLedgerId;
        
        public long getCursorBacklog() {
            return cursorBacklog;
        }
        public long getCursorLedgerId() {
            return cursorLedgerId;
        }

    }

    public static class LedgerDetails {
        private long entries;
        private long timestamp;
        private long size;
        private long ledgerId;
        public long getEntries() {
            return entries;
        }
        public long getTimestamp() {
            return timestamp;
        }
        public long getSize() {
            return size;
        }
        public long getLedgerId() {
            return ledgerId;
        }

    }

    public Integer getMessageBacklog() {
        return messageBacklog;
    }
}
