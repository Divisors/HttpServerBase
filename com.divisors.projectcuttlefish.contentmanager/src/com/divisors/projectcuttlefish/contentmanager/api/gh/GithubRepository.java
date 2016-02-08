package com.divisors.projectcuttlefish.contentmanager.api.gh;

public class GithubRepository extends GithubApiObject {
	protected String owner;
	protected String name;
	public GithubRepository(GithubApiService api, String owner, String name) {
		super(api, "/repos/" + owner + "/" + name);
		this.owner = owner;
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public String getOwner() {
		return owner;
	}
	
}
