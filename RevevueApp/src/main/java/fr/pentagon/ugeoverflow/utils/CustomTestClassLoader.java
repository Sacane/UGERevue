package fr.pentagon.ugeoverflow.utils;

import fr.pentagon.ugeoverflow.exception.CompilationException;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
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

    /**
     * The Java compiler instance.
     */
    private static final JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();

    /**
     * The URL class loader used to load classes dynamically.
     */
    private final URLClassLoader urlClassLoader;

    /**
     * The directory containing the source files.
     */
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
     * Ensures that the provided path is non-null and exists. If the path is null
     * or does not exist, a NoSuchFileException is thrown.
     *
     * @param path the path to check
     * @return the provided path if it exists
     * @throws NoSuchFileException if the path is null or does not exist
     */
    private static Path assertPathNonNullAndExists(Path path) throws NoSuchFileException {
        Objects.requireNonNull(path);
        if (Files.notExists(path)) {
            throw new NoSuchFileException(path.toString());
        }
        return path;
    }

    /**
     * Creates a new CustomTestClassLoader for the specified source directory.
     *
     * @param sourcesDirectory the directory containing the source files
     * @return a new CustomTestClassLoader instance
     * @throws NoSuchFileException   if the specified directory does not exist
     * @throws MalformedURLException if a URL cannot be formed for the directory
     */
    public static CustomTestClassLoader in(Path sourcesDirectory) throws NoSuchFileException, MalformedURLException {
        assertPathNonNullAndExists(sourcesDirectory);
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
        assertPathNonNullAndExists(filePath);
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
     * @param testFilePath  the path to the test file
     * @param dependencyURI the URI of the dependency package
     * @throws IOException if an I/O error occurs
     */
    private static void removePackageAndImports(Path testFilePath, String dependencyURI) throws IOException {
        Objects.requireNonNull(dependencyURI);
        assertPathNonNullAndExists(testFilePath);
        try (var lines = Files.lines(testFilePath)) {
            var remainingLines = lines.dropWhile(s -> s.isBlank() || s.stripLeading().startsWith("package"));
            Files.write(testFilePath, remainingLines.filter(s -> !s.contains(dependencyURI)).toList());
        }
    }

    /**
     * Compiles the test file and its dependency using the Java Compiler API.
     *
     * @param testFileName      the name of the test file to compile
     * @param dependencyFileName the name of the dependency file to compile
     * @throws CompilationException if compilation fails
     */
    private static void compileTestAndDependency(String testFileName, String dependencyFileName) throws CompilationException {
        var errorByteArray = new ByteArrayOutputStream();
        var trashPrintStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                // Drop all
            }
        });
        JAVA_COMPILER.run(System.in, trashPrintStream, new PrintStream(errorByteArray), dependencyFileName, testFileName);
        var error = errorByteArray.toString();
        if (!error.isBlank()) {
            throw new CompilationException(error);
        }
    }

    /**
     * Compiles and loads a test class from the provided source files.
     *
     * @param testFileName       the name of the test file to compile and load
     * @param dependencyFileName the name of the dependency file required by the test file
     * @return the compiled test class
     * @throws IOException              if an I/O error occurs
     * @throws ClassNotFoundException   if the class could not be found
     * @throws CompilationException     if the compilation process fails
     * @throws IllegalArgumentException if the file names do not end with ".java"
     * @throws NoSuchFileException      if the specified files do not exist
     */
    public Class<?> load(String testFileName, String dependencyFileName) throws IOException,
            ClassNotFoundException, CompilationException {
        Objects.requireNonNull(testFileName);
        Objects.requireNonNull(dependencyFileName);
        if (!testFileName.endsWith(".java") || !dependencyFileName.endsWith(".java")) {
            throw new IllegalArgumentException("Must be a java file.");
        }
        var dependencyFilePath = assertPathNonNullAndExists(sourcesDirectory.resolve(dependencyFileName));
        var testFilePath = assertPathNonNullAndExists(sourcesDirectory.resolve(testFileName));
        var dependencyURI = removePackage(dependencyFilePath);
        if (dependencyURI.isPresent()) {
            removePackageAndImports(testFilePath, dependencyURI.get());
        } else {
            removePackage(testFilePath);
        }
        compileTestAndDependency(testFilePath.toString(), dependencyFilePath.toString());
        return Class.forName(testFileName.substring(0, testFileName.lastIndexOf(".")), false, urlClassLoader);
    }

    /**
     * Removes all .class files in the specified directory.
     *
     * @throws IOException if an I/O error occurs while deleting the files
     */
    public void clean() throws IOException {
        try (var stream = Files.list(sourcesDirectory)) {
            stream.filter(p -> p.toString().endsWith(".class")).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    // Do nothing
                }
            });
        }
    }

}

