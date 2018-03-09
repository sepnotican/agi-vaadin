package com.sepnotican.springjpaformautocreator;

import com.sepnotican.springjpaformautocreator.generator.form.element.AbstractElementForm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class AppConfig {

    @Bean
    <T> AbstractElementForm<T> abstractElementForm() {
        return new AbstractElementForm<T>();
    }

}
