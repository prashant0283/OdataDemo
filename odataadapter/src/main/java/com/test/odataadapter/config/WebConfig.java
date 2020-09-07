package com.test.odataadapter.config;

import com.test.odataadapter.controller.CarsServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServlet;

@Configuration
public class WebConfig {
    @Bean
    public ServletRegistrationBean<HttpServlet> countryServlet() {
        ServletRegistrationBean<HttpServlet> servRegBean = new ServletRegistrationBean<>();
        servRegBean.setServlet(new CarsServlet());
        servRegBean.addUrlMappings("/cars.svc/*");
        servRegBean.setLoadOnStartup(1);
        return servRegBean;
    }
}
