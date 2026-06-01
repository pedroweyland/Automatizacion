package com.cartadespido.automatizacion.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Worker {

    private Status status;
    //Data Workers
    private Long RutEmpleador;
    private Long RutTrab;
    private String Nom;
    private String ApePat;
    private String ApeMat;
    private String SelDom;
    private Integer CodCom;
    private Integer Sex;
    private Integer Nac;

    //Details Carta
    private Integer Forma;
    private String FecIniCon;
    private String FecTerCon;
    private String FecComDes;
    private String Ofi;
    private String Causal;
    private String Motivo;

    //Amount Carta
    private Long AnoSer;
    private Long Aviso;

    //Quotes Carta
    private String Prev;
    private Integer Doc;


}
