package habilitipro.enums;

public enum Status {
    NAOINICIADO("Curso não iniciado"),
    EMANDAMENTO("Curso em andamento"),
    AVALIACAO("Em fase de avaliação"),
    FINALIZADO("Fase de avaliação finalizada");

    private String status;

    private Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
