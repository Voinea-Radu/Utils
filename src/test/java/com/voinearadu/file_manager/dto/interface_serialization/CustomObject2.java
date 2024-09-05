package com.voinearadu.file_manager.dto.interface_serialization;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomObject2 implements CustomInterface {

    @SuppressWarnings("unused")
    private final String class_name = CustomObject2.class.getName();
    public int data;

}
