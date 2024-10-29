package com.example.moyeothon.Config.Swagger;


import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }
    private Info apiInfo() {
        return new Info()
                .title("\uD83E\uDD81 멋사 모여톤 10팀 스웨거 \uD83D\uDC8E")
                .description("모여톤 10팀의 스웨거입니다.")
                .version("1.0.0");
    }
}
