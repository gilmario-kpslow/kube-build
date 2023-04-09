package br.com.gilmariosoftware.builder;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author gilmario
 */
@Getter
@Setter
public class BuilderStatus {

    private String url;
    private String mensagem;
    private boolean pronta = false;

    public BuilderStatus() {
        mensagem = "processando";
    }

    public BuilderStatus(String url) {
        this.mensagem = "pronta";
        this.url = url;
        pronta = true;
    }

}
