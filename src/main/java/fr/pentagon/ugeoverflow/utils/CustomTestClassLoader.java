package fr.pentagon.ugeoverflow.utils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * A custom class loader for loading and compiling test classes from source files.
 */
public final class CustomTestClassLoader {

    /** The Java compiler instance. */
    private static final JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();

    /** The URL class loader used to load classes dynamically. */
    private final URLClassLoader urlClassLoader;

    /** The directory containing the source files. */
    private final Path sourcesDirectory;

    /**
     * Constructs a new CustomTestClassLoader.
     *
     * @param sourcesDirectory the directory containing the source files
     * @param urlClassLoader   the URLClassLoader for loading classes dynamically
     */
    private CustomTestClassLoader(Path sourcesDirectory, URLClassLoader urlClassLoader) {
        this.sourcesDirectory = sourcesDirectory;
        this.urlClassLoader = urlClassLoader;
    }

    /**
     * Creates a new CustomTestClassLoader for the specified sources directory.
     *
     * @param sourcesDirectory the directory containing the source files
     * @return a new CustomTestClassLoader instance
     * @throws NoSuchFileException   if the specified directory does not exist
     * @throws MalformedURLException if a URL cannot be formed for the directory
     */
    public static CustomTestClassLoader in(Path sourcesDirectory) throws NoSuchFileException, MalformedURLException {
        Objects.requireNonNull(sourcesDirectory);
        if (Files.notExists(sourcesDirectory)) {
            throw new NoSuchFileException(sourcesDirectory.toString());
        }
        if (!Files.isDirectory(sourcesDirectory)) {
            throw new IllegalArgumentException("Must be a folder");
        }
        var classLoader = URLClassLoader.newInstance(new URL[]{sourcesDirectory.toUri().toURL()});
        return new CustomTestClassLoader(sourcesDirectory, classLoader);
    }

    /**
     * Removes the package declaration from the specified file.
     *
     * @param filePath the path to the file
     * @return an optional containing the package URI, if present; otherwise an empty optional
     * @throws IOException if an I/O error occurs
     */
    private static Optional<String> removePackage(Path filePath) throws IOException {
        if (!Files.exists(filePath)) throw new NoSuchFileException(filePath.toString());
        int keepingIndex;
        var lines = Files.readAllLines(filePath);
        String dependencyURI = null;
        var it = lines.iterator();
        for (keepingIndex = 0; it.hasNext(); keepingIndex++) {
            var line = it.next().stripLeading();
            if (line.startsWith("package")) {
                dependencyURI = line.substring(7, line.length() - 1).stripLeading();
            } else if (!line.isBlank()) {
                break;
            }
        }
        Files.write(filePath, lines.subList(keepingIndex, lines.size()));
        return Optional.ofNullable(dependencyURI);
    }

    /**
     * Removes the package declaration and import statements related to the specified dependency from the test file.
     *
     * @param testFilePath    the path to the test file
     * @param dependencyURI   the URI of the dependency package
     * @throws IOException if an I/O error occurs
     */
    private static void removePackageAndImports(Path testFilePath, String dependencyURI) throws IOException {
        if (!Files.exists(testFilePath)) throw new NoSuchFileException(testFilePath.toString());
        try (var lines = Files.lines(testFilePath)) {
            var remainingLines = lines.dropWhile(s -> s.isBlank() || s.stripLeading().startsWith("package"));
            Files.write(testFilePath, remainingLines.filter(s -> !s.contains(dependencyURI)).toList());
        }
    }

    /**
     * Loads and compiles the test class from the specified source files.
     *
     * @param testFileName      the name of the test file
     * @param dependencyFileName the name of the dependency file
     * @return the loaded test class
     * @throws IOException            if an I/O error occurs
     * @throws ClassNotFoundException if the class cannot be found
     */
    public Class<?> fromSourceFiles(String testFileName, String dependencyFileName) throws IOException, ClassNotFoundException {
        Objects.requireNonNull(testFileName);
        Objects.requireNonNull(dependencyFileName);
        if (!testFileName.endsWith(".java") || !dependencyFileName.endsWith(".java")) {
            throw new IllegalArgumentException("Must be a java file.");
        }
        var dependencyFilePath = sourcesDirectory.resolve(dependencyFileName);
        var testFilePath = sourcesDirectory.resolve(testFileName);
        if (Files.notExists(testFilePath)) {
            throw new NoSuchFileException(testFilePath.toString());
        }
        if (Files.notExists(dependencyFilePath)) {
            throw new NoSuchFileException(dependencyFilePath.toString());
        }
        var dependencyURI = removePackage(dependencyFilePath);
        if(dependencyURI.isPresent()){
            removePackageAndImports(testFilePath, dependencyURI.get());
        }else {
            removePackage(testFilePath);
        }
        JAVA_COMPILER.run(null, null, null, dependencyFilePath.toString(), testFilePath.toString());
        return Class.forName(testFileName.substring(0, testFileName.lastIndexOf(".")), false, urlClassLoader);
    }

}

