package vstr.dto;

public class TransmisionDto {

    private Long transmisionId;

    public TransmisionDto(Long transmisionId) {
        this.transmisionId = transmisionId;
    }

    public TransmisionDto() {
    }

    public Long getTransmisionId() {
        return transmisionId;
    }

    public void setTransmisionId(Long transmisionId) {
        this.transmisionId = transmisionId;
    }
}
