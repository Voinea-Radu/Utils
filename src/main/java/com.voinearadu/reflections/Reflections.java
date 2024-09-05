package com.voinearadu.reflections;

import com.voinearadu.logger.Logger;
import lombok.Getter;
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
    private final ClassLoader classLoader;
    private final List<String> searchDomain;

    public Reflections(List<File> zipFiles, ClassLoader classLoader, String... searchDomain) {
        this.zipFiles = zipFiles;
        this.classLoader = classLoader;
        this.searchDomain = Arrays.stream(searchDomain).map(domain -> {
            if (!domain.endsWith(".")) {
                return domain + ".";
            }
            return domain;
        }).toList();
    }

    @SuppressWarnings("unused")
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
        return new Reflections(zipFiles, classLoader, searchDomain);
    }

    public @NotNull Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        for (File zipFile : zipFiles) {
            classes.addAll(getClasses(zipFile));
        }

        return classes;
    }

    private @NotNull Set<Class<?>> getClasses(File zipFile) {
        Set<Class<?>> classes = new HashSet<>();

        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry == null)
                    break;

                if (entry.isDirectory()) {
                    continue;
                }

                String name = entry.getName();

                if (!name.endsWith(".class")) {
                    continue;
                }

                name = name.replace("/", ".");
                name = name.replace(".class", "");

                if (searchDomain.stream().noneMatch(name::startsWith)) {
                    continue;
                }

                String simpleClassName = name.substring(name.lastIndexOf('.') + 1);

                // Skip Mixin classes
                if (simpleClassName.contains("Mixin")) {
                    continue;
                }


                try {
                    classes.add(classLoader.loadClass(name));
                } catch (Throwable throwable) {
                    Logger.error("Failed to load class " + name + " from " + zipFile.getName());
                    Logger.error(throwable);
                }
            }
        } catch (Exception error) {
            Logger.error(error);
        }

        return classes;
    }

    @SuppressWarnings("unused")
    public @NotNull Set<Class<?>> getTypesAnnotatedWith(@NotNull Class<? extends Annotation> annotation) {
        Set<Class<?>> classes = new HashSet<>();

        for (Class<?> clazz : getClasses()) {
            if (clazz.getDeclaredAnnotation(annotation) != null) {
                classes.add(clazz);
            }
        }

        return classes;
    }

    @SuppressWarnings("unused")
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
