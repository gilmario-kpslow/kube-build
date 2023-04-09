package br.com.gilmariosoftware.builder;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author gilmario
 */
@Version("v1")
@Group("gilmariosoftware.io")
@Getter
@Setter
public class BuilderResource extends CustomResource<BuilderSpec, BuilderStatus> implements Namespaced {

    private String nome;

    @Override
    protected BuilderSpec initSpec() {
        return new BuilderSpec();
    }

    @Override
    protected BuilderStatus initStatus() {
        return new BuilderStatus();
    }

}
