package com.voinearadu.file_manager.dto.interface_serialization;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomObject1 implements CustomInterface {

    @SuppressWarnings("unused")
    private final String class_name = CustomObject1.class.getName();
    public String data;

}
