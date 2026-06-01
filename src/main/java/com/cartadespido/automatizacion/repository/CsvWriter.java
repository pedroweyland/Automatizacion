package com.cartadespido.automatizacion.repository;

import com.cartadespido.automatizacion.model.Worker;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvWriter {

    public void escribirEnCsv(List<Worker> workers, String pathCsv) {
        try (CSVWriter writer = new CSVWriter(
                new FileWriter(pathCsv),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
            )
        {

            String[] header = {"Status", "Rut Empleador", "Rut Trabajador", "Nombres", "Apellido Paterno", "Apellido Materno", "Domicilio Trabajo", "Codigo Comuna", "Sexo", "Codigo Nacionalidad", "Forma de Comunicacion", "Fecha inicio Contrato", "Fecha Termino Contrato", "Fecha Comunicacion Despido", "Oficina de Correo", "Causal", "Motivo", "Anio Servicio", "Aviso", "Previo", "Prevensionales"};

            writer.writeNext(header);

            for (Worker worker : workers) {
                String[] row = createRow(worker);
                writer.writeNext(row);
            }
        } catch (IOException e) {
            System.out.println("Error writing CSV: " + e.getMessage());
        }
    }

    private String[] createRow(Worker worker) {
        return new String[]{
                worker.getStatus().toString(),
                worker.getRutEmpleador().toString(),
                worker.getRutTrab().toString(),
                worker.getNom(),
                worker.getApePat(),
                worker.getApeMat(),
                worker.getSelDom(),
                worker.getCodCom().toString(),
                worker.getSex().toString(),
                worker.getNac().toString(),
                worker.getForma().toString(),
                worker.getFecIniCon().toString(),
                worker.getFecTerCon().toString(),
                worker.getFecComDes().toString(),
                worker.getOfi(),
                worker.getCausal().toString(),
                worker.getMotivo(),
                worker.getAnoSer().toString(),
                worker.getAviso().toString(),
                worker.getPrev(),
                worker.getDoc().toString()
        };
    }
}
