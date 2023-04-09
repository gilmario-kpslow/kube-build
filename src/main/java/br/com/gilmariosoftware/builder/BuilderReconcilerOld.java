//package br.com.gilmariosoftware.builder;
//
//import io.fabric8.kubernetes.api.model.EnvVar;
//import io.fabric8.kubernetes.api.model.LoadBalancerIngress;
//import io.fabric8.kubernetes.api.model.ObjectMeta;
//import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
//import io.fabric8.kubernetes.api.model.Service;
//import io.fabric8.kubernetes.api.model.ServiceBuilder;
//import io.fabric8.kubernetes.api.model.apps.Deployment;
//import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
//import io.fabric8.kubernetes.api.model.apps.DeploymentCondition;
//import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
//import io.fabric8.kubernetes.api.model.networking.v1.IngressBuilder;
//import io.fabric8.kubernetes.api.model.networking.v1.IngressSpec;
//import io.fabric8.kubernetes.api.model.networking.v1.IngressStatus;
//import io.fabric8.kubernetes.client.KubernetesClient;
//import io.javaoperatorsdk.operator.api.reconciler.Context;
//import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
//import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
//import io.quarkus.logging.Log;
//import java.time.Duration;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//import javax.inject.Inject;
//
///**
// *
// * @author gilmario
// */
////@ControllerConfiguration(namespaces = "default", name = "builder")
//public class BuilderReconcilerOld implements Reconciler<BuilderResource> {
//
//    public static final Duration INTERVALO_TENTATIVA = Duration.ofSeconds(3);
//    private static final String APP_LABEL = "app.kubernetes.io/name";
//
//    @Inject
//    private KubernetesClient kubernetesClient;
//
//    @Override
//    public UpdateControl<BuilderResource> reconcile(BuilderResource resource, Context<BuilderResource> cntxt) throws Exception {
//
//        final ObjectMeta metadata = getMetadataPadrao(resource);
//
//        final Deployment deployment = criaDeployment(resource, metadata);
//        if (deployment == null) {
//            return novaTentativaReconciliacao("deployment", metadata);
//        }
//
//        final Service service = criaService(resource, metadata);
//        if (service == null) {
//            return novaTentativaReconciliacao("service", metadata);
//        }
//
//        final Ingress ingress = criaIngress(resource, metadata);
//        if (ingress == null) {
//            return novaTentativaReconciliacao("ingress", metadata);
//        }
//
//        final String url = extraiURL(ingress);
//
//        Log.infof("Aplicacao %s criada e exposta com sucesso em %s", metadata.getName(), url);
//        resource.setStatus(new BuilderStatus(url));
//        return UpdateControl.updateStatus(resource);
//    }
//
//    private UpdateControl<BuilderResource> novaTentativaReconciliacao(String recurso, ObjectMeta metadata) {
//        Log.infof("O %s da resource %s ainda não está pronto. Reagendando nova tentativa de reconciliacao após {}s",
//                recurso, metadata.getName(), INTERVALO_TENTATIVA.toSeconds());
//        return UpdateControl.<BuilderResource>noUpdate().rescheduleAfter(INTERVALO_TENTATIVA);
//    }
//
//    private ObjectMeta getMetadataPadrao(BuilderResource resource) {
//        final String nome = resource.getMetadata().getName();
//        final String namespace = resource.getMetadata().getNamespace();
//        Map<String, String> labels = Map.of(APP_LABEL, nome);
//        ObjectMeta metadata = new ObjectMetaBuilder()
//                .withName(nome)
//                .withNamespace(namespace)
//                .withLabels(labels)
//                .build();
//        return metadata;
//    }
//
//    private Deployment criaDeployment(BuilderResource resource, ObjectMeta metadata) {
//        Deployment deployAtual = kubernetesClient.apps().deployments()
//                .inNamespace(metadata.getNamespace())
//                .withName(metadata.getName())
//                .get();
//
//        if (deployAtual == null) {
//            List<EnvVar> envVars = resource.getSpec().getAdditionalProperties()
//                    .entrySet()
//                    .stream()
//                    .map(entry -> {
//                        var envVar = new EnvVar();
//                        envVar.setName(entry.getKey());
//                        envVar.setValue(entry.getValue());
//                        return envVar;
//                    })
//                    .collect(Collectors.toList());
//
//            var novoDeploy = new DeploymentBuilder()
//                    .withMetadata(metadata)
//                    .withNewSpec()
//                    .withNewSelector().withMatchLabels(metadata.getLabels()).endSelector()
//                    .withNewTemplate()
//                    .withNewMetadata().withLabels(metadata.getLabels())
//                    .endMetadata()
//                    .withNewSpec()
//                    .addNewContainer()
//                    .withName(resource.getMetadata().getName())
//                    .withImage(resource.getSpec().getImagem())
//                    .addAllToEnv(envVars)
//                    .addNewPort()
//                    .withName("http")
//                    .withProtocol("TCP")
//                    .withContainerPort(80)
//                    .endPort()
//                    .endContainer()
//                    .endSpec()
//                    .endTemplate()
//                    .endSpec()
//                    .build();
//
//            deployAtual = kubernetesClient.apps().deployments().resource(novoDeploy).create();
//        }
//
//        Predicate<DeploymentCondition> condicaoDeployCompleto = cond
//                -> cond.getType().equals("Progressing")
//                && cond.getStatus().equals("True")
//                && cond.getReason().equals("NewReplicaSetAvailable");
//        if (deployAtual.getStatus().getConditions().stream().anyMatch(condicaoDeployCompleto)) {
//            return deployAtual;
//        }
//        return null;
//    }
//
//    private Service criaService(BuilderResource resource, ObjectMeta metadata) {
//        Service serviceAtual = kubernetesClient.services()
//                .inNamespace(metadata.getNamespace())
//                .withName(metadata.getName())
//                .get();
//
//        if (serviceAtual == null) {
//            var novoService = new ServiceBuilder()
//                    .withMetadata(metadata)
//                    .withNewSpec()
//                    .addNewPort()
//                    .withName("http")
//                    .withPort(80)
//                    .withNewTargetPort().withValue(80).endTargetPort()
//                    .endPort()
//                    .withSelector(metadata.getLabels())
//                    .withType("ClusterIP")
//                    .endSpec()
//                    .build();
//
//            serviceAtual = kubernetesClient.services().resource(novoService).create();
//        }
//
//        if (serviceAtual != null) {
//            return serviceAtual;
//        }
//        return null;
//    }
//
//    private Ingress criaIngress(BuilderResource resource, ObjectMeta metadata) {
//        Ingress ingressAtual = kubernetesClient.resources(Ingress.class)
//                .inNamespace(metadata.getNamespace())
//                .withName(metadata.getName())
//                .get();
//
//        IngressSpec spec = new IngressSpec();
//
//        if (ingressAtual == null) {
//            var novoIngress = new IngressBuilder()
//                    .withMetadata(metadata)
//                    .withNewSpec()
//                    .addNewRule()
//                    .withHost("gilmario.truesystem.com.br")
//                    .withNewHttp()
//                    .addNewPath()
//                    .withPath("/")
//                    .withPathType("Prefix")
//                    .withNewBackend()
//                    .withNewService()
//                    .withName(metadata.getName())
//                    .withNewPort().withNumber(80).endPort()
//                    .endService()
//                    .endBackend()
//                    .endPath()
//                    .endHttp()
//                    .endRule()
//                    .endSpec()
//                    .build();
//
//            ingressAtual = kubernetesClient.resources(Ingress.class).resource(novoIngress).create();
//        }
//
//        if (ingressAtual != null) {
//            IngressStatus status = ingressAtual.getStatus();
//            if (status != null) {
//                List<LoadBalancerIngress> ingress = status.getLoadBalancer().getIngress();
//                if (ingress != null && !ingress.isEmpty()) {
//                    return ingressAtual;
//                }
//            }
//        }
//
//        return null;
//    }
//
//    private String extraiURL(Ingress ingress) {
//        LoadBalancerIngress ingressLB = ingress.getStatus().getLoadBalancer().getIngress().get(0);
//        final String hostname = ingressLB.getHostname();
//        final String url = "http://gilmario.truesystem.com.br";
//        return url;
//    }
//
//}
