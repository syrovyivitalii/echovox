package syrovyi.vitalii.echovox.file.repository;

import jakarta.annotation.PostConstruct;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileSystemRepository {
    @PostConstruct
    void init();

    boolean exists(String filename);

    void save(String filename, byte[] content);

    void delete(String filename);

    byte[] readFile(String filename);

    Stream<Path> findFiles(String globPattern);
}
