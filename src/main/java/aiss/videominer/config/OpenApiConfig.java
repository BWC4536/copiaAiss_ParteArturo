package aiss.videominer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("VideoMiner API")
                .version("1.0.0")
                .description("API REST para minería de datos de vídeos en PeerTube y Dailymotion. " +
                             "Microservicio central que almacena y sirve datos de canales, vídeos, " +
                             "subtítulos, comentarios y usuarios.")
                .contact(new Contact()
                    .name("VideoMiner Team")
                    .email("videominer@example.com")
                    .url("https://github.com/"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .addServersItem(new Server()
                .url("http://localhost:8080")
                .description("Local development environment"));
    }

}
