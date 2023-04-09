package br.com.gilmariosoftware.builder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;
import java.util.Map;

/**
 *
 * @author gilmario
 */
@JsonDeserialize(using = JsonDeserializer.None.class)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = {"apiVersion", "kind", "metadata", "registry", "image", "dockerfile", "gitUrl", "name", "tag"})
@Version(value = "v1")
@Group(value = "")
public class BuilderConfig implements HasMetadata, Namespaced {

    @JsonProperty(value = "apiVersion")
    private String apiVersion;
    @JsonProperty(value = "kind")
    private String kind;
    @JsonProperty(value = "image")
    private String image;
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "registry")
    private String registry;
    @JsonProperty(value = "metadata")
    private ObjectMeta metadata;
    @JsonProperty(value = "tag")
    private String tag;
    @JsonProperty(value = "dockerfile")
    private String dockerfile;
    @JsonProperty(value = "gitUrl")
    private String gitUrl;
    @JsonIgnore
    private Map<String, Object> additionalProperties;

    public BuilderConfig() {
    }

    public BuilderConfig(String apiVersion, String kind, ObjectMeta metadata, Map<String, Object> additionalProperties) {
        this.apiVersion = apiVersion;
        this.kind = kind;
        this.metadata = metadata;
        this.additionalProperties = additionalProperties;
    }

    @Override
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    public ObjectMeta getMetadata() {
        return this.metadata;
    }

    @Override
    public void setMetadata(ObjectMeta om) {
        this.metadata = om;
    }

    @Override
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public String getApiVersion() {
        return apiVersion;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDockerfile() {
        return dockerfile;
    }

    public void setDockerfile(String dockerfile) {
        this.dockerfile = dockerfile;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
