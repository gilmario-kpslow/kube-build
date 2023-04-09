package br.com.gilmariosoftware.namespace;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author gilmario
 */
@Path("/namespace")
public class NamespaceResource {

    private final KubernetesClient kubernetesClient;

    public NamespaceResource(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @GET
    public List<Namespace> list(@PathParam("namespace") String namespace) {
        return kubernetesClient.namespaces().list().getItems();
    }

}
