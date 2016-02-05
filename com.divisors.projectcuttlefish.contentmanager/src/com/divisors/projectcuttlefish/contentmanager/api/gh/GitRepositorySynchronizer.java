package com.divisors.projectcuttlefish.contentmanager.api.gh;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

public class GitRepositorySynchronizer {
	final File localPath;
	final String url;
	final String branch;
	final Git git;
	public GitRepositorySynchronizer(File local, String url, String branch) throws IOException, URISyntaxException, GitAPIException {
		this.localPath = local;
		this.url = url;
		this.branch = branch;
		git = Git.init()
			.setDirectory(local)
			.call();
		
		RemoteAddCommand cmd = git.remoteAdd();
		cmd.setUri(new URIish(url));
		cmd.setName("GitHub");
		RemoteConfig cfg = cmd.call();
		System.out.println("Remote: " + cfg.getName());
		
		git.fetch().setRemote(url).call();
		
		git.checkout()
			.setName(branch)
			.call();
	}
	public void update() throws InvalidRemoteException, TransportException, GitAPIException {
		git.pull()
			.call();
	}
}
