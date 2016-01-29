package com.divisors.projectcuttlefish.contentmanager.api.gh;

public class GithubRepository {
	protected GithubApiService api;
	protected String owner;
	protected String name;
	public GithubRepository(GithubApiService api, String owner, String name) {
		this.api = api;
		this.owner = owner;
		this.name = name;
	}
}
