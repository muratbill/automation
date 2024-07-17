package com.tests.myopenstack;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.heat.StackCreate;
import org.openstack4j.openstack.heat.domain.HeatStackCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;


public class HeatTemplateHandler {
    private static final Logger logger = LoggerFactory.getLogger(HeatTemplateHandler.class);
    public static String readFileAsString(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(STR."Error reading file: \{filePath}", e);
        }
    }

    public static void createStack(OSClientV3 os, String template, Map<String, String> parameters) {
        StackCreate stackCreate = HeatStackCreate.build()
                .name("my_heat_stack")
                .template(template)
                .parameters(parameters)
                .build();
        os.heat().stacks().create(stackCreate);
    }
    protected static boolean pollInstanceStatus(OSClientV3 os, String instanceName) {
        int maxRetries = 40;
        int retryInterval = 10000; // 10 seconds

        for (int i = 0; i < maxRetries; i++) {
            try {
                Server server = os.compute().servers().list().stream()
                        .filter(s -> instanceName.equals(s.getName()))
                        .findFirst()
                        .orElse(null);

                if (server != null && "ACTIVE".equals(server.getStatus().toString())) {
                    // Optionally, check network accessibility
                    logger.info(STR."Addresses: \{server.getAddresses()}");
                    return true;
                }

                Thread.sleep(retryInterval);
            } catch (InterruptedException e) {
                logger.error("Interrupted while waiting for instance to become active.", e);
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }


}
