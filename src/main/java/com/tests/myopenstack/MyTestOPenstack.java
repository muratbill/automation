package com.tests.myopenstack;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.exceptions.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class MyTestOPenstack {
    private static final Logger logger = LoggerFactory.getLogger(MyTestOPenstack.class);

    public static void main(String[] args) {
        Properties properties = ConfigLoader.loadProperties("config.properties");
        if (properties == null) {
            logger.error("Failed to load configuration properties.");
            return;
        }
        String authUrl = properties.getProperty("authUrl");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        String domain = properties.getProperty("domain");
        String project = properties.getProperty("project");
        String templateFilePath = properties.getProperty("templateFilePath");
        String mynetwork = properties.getProperty("mynetwork");
        String myimage = properties.getProperty("myimage");
        String myinstancename = properties.getProperty("myinstancename");

        String heatTemplate = HeatTemplateHandler.readFileAsString(templateFilePath);

        try {
            // Authenticate with OpenStack
            OSClientV3 os = OpenStackAuth.authenticate(authUrl, username, password, domain, project);


            Map<String, String> parameters = new HashMap<>();
            parameters.put("network", mynetwork);
            parameters.put("image", myimage);
            parameters.put("instance_name", myinstancename);

            // Create the stack
            HeatTemplateHandler.createStack(os, heatTemplate, parameters);
            logger.info("Heat stack creation initiated.");


            // Poll the instance status
            boolean isInstanceUp = HeatTemplateHandler.pollInstanceStatus(os, myinstancename);
            if (isInstanceUp) {
                logger.info("Instance is up and running.");

            } else {
                logger.error("Instance failed to start.");
            }

        } catch (ResponseException e) {
            logger.error(STR."Error: \{e.getMessage()}", e);
        }

    }


}


