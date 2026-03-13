package com.caiohenrique.demo_park_api.repository.projection;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name", "cpf"})
public interface ClientProjection {

    Long getId();

    String getName();

    String getCpf();
}
