/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.gilmariosoftware.builder;

import io.fabric8.kubernetes.client.KubernetesClient;
import javax.ws.rs.Path;

/**
 *
 * @author gilmario
 */
@Path("builder")
public class BuilderEndpoint {

    private final KubernetesClient kubernetesClient;

    public BuilderEndpoint(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

}
