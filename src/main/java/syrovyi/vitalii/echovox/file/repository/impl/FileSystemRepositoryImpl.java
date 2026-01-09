package syrovyi.vitalii.echovox.file.repository.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import syrovyi.vitalii.echovox.common.exception.enums.ErrorCode;
import syrovyi.vitalii.echovox.common.exception.exception.ClientBackendException;
import syrovyi.vitalii.echovox.file.repository.FileSystemRepository;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FileSystemRepositoryImpl implements FileSystemRepository {
    @Value("${file.upload-dir}")
    private Path rootLocation;

    @PostConstruct
    @Override
    public void init() {
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.IO_ERROR, "Could not initialize storage", e);
        }
    }

    @Override
    public boolean exists(String filename) {
        return Files.exists(this.rootLocation.resolve(filename));
    }

    @Override
    public void save(String filename, byte[] content) {
        try {
            Path destinationFile = this.rootLocation.resolve(filename);
            Files.write(destinationFile, content);
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.IO_ERROR, "Failed to store file " + filename, e);
        }
    }

    @Override
    public void delete(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.IO_ERROR, "Could not delete file: " + filename, e);
        }
    }

    @Override
    public byte[] readFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.IO_ERROR, "Could not read file: " + filename, e);
        }
    }

    @Override
    public Stream<Path> findFiles(String globPattern) {
        try {
            DirectoryStream<Path> dirStream = Files.newDirectoryStream(this.rootLocation, globPattern);

            return StreamSupport.stream(dirStream.spliterator(), false)
                    .filter(path -> !Files.isDirectory(path))
                    .onClose(() -> {
                        try { dirStream.close(); } catch (IOException e) { log.error("Error closing stream", e); }
                    });
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.IO_ERROR, "Failed to find files with pattern: " + globPattern, e);
        }
    }
}
