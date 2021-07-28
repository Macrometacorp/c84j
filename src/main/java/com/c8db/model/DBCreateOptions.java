/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */
package com.c8db.model;

public class DBCreateOptions {

	private String tenant;

	private String name;

	private Options options;

	public DBCreateOptions() {
		super();
	}

	public final String getTenant() {
		return tenant;
	}

	public String getName() {
		return name;
	}

	public Options getOptions() {
		return options;
	}

	/**
	 * @param tenant Valid database name
	 * @param name   Valid database name
	 * @return options
	 */
	protected DBCreateOptions geoFabric(final String tenant, final String name) {
		this.tenant = tenant;
		this.name = name;
		return this;
	}

	/**
	 * 
	 * @param options Has to contain a valid dc
	 * @return options
	 */
	protected DBCreateOptions options(final String spotDc, final String dcList) {
		Options options = new Options();
		options.setSpotDc(spotDc);
		options.setDcList(dcList);
		this.options = options;
		return this;
	}

	public class Options {
		private String dcList;
		private String spotDc;

		public String getDcList() {
			return dcList;
		}

		public void setDcList(String dcList) {
			this.dcList = dcList;
		}

		public String getSpotDc() {
			return spotDc;
		}

		public void setSpotDc(String spotDc) {
			this.spotDc = spotDc;
		}

	}
}
