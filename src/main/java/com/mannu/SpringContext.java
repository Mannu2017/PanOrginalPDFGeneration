package com.mannu;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
public class SpringContext {

	@Bean(name = "mainFrame")
    public MainFrame createMainFrame() {
        return new MainFrame();
    }
 
 @Bean
    public static PropertySourcesPlaceholderConfigurer setUp() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
