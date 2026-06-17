package com.scplatform.pcm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.artemis.autoconfigure.ArtemisAutoConfiguration;
import org.springframework.boot.batch.autoconfigure.BatchAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.thymeleaf.autoconfigure.ThymeleafAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication(exclude={ThymeleafAutoConfiguration.class,ArtemisAutoConfiguration.class,BatchAutoConfiguration.class})
@ComponentScan(basePackages={"com.scplatform"})
@EnableJpaRepositories(basePackages={"com.scplatform"})
@EnableScheduling
public class PcmApplication extends SpringBootServletInitializer {
    @Override protected SpringApplicationBuilder configure(SpringApplicationBuilder b){return b.sources(PcmApplication.class);}
    public static void main(String[] args){SpringApplication.run(PcmApplication.class,args);}
}