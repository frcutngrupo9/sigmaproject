/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ar.edu.utn.sigmaproject.web;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import ar.edu.utn.sigmaproject.zk.spring.annotations.ZkSpringConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ar.edu.utn.sigmaproject.service.impl.SearchableRepositoryImpl;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.transaction.aspectj.AnnotationTransactionAspect;

@Configuration
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EnableJpaRepositories(value = "ar.edu.utn.sigmaproject.service", repositoryBaseClass = SearchableRepositoryImpl.class)
@ComponentScan(value = { "ar.edu.utn.sigmaproject.controller", "ar.edu.utn.sigmaproject.service", "ar.edu.utn.sigmaproject.service.impl", "ar.edu.utn.sigmaproject.util"})
@PropertySource("classpath:db.properties")
@ZkSpringConfig
public class ApplicationConfig {
	
	@Value("${db.url}")
	private String dbUrl;
	
	@Value("${db.user}")
	private String dbUser;
	
	@Value("${db.password}")
	private String dbPassword;
	
	@Value("${hibernate.hbm2ddl.auto}")
	private String hbm2ddl;

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] { "ar.edu.utn.sigmaproject.domain" });

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());

		return em;
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl(this.dbUrl);
		dataSource.setUsername(this.dbUser);
		dataSource.setPassword(this.dbPassword);
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		AnnotationTransactionAspect.aspectOf().setTransactionManager(transactionManager);
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.HBM2DDL_AUTO, this.hbm2ddl);
		properties.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQL5InnoDBDialect");
		properties.setProperty(AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS, "true");
		return properties;
	}
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
	    return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
