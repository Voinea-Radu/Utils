package com.voinearadu.file_manager.dto.gson;

import com.google.gson.*;
import com.voinearadu.file_manager.dto.GsonSerializer;
import com.voinearadu.file_manager.dto.serializable.SerializableList;
import com.voinearadu.logger.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;


@SuppressWarnings("rawtypes")
public class SerializableListGsonTypeAdapter extends GsonSerializer<SerializableList> {

    private static final String CLASS_NAME = "class_name";
    private static final String VALUES = "values";

    public SerializableListGsonTypeAdapter(ClassLoader classLoader) {
        super(classLoader, SerializableList.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SerializableList deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
        String className = json.getAsJsonObject().get(CLASS_NAME).getAsString();

        try {
            Class clazz = classLoader.loadClass(className);

            JsonArray data = json.getAsJsonObject().get(VALUES).getAsJsonArray();
            java.util.List output = new ArrayList();
            data.forEach((element) -> output.add(context.deserialize(element, clazz)));

            return new SerializableList(clazz, output);
        } catch (ClassNotFoundException error) {
            Logger.error(error);
            return null; //NOPMD - suppressed ReturnEmptyCollectionRatherThanNull - If the class of the list can not be
            // found, no list can be created
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public JsonElement serialize(SerializableList list, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty(CLASS_NAME, list.getValueClass().getName());

        JsonArray array = new JsonArray();
        list.forEach((element) -> array.add(context.serialize(element)));

        object.add(VALUES, array);

        return object;
    }
}
