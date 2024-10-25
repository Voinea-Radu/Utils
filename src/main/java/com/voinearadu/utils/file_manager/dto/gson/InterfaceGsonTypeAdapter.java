package com.voinearadu.utils.file_manager.dto.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.voinearadu.utils.file_manager.dto.serializable.ISerializable;
import com.voinearadu.utils.logger.Logger;
import com.voinearadu.utils.reflections.Reflections;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


@SuppressWarnings("unchecked")
public class InterfaceGsonTypeAdapter<T extends ISerializable> implements JsonDeserializer<T> {

    public static final String CLASS_NAME = "class_name";

    protected final ClassLoader classLoader;
    private final @Getter Class<T> serializedClass;

    public InterfaceGsonTypeAdapter(ClassLoader classLoader, Class<T> serializedClass) {
        this.serializedClass = serializedClass;
        this.classLoader = classLoader;
    }

    @SuppressWarnings("unchecked")
    public static List<InterfaceGsonTypeAdapter<? extends ISerializable>> generate(ClassLoader classLoader, Class<? extends ISerializable>... classes) {
        List<InterfaceGsonTypeAdapter<? extends ISerializable>> output = new ArrayList<>();

        Arrays.stream(classes)
                .filter(Class::isInterface)
                .forEach(clazz -> output.add(new InterfaceGsonTypeAdapter<>(classLoader, clazz)));

        return output;
    }


    @SuppressWarnings({"unchecked", "unused"})
    public static List<InterfaceGsonTypeAdapter<? extends ISerializable>> generate(ClassLoader classLoader, Reflections reflections) {
        Set<Class<? extends ISerializable>> classes = reflections.getOfType(ISerializable.class);

        classes.stream()
                .filter(clazz -> !clazz.isInterface())
                .forEach(clazz -> {
                    boolean flag = Reflections.getFields(clazz).stream()
                            .anyMatch(field -> field.getName().equals(CLASS_NAME) && field.getType().equals(String.class));

                    if (!flag) {
                        Logger.error("Class " + clazz.getName() + " implements ISerializable but does not have a field `class_name`. This can cause issues with deserialization of this class.");
                    }
                });


        return generate(classLoader, classes.toArray(new Class[0]));
    }

    @SneakyThrows(value = {ClassNotFoundException.class})
    @Override
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        ISerializable output;

        JsonElement classElement = json.getAsJsonObject().get(CLASS_NAME);

        if (classElement == null) {
            Logger.error("Field class_name was not not found in json. Please check the definition of " + type.getTypeName() + " and make sure it has the field class_name.");
            Logger.error("Json:");
            Logger.error(json.toString());
            return null;
        }

        String className = json.getAsJsonObject().get(CLASS_NAME).getAsString();

        //noinspection unchecked
        Class<ISerializable> clazz = (Class<ISerializable>) classLoader.loadClass(className);

        output = context.deserialize(json, clazz);

        return (T) output;
    }

}
