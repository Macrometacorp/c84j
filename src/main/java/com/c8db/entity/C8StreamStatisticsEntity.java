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

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class C8StreamStatisticsEntity implements Entity {

  //TODO:replication & subscriptions
    private Double msgRateIn;
    private Double msgThroughputIn;
    private Double msgRateOut;
    private Double averageMsgSize;
    private Long storageSize;
    private List<Publisher> publishers;
    private Map<String, Object> replication;
    
    public Double getMsgRateIn() {
        return msgRateIn;
    }
    
    public Double getMsgThroughputIn() {
        return msgThroughputIn;
    }
    
    public Double getMsgRateOut() {
        return msgRateOut;
    }
    
    public Double getAverageMsgSize() {
        return averageMsgSize;
    }
    
    public Long getStorageSize() {
        return storageSize;
    }
    
    public List<Publisher> getPublishers() {
        return publishers;
    }
    
    public Map<String, Object> getReplication() {
        return replication;
    }
      
    public static class Publisher {
        private Double msgRateIn;
        private Double msgThroughputIn;
        private Double msgRateOut;
        private Double averageMsgSize;
        private Long producerId;
        public Map<String, String> metadata;
        private String connectedSince;
        private String producerName;
        private String address;
        private String clientVersion;
        
        public void setMsgRateIn(Double msgRateIn) {
            this.msgRateIn = msgRateIn;
        }
        public void setMsgThroughputIn(Double msgThroughputIn) {
            this.msgThroughputIn = msgThroughputIn;
        }
        public void setMsgRateOut(Double msgRateOut) {
            this.msgRateOut = msgRateOut;
        }
        public void setAverageMsgSize(Double averageMsgSize) {
            this.averageMsgSize = averageMsgSize;
        }
        public void setProducerId(Long producerId) {
            this.producerId = producerId;
        }
        public void setMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
        }
        public void setConnectedSince(String connectedSince) {
            this.connectedSince = connectedSince;
        }
        public void setProducerName(String producerName) {
            this.producerName = producerName;
        }
        public void setAddress(String address) {
            this.address = address;
        }
        public String getClientVersion() {
            return clientVersion;
        }
        public void setClientVersion(String clientVersion) {
            this.clientVersion = clientVersion;
        }
        public Double getMsgRateIn() {
            return msgRateIn;
        }
        public Double getMsgThroughputIn() {
            return msgThroughputIn;
        }
        public Double getMsgRateOut() {
            return msgRateOut;
        }
        public Double getAverageMsgSize() {
            return averageMsgSize;
        }
        public Long getProducerId() {
            return producerId;
        }
        public Map<String, String> getMetadata() {
            return metadata;
        }
        public String getConnectedSince() {
            return connectedSince;
        }
        public String getProducerName() {
            return producerName;
        }
        public String getAddress() {
            return address;
        }
    }
}
