
package com.github.nexus.dao;

import java.util.HashMap;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

@Configuration
@ComponentScan(basePackages = "com.github.nexus.dao")
public class JpaConfig {

     @Bean
    public SomeDAO someDAO() {
        return new SomeDAOImpl();
    }
    
    @Bean
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
    
    
    @Bean
    public DataSource dataSource() {
       return  new EmbeddedDatabaseBuilder()
               .setType(EmbeddedDatabaseType.H2)
               .build();

    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaDialect(new org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect());
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(new EclipseLinkJpaVendorAdapter());
        localContainerEntityManagerFactoryBean.setJpaPropertyMap(new HashMap<String, String>() {{
            put("eclipselink.weaving", "false");
             put("javax.persistence.schema-generation.database.action", "create");
        }});
        
        /*
                <property name="jpaVendorAdapter">
            <bean class="org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter">
                <property name="showSql" value="true"/>
                <property name="generateDdl" value="true"/>
                <property name="databasePlatform" value="org.eclipse.persistence.platform.database.H2Platform"/>
            </bean>
        </property>
        */
        
        return localContainerEntityManagerFactoryBean;
        
    }
    
}