package com.divisors.projectcuttlefish.contentmanager.api.resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.divisors.projectcuttlefish.httpserver.api.Version;

public class DirectoryResourceLoader {
	Path dir;
	Function<Path, Version> mapper;

	public DirectoryResourceLoader(Path p, Function<Path, Version> mapper) {
		this.dir = p;
		this.mapper = mapper;
		System.out.println("Loading from " + p.toString());
	}

	public List<FileResource> load() {
		List<FileResource> result = new LinkedList<>();
		try {
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (!attrs.isDirectory()) {
						String name = dir.relativize(file).toString().replace(File.separator, "/");
						result.add(new FileResource(file, name, mapper.apply(file)));
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//force generation of Etags
		for (FileResource file : result) {
			file.getEtag(true);
			file.getEtag(false);
		}
		return result;
	}
}
