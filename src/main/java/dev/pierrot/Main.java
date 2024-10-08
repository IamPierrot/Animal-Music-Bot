package dev.pierrot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mongodb.client.MongoClient;
import dev.pierrot.Database.MongoDB;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main extends Utils {
    public static Configuration config;
    public static MongoClient mongoClient ;

    public static void main(String[] args) throws InterruptedException {
        String configPath = isRunningFromJar() ? "config.yml" : "src/main/resources/config.yml";

        File file = new File(configPath);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            config = objectMapper.readValue(file, Configuration.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        App.appInitialize();
        mongoClient = new MongoDB().mongoClient;
    }

    public static class Configuration {
        public Configuration() {}

        private AppConfig app;

        public AppConfig getApp() {
            return app;
        }

        public static class AppConfig {
            public String prefix;
            public String TOKEN;
            public boolean global;
            public int port;
            public String MONGO_URI;
        }

    }
    private static boolean isRunningFromJar() {
        URL resource = Main.class.getResource(Main.class.getSimpleName() + ".class");
        return resource != null && resource.getProtocol().equals("jar");
    }
}