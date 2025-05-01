package ru.tbank.itis.tripbackend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.tbank.itis.tripbackend.exception.JsonProcessingException;

import java.io.IOException;
import java.io.Reader;


public class JsonUtil {
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules();

    public static <T> T read(Reader reader, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(reader, clazz);
        } catch (IOException e) {
            throw new JsonProcessingException("Ошибка при чтении JSON", e);
        }
    }

    public static String write(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            throw new JsonProcessingException("Ошибка при сериализации объекта в JSON", e);
        }
    }
}
