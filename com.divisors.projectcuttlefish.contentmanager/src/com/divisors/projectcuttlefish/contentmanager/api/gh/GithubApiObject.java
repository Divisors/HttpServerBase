package com.divisors.projectcuttlefish.contentmanager.api.gh;

import java.io.IOException;

import org.json.JSONObject;

public class GithubApiObject {
	protected transient GithubApiService api;
	protected String path;
	protected final JSONObject properties;
	public GithubApiObject(GithubApiService api, String path) {
		this.api = api;
		this.path = path;
		this.properties = new JSONObject();
	}
	public String getPath() {
		return path;
	}
	public void fetch() throws IOException {
		api.query(this);
	}
	protected void onUpdate(JSONObject data) {
		
	}
}
