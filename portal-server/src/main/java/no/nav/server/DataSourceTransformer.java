package no.nav.server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DataSourceTransformer {
    public static DataSource create(Map<String, String> props1) {
        Map<String, String> props = new HashMap<>();
        props.put("jdbcUrl", "jdbc:postgresql://127.0.0.1:5432/flywaytest");
        props.put("username", "postgres");
        //Lokalt passord
        props.put("password", "system");
        //props.put("password", System.getenv("key1"));
        System.out.println(System.getenv("key1"));
        props.put("driverClassName", "org.postgresql.Driver");
        props.put("maximumPoolSize","32");
        Properties properties = new Properties();
        props.forEach(properties::put);
        HikariDataSource dataSource = new HikariDataSource(new HikariConfig(properties));
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }



}
