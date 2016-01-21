package com.divisors.projectcuttlefish.contentmanager.api.updater;

import java.io.IOException;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryBranch;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

public class GitAutoUpdateService {
	GitHubClient client = new GitHubClient();
	RepositoryService repos = new RepositoryService(client);
	public GitAutoUpdateService() throws IOException {
		
	}
	public void watch(String username, String name) throws IOException {
		Repository repo = repos.getRepository(username, name);
		System.out.println("GIT::Got repo " + repo.getName() + ": " + repo.getDescription());
		for (RepositoryBranch branch : repos.getBranches(repo)) {
			 System.out.println("\t"+branch.getName());
		}
	}
}
