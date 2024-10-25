package com.voinearadu.utils.reflections;

import com.voinearadu.utils.logger.Logger;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Every method in this class is very computationally expensive. It is recommended to cache the results, and if
 * possible to use the class only in the constructors of modules and managers (one time uses).
 */
@Getter
public class Reflections {

    private final List<File> zipFiles;
    private final List<File> folders;
    private final ClassLoader classLoader;
    private final List<String> searchDomain;

    public Reflections(@NotNull List<File> zipFiles, @NotNull List<File> folders, @NotNull ClassLoader classLoader, @NotNull String... searchDomain) {
        this.zipFiles = zipFiles;
        this.folders = folders;

        String[] classPathEntries = System.getProperty("java.class.path").split(File.pathSeparator);
        for (String classPathEntry : classPathEntries) {
            if (classPathEntry.endsWith(".jar") || classPathEntry.endsWith(".zip")) {
                zipFiles.add(new File(classPathEntry));
            } else {
                folders.add(new File(classPathEntry));
            }
        }

        this.classLoader = classLoader;
        this.searchDomain = Arrays.stream(searchDomain).map(domain -> {
            if (!domain.endsWith(".")) {
                return domain + ".";
            }
            return domain;
        }).toList();
    }

    public static @Nullable Field getField(@NotNull Class<?> clazz, String fieldName) {
        Field field = null;

        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException ignored) {
        }

        if (field == null) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
            }
        }

        if (field != null) {
            field.setAccessible(true);
        }

        return field;
    }

    public static @NotNull List<Field> getFields(@NotNull Class<?> clazz) {
        Set<Field> output = new HashSet<>();

        Queue<Class<?>> classesToSearch = new LinkedList<>();
        classesToSearch.add(clazz);

        while (!classesToSearch.isEmpty()) {
            Class<?> searchClass = classesToSearch.poll();

            if (searchClass == null) {
                continue;
            }

            classesToSearch.add(searchClass.getSuperclass());

            for (Field field : searchClass.getDeclaredFields()) {
                field.setAccessible(true);
                output.add(field);
            }
        }

        return output.stream().toList();
    }

    @SuppressWarnings("unused")
    public Reflections from(String... searchDomain) {
        return new Reflections(zipFiles, folders, classLoader, searchDomain);
    }

    public @NotNull Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        for (File zipFile : zipFiles) {
            classes.addAll(getClassesFromZip(zipFile));
        }

        for (File zipFile : folders) {
            classes.addAll(getClassesFromFolder(zipFile));
        }

        return classes;
    }

    private @NotNull Set<Class<?>> getClassesFromZip(File zipFile) {
        Set<Class<?>> classes = new HashSet<>();

        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry == null || entry.isDirectory()) {
                    continue;
                }

                Class<?> clazz = processFile(entry.getName());

                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        } catch (Exception error) {
            Logger.error(error);
        }

        return classes;
    }

    private @NotNull Set<Class<?>> getClassesFromFolder(File folder) {
        Set<Class<?>> classes = new HashSet<>();

        try {
            for (File file : FileUtils.listFiles(folder, new String[]{"class"}, true)) {
                Class<?> clazz = processFile(file.getAbsolutePath().replace(folder.getAbsolutePath() + File.separator, ""));

                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        } catch (Exception error) {
            Logger.error(error);
        }

        return classes;
    }

    private @Nullable Class<?> processFile(String fileName) {
        if (!fileName.endsWith(".class")) {
            return null;
        }

        fileName = fileName.replace("/", ".");
        fileName = fileName.replace("\\", ".");
        fileName = fileName.replace(".class", "");

        if (searchDomain.stream().noneMatch(fileName::startsWith)) {
            return null;
        }

        String simpleClassName = fileName.substring(fileName.lastIndexOf('.') + 1);

        // Skip Mixin classes
        if (simpleClassName.contains("Mixin")) {
            return null;
        }

        try {
            return classLoader.loadClass(fileName);
        } catch (Throwable throwable) {
            Logger.error("Failed to load class " + fileName + " from " + fileName);
            Logger.error(throwable);
        }

        return null;
    }

    public @NotNull Set<Class<?>> getTypesAnnotatedWith(@NotNull Class<? extends Annotation> annotation) {
        Set<Class<?>> classes = new HashSet<>();

        for (Class<?> clazz : getClasses()) {
            if (clazz.getDeclaredAnnotation(annotation) != null) {
                classes.add(clazz);
            }
        }

        return classes;
    }

    public @NotNull Set<Method> getMethodsAnnotatedWith(@NotNull Class<? extends Annotation> annotation) {
        Set<Method> methods = new HashSet<>();

        for (Class<?> clazz : getClasses()) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getDeclaredAnnotation(annotation) != null) {
                    method.setAccessible(true);
                    methods.add(method);
                }
            }
        }

        return methods;
    }

    public @NotNull <T> Set<Class<? extends T>> getOfType(@NotNull Class<T> clazz) {
        Set<Class<? extends T>> classes = new HashSet<>();

        for (Class<?> aClass : getClasses()) {
            if (clazz.isAssignableFrom(aClass)) {
                //noinspection unchecked
                classes.add((Class<? extends T>) aClass);
            }
        }

        return classes;
    }

    @SuppressWarnings("unused")
    public static Method getCallingMethod(int depth) {
        StackWalker.StackFrame stackFrame = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(stream -> stream
                        .skip(depth)
                        .findFirst()
                        .orElse(null)
                );

        if (stackFrame == null) {
            throw new RuntimeException("StackFrame is null");
        }

        Class<?>[] parameterTypes = stackFrame.getMethodType().parameterArray();
        Class<?> clazz = stackFrame.getDeclaringClass();
        String methodName = stackFrame.getMethodName();

        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException error) {
            Logger.error(error);
        }

        return null;
    }

}
