package com.sepnotican.springjpaformautocreator;

import com.sepnotican.springjpaformautocreator.generator.form.AbstractForm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class AppConfig {

    @Bean
    <T> AbstractForm<T> abstractForm() {
        return new AbstractForm<T>();
    }

}
