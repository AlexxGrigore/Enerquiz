package server.utils;

import com.google.gson.Gson;
import commons.GsonConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Gson.class)
public class GsonBean {

    /**
     * Bean method so Spring Boot uses a Gson instance with our config.
     *
     * @return The configured Gson instance
     */
    @Bean
    public Gson gson() {
        return new GsonConfig().getGson();
    }

}
