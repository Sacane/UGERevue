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

/**
 * CustomClassLoader is a utility class for loading classes dynamically from Java source files and compiling them at runtime.
 */
public final class CustomClassLoader {

    // System Java Compiler
    private static final JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();

    // URLClassLoader for loading compiled classes
    private final URLClassLoader urlClassLoader;

    // Path to the directory containing Java source files
    private final Path sourcesDirectory;

    /**
     * Constructs a CustomClassLoader with the specified sources directory and URLClassLoader.
     *
     * @param sourcesDirectory Path to the directory containing Java source files
     * @param urlClassLoader  URLClassLoader for loading compiled classes
     */
    private CustomClassLoader(Path sourcesDirectory, URLClassLoader urlClassLoader) {
        this.sourcesDirectory = sourcesDirectory;
        this.urlClassLoader = urlClassLoader;
    }

    /**
     * Creates a new CustomClassLoader instance with the specified sources directory.
     *
     * @param sourcesDirectory Path to the directory containing Java source files
     * @return a new CustomClassLoader instance
     * @throws NoSuchFileException   if the specified sources directory does not exist
     * @throws MalformedURLException if an error occurs while creating the URL for the sources directory
     */
    public static CustomClassLoader in(Path sourcesDirectory) throws NoSuchFileException, MalformedURLException {
        Objects.requireNonNull(sourcesDirectory);
        if (Files.notExists(sourcesDirectory)) {
            throw new NoSuchFileException(sourcesDirectory.toString());
        }
        if (!Files.isDirectory(sourcesDirectory)) {
            throw new IllegalArgumentException("Must be a folder");
        }
        var classLoader = URLClassLoader.newInstance(new URL[]{sourcesDirectory.toUri().toURL()});
        return new CustomClassLoader(sourcesDirectory, classLoader);
    }

    /**
     * Removes the package declaration from a Java source file.
     *
     * @param path Path to the Java source file
     * @throws IOException if an I/O error occurs while reading or writing the file
     */
    private static void removePackageFromSourceFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new NoSuchFileException(path.toString());
        }
        try (var lines = Files.lines(path)) {
            Files.write(path, lines.dropWhile(s -> s.isBlank() || s.stripLeading().startsWith("package")).toList());
        }
    }

    /**
     * Loads and compiles a class from a Java source file.
     *
     * @param sourceFile Name of the Java source file (with .java extension)
     * @return the loaded Class object
     * @throws IOException            if an I/O error occurs while reading or compiling the source file
     * @throws ClassNotFoundException if the compiled class cannot be found
     */
    public Class<?> fromSourceFile(String sourceFile) throws IOException, ClassNotFoundException {
        Objects.requireNonNull(sourceFile);
        if (!sourceFile.endsWith(".java")) {
            throw new IllegalArgumentException("Must be a java file.");
        }
        var sourceFilePath = sourcesDirectory.resolve(sourceFile);
        if (Files.notExists(sourceFilePath)) {
            throw new NoSuchFileException(sourceFilePath.toString());
        }
        removePackageFromSourceFile(sourceFilePath);
        JAVA_COMPILER.run(null, null, null, sourceFilePath.toString());
        return Class.forName(sourceFile.substring(0, sourceFile.lastIndexOf(".")), false, urlClassLoader);
    }

}

