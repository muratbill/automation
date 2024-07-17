package com.tests.myopenstack;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;


public class OpenStackAuth {
    public static OSClientV3 authenticate(String authUrl, String username, String password, String domain, String project) {
        return OSFactory.builderV3()
                .endpoint(authUrl)
                .credentials(username, password, Identifier.byName(domain))
                .scopeToProject(Identifier.byName(project), Identifier.byName(domain))
                .authenticate();
    }
}
