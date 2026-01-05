package com.github.heroslender.hero_api.service.impl;

import com.github.heroslender.hero_api.exceptions.StorageException;
import com.github.heroslender.hero_api.exceptions.StorageFileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class FileSystemResourceStorageService {

    public void store(Path location, String filename, MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            Path destinationFile = location.resolve(Paths.get(filename)).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(location.toAbsolutePath())) {
                // This is a security check
                throw new StorageException("Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file.");
        }
    }

    public Stream<Path> loadAll(Path location) {
        try (Stream<Path> files = Files.walk(location, 1)) {
            return files.filter(path -> !path.equals(location)).map(location::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files");
        }

    }

    public Path load(Path location, String filename) {
        return location.resolve(filename);
    }

    public Resource loadAsResource(Path location, String filename) {
        try {
            Path file = load(location, filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename);
        }
    }

    public void deleteAll(Path location) {
        FileSystemUtils.deleteRecursively(location.toFile());
    }

    public void init(Path... paths) {
        try {
            for (Path path : paths) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}