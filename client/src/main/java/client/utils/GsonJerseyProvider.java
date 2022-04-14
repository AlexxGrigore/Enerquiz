package client.utils;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import commons.GsonConfig;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonJerseyProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

    private Gson gson = new GsonConfig().getGson();

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {

        InputStreamReader reader = new InputStreamReader(entityStream, "UTF-8");
        String jsonString = CharStreams.toString(reader);

        Object json;
        try {
            json = gson.fromJson(jsonString, genericType);
        } catch (JsonParseException e) {
            System.err.println("Failed to parse JSON response: " + jsonString);
            throw e;
        } finally {
            reader.close();
        }

        reader.close();
        return json;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public void writeTo(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {

        OutputStreamWriter writer = new OutputStreamWriter(entityStream, "UTF-8");

        gson.toJson(o, genericType, writer);

        writer.close();
    }
}
