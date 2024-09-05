package org.brownsolutions.model;

public enum FileChooserType {
    OPEN("Selecionar arquivo"),
    OPEN_MULTIPLE("Selecionar arquivos"),
    SAVE("Salvar arquivo");

    private final String title;

    FileChooserType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
