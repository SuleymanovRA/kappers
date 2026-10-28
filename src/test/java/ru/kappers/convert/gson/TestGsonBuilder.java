package ru.kappers.convert.gson;

import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class TestGsonBuilder {
    public GsonBuilder defaultGsonBuilder() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
    }
}
