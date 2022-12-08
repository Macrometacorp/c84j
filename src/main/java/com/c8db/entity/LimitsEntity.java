/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

import lombok.Data;

@Data
public class LimitsEntity implements Entity {

	private Database database; 
	private Streams streamsLocal, streamsGlobal;
	private Compute compute;
	private CEP cep;
	private DataMeshIntegrations dataMeshIntegration;

	private boolean defaultsEnabled;
	
	@Data	
	public static class Database {
		private int maxDocumentSize;
		private int maxDocumentsReturnedByQuery;
		private int maxQueryExecutionTimeInMs;
		private long maxQueryMemoryBytes;
		private int maxGeoFabricsPerTenant;
		private int maxCollectionsPerFabric;
		private int maxGraphsPerFabric;
		private int maxIndexes;
		private int maxViewsPerFabric;
		private int maxRequestsPerDay;
		private int maxRequestPerMinute;
		private long maxStoragePerRegion;
		private int maxRestQLUsagePerFabric;
		private int maxRestQLUsagePerDay;
		private int maxDocumentsImportedPerAPICall;
	}

	@Data
	public static class Streams {
		private int maxStreamsCount;
		private int maxProducersCount;
		private int maxConsumersCount;
		private int maxSubscriptionsCount;
		private int maxBacklogMessageTTLMin;
		private long maxBacklogStorageSizeMB;
		private long maxDispatchThrottlingRateInByte;
	}

	@Data
	public static class Compute {
		private int maxConfigmapsCount;
		private int maxEphimeralStorageMB;
		private int maxLimitsCpuMi;
		private int maxLimitsMemoryMB;
		private int maxPodsCount;
		private int maxRequestsCpuMi;
		private int maxRequestsMemoryMB;
		private int maxSecretsCount;
		private int maxServicesCount;
	}

	@Data
	public static class CEP {
		private int maxMemoryMBPerWorker;
		private int maxPublishedWorkers;
		private int maxWorkersMemoryMB;
		private int maxWorkersCpuSecondsPerMinute;
		private int maxWorkersThroughputInMBPerMinute;
		private int maxWorkersThroughputOutMBPerMinute;
		private int maxCpuSecondsPerMinutePerWorker;
		private int maxLogsLengthKBPerMinutePerWorker;
	}

	@Data
	public static class DataMeshIntegrations {
		private int maxIntegrationsPerTenant;
		private int maxConnectorWorkflowsPerTenant;
	}
}
