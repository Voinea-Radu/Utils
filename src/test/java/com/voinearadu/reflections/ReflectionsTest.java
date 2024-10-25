package com.voinearadu.reflections;

import com.voinearadu.reflections.annotation.TestAnnotation;
import com.voinearadu.reflections.dto.TestChild;
import com.voinearadu.reflections.dto.TestParent;
import com.voinearadu.utils.reflections.Reflections;
import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ReflectionsTest {

    private static Reflections reflections;

    @BeforeAll
    public static void init() {
        BasicConfigurator.configure();

        ReflectionsTest.reflections = new Reflections(new ArrayList<>(), new ArrayList<>(), ReflectionsTest.class.getClassLoader(), "com.voinearadu.reflections");
    }

    @Test
    public void testTypeAnnotationProcessing() {
        int typesCount = reflections.getTypesAnnotatedWith(TestAnnotation.class).size();

        assertEquals(1, typesCount);
    }

    @Test
    public void testMethodAnnotationProcessing() {
        int typesCount = reflections.getMethodsAnnotatedWith(TestAnnotation.class).size();

        assertEquals(1, typesCount);
    }

    @Test
    public void testGetCallingMethod() {
        Method method = Reflections.getCallingMethod(1);

        assertNotNull(method);
        assertEquals("testGetCallingMethod", method.getName());
    }


    @Test
    public void testTypeChildrenProcessing() {
        int typesCount = reflections.getOfType(TestParent.class).size();

        assertEquals(2, typesCount);
    }

    @Test
    public void testGetField() {
        int childFieldsCount = Reflections.getFields(TestChild.class).size();
        Field publicChildFiled = Reflections.getField(TestChild.class, "publicChildFiled");
        Field protectedChildField = Reflections.getField(TestChild.class, "protectedChildField");
        Field privateChildField = Reflections.getField(TestChild.class, "privateChildField");

        int parentFieldsCount = Reflections.getFields(TestParent.class).size();
        Field publicParentField = Reflections.getField(TestParent.class, "publicParentField");
        Field protectedParentField = Reflections.getField(TestParent.class, "protectedParentField");
        Field privateParentField = Reflections.getField(TestParent.class, "privateParentField");

        assertEquals(6, childFieldsCount);
        assertNotNull(publicChildFiled);
        assertNotNull(protectedChildField);
        assertNotNull(privateChildField);

        assertEquals(3, parentFieldsCount);
        assertNotNull(publicParentField);
        assertNotNull(protectedParentField);
        assertNotNull(privateParentField);
    }

}
