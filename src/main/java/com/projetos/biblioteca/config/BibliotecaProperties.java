package com.projetos.biblioteca.config;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "biblioteca")
@Validated
public record BibliotecaProperties(
        @DefaultValue("14") @Min(1)
        int prazoDias,

        @DefaultValue("7") @Min(1)
        int renovacaoDias,

        @DefaultValue("1") @Min(0)
        int maxRenovacoes,

        @DefaultValue("3") @Min(1)
        int limiteEmprestimosAtivos,

        @DefaultValue("1.00") @DecimalMin("0.00")
        BigDecimal multaPorDia
) {}
