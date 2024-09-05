package org.brownsolutions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Line {

    private String cnpj;
    private String description;
    private int cfop;
    private double total;
    private double base;
    private double icms;
}
