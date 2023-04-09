package br.com.gilmariosoftware.builder;

import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;

/**
 *
 * @author gilmario
 */
//@Singleton
//@Startup
public class BuilderWatch {

    private final KubernetesClient client;

    public BuilderWatch(KubernetesClient kubernetesClient) {
        this.client = kubernetesClient;
    }

//    @PostConstruct
    public void teste() {

        client.apiextensions().v1().customResourceDefinitions().watch(new Watcher<CustomResourceDefinition>() {
            @Override
            public void eventReceived(Watcher.Action action, CustomResourceDefinition resource) {

            }

            @Override
            public void onClose(WatcherException cause) {

            }
        });

        client.apiextensions().v1().customResourceDefinitions();
    }
}
