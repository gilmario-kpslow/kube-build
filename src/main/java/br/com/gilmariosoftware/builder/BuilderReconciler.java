package br.com.gilmariosoftware.builder;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodCondition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.quarkus.logging.Log;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 *
 * @author gilmario
 */
@ControllerConfiguration(namespaces = "default", name = "builder")
public class BuilderReconciler implements Reconciler<BuilderResource> {

    public static final Duration INTERVALO_TENTATIVA = Duration.ofSeconds(5);
    private static final String APP_LABEL = "app.kubernetes.io/name";

    @Inject
    private KubernetesClient kubernetesClient;

    @Override
    public UpdateControl<BuilderResource> reconcile(BuilderResource resource, Context<BuilderResource> cntxt) throws Exception {

        final ObjectMeta metadata = getMetadataPadrao(resource);

        final Pod build = criaBuild(resource, metadata);
        if (Objects.isNull(build)) {
            return novaTentativaReconciliacao("Build", metadata);
        }

        Log.infof("Build concluido %s criada e exposta com sucesso em %s", metadata.getName(), resource.getSpec().getImagem());
        resource.setStatus(new BuilderStatus("Finalizado"));
        return UpdateControl.updateStatus(resource);
    }

    private UpdateControl<BuilderResource> novaTentativaReconciliacao(String recurso, ObjectMeta metadata) {
        Log.infof("O %s da resource %s ainda não está pronto. Reagendando nova tentativa de reconciliacao após {}s",
                recurso, metadata.getName(), INTERVALO_TENTATIVA.toSeconds());
        return UpdateControl.<BuilderResource>noUpdate().rescheduleAfter(INTERVALO_TENTATIVA);
    }

    private ObjectMeta getMetadataPadrao(BuilderResource resource) {
        final String nome = resource.getMetadata().getName();
        final String namespace = resource.getMetadata().getNamespace();
        Map<String, String> labels = Map.of(APP_LABEL, nome);
        ObjectMeta metadata = new ObjectMetaBuilder()
                .withName(nome)
                .withNamespace(namespace)
                .withLabels(labels)
                .build();
        return metadata;
    }

    private Pod criaBuild(BuilderResource resource, ObjectMeta metadata) {
        Pod build = kubernetesClient.pods()
                .inNamespace(metadata.getNamespace())
                .withName(metadata.getName()).get();

        if (Objects.isNull(build)) {
            List<EnvVar> envVars = resource.getSpec().getAdditionalProperties()
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        var envVar = new EnvVar();
                        envVar.setName(entry.getKey());
                        envVar.setValue(entry.getValue());
                        return envVar;
                    })
                    .collect(Collectors.toList());

            List<String> args = resource.getSpec().getBuildProperties().entrySet().stream().map(entry -> {
                return new StringBuilder().append("--").append(entry.getKey()).append("=").append(entry.getValue()).toString();
            }).toList();

            var novoBuild = new PodBuilder()
                    .withMetadata(metadata)
                    .withNewSpec()
                    .addNewContainer()
                    .withName("builder")
                    .withImage("gcr.io/kaniko-project/executor:latest")
                    .withArgs(args)
                    .addAllToEnv(envVars)
                    .endContainer()
                    .withRestartPolicy("Never")
                    .endSpec()
                    .build();

            build = kubernetesClient.pods().resource(novoBuild).create();
        }

        System.out.println(build.getStatus().getConditions());

        Predicate<PodCondition> condicaoDeployCompleto = cond
                -> cond.getType().equals("Initialized")
                && cond.getStatus().equals("True")
                && Objects.nonNull(cond.getReason()) && cond.getReason().equals("PodCompleted");
        if (build.getStatus().getConditions().stream().anyMatch(condicaoDeployCompleto)) {
            return build;
        }
        return null;
    }

}
