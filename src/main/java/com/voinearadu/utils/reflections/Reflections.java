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

    private final ClassLoader classLoader;
    private final Set<Class<?>> classes = new HashSet<>();

    public Reflections(@NotNull ClassLoader classLoader) {
        this.classLoader = classLoader;
        String[] classPathEntries = System.getProperty("java.class.path").split(File.pathSeparator);
        for (String classPathEntry : classPathEntries) {
            if (classPathEntry.endsWith(".jar") || classPathEntry.endsWith(".zip")) {
                registerZip(new File(classPathEntry));
            } else {
                registerDirectory(new File(classPathEntry));
            }
        }
    }

    public static @NotNull Reflections.Crawler simple(ClassLoader classLoader, Class<?>... classes) {
        return simple(classLoader, Arrays.asList(classes));
    }

    public static @NotNull Reflections.Crawler simple(ClassLoader classLoader, Collection<Class<?>> classes) {
        Reflections reflections = new Reflections(classLoader);
        reflections.classes.addAll(classes);
        return reflections.from();
    }

    public Reflections.Crawler from(String... searchDomain) {
        return new Reflections.Crawler(this, searchDomain);
    }

    @SuppressWarnings("unused")
    public Reflections registerZip(Collection<File> zipFile) {
        for (File file : zipFile) {
            registerZip(file);
        }

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Reflections registerZip(File zipFile) {
        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry == null || entry.isDirectory()) {
                    continue;
                }

                processFile(entry.getName());
            }
        } catch (Exception error) {
            Logger.error(error);
        }

        return this;
    }

    @SuppressWarnings("unused")
    public Reflections registerDirectories(Collection<File> directories) {
        for (File directory : directories) {
            registerDirectory(directory);
        }

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Reflections registerDirectory(File directory) {
        try {
            for (File file : FileUtils.listFiles(directory, new String[]{"class"}, true)) {
                processFile(file.getAbsolutePath().replace(directory.getAbsolutePath() + File.separator, ""));
            }
        } catch (Exception error) {
            Logger.error(error);
        }

        return this;
    }

    @SuppressWarnings("unused")
    public Reflections registerClasses(@NotNull Class<?>... classes) {
        return this.registerClasses(Arrays.asList(classes));
    }

    public Reflections registerClasses(@NotNull Collection<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            this.registerClass(clazz);
        }

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Reflections registerClass(@NotNull Class<?> clazz) {
        this.classes.add(clazz);
        return this;
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

    public static @NotNull Set<Field> getFields(@NotNull Class<?> clazz) {
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

        return output;
    }

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

    private void processFile(String fileName) {
        if (!fileName.endsWith(".class")) {
            return;
        }

        fileName = fileName.replace("/", ".");
        fileName = fileName.replace("\\", ".");
        fileName = fileName.replace(".class", "");

        String simpleClassName = fileName.substring(fileName.lastIndexOf('.') + 1);

        // Skip Mixin classes
        if (simpleClassName.contains("Mixin") || simpleClassName.contains("module-info")) {
            return;
        }

        try {
            this.classes.add(classLoader.loadClass(fileName));
        } catch (Throwable throwable) {
            // Logger.error("Failed to load class " + fileName);
            // Logger.error(throwable);
        }
    }

    @Getter
    public static class Crawler {
        private final Reflections reflections;
        private final Set<String> searchDomain = new HashSet<>();

        public Crawler(Reflections reflections, String... searchDomain) {
            this.reflections = reflections;

            if (searchDomain.length == 0) {
                this.searchDomain.add("");
            }

            for (String domain : searchDomain) {
                if (!domain.endsWith(".") && !domain.isEmpty()) {
                    domain += ".";
                }

                this.searchDomain.add(domain);
            }
        }

        public @NotNull Set<Class<?>> getClasses() {
            Set<Class<?>> classes = new HashSet<>();

            for (Class<?> clazz : reflections.getClasses()) {
                for (String domain : searchDomain) {
                    String packageName = clazz.getPackageName();

                    if (packageName.startsWith(domain)) {
                        classes.add(clazz);
                        break;
                    }
                }
            }

            return classes;
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

    }

}
