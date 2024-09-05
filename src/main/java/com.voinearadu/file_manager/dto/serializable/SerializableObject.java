package com.voinearadu.file_manager.dto.serializable;

public record SerializableObject<Object>(Class<Object> objectClass, Object object) {

}
