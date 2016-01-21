package com.divisors.projectcuttlefish.contentmanager.api;

import java.io.File;
import java.nio.file.Path;

public class FileResource implements Resource {
	protected final Path path;
	public FileResource(File f) {
		this.path = f.toPath();
	}
	public FileResource(Path p) {
		this.path = p;
	}
	@Override
	public String getName() {
		return path.toString();
	}
	@Override
	public boolean isRoot() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Resource getParent() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long estimateSize() {
		// TODO Auto-generated method stub
		return 0;
	}
}
