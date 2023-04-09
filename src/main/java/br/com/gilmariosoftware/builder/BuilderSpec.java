package br.com.gilmariosoftware.builder;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author gilmario
 */
@Getter
@Setter
public class BuilderSpec {

    private String imagem;
    private String versao;
    private Map<String, String> buildProperties = new HashMap<>();
    private Map<String, String> additionalProperties = new HashMap<>();

}
