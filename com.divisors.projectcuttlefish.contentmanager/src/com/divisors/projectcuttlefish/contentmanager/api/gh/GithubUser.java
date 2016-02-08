package com.divisors.projectcuttlefish.contentmanager.api.gh;

public class GithubUser extends GithubApiObject {
	public GithubUser(GithubApiService api, String name) {
		super(api, "/users/" + name);
	}
	public String getName() {
		return properties.getString("name");
	}
}
