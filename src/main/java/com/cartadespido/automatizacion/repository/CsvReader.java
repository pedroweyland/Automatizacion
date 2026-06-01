package com.cartadespido.automatizacion.repository;

import com.cartadespido.automatizacion.model.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    public List<Worker> leerDesdeCsvWorkers(String pathCsv) {
        List<Worker> workers = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(pathCsv))) {
            List<String[]> filas = reader.readAll();
            boolean esHeader = true;

            for (String[] fila : filas) {
                if (esHeader) {
                    esHeader = false;
                    continue;
                }

                Worker worker = buildWorker(fila);

                workers.add(worker);
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
            throw e;
        } catch (IOException | CsvException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }

        return workers;
    }

    private Worker buildWorker(String[] row) {
        Long rutTrab = validateLongData(row[1], "Rut trabajador");
        String comuna = row[6];
        String nac = row[8];
        String causal = row[14];

        validateData(rutTrab, comuna, nac, causal);

        return mapToWorker(row);
    }

    private void validateData(Long rutTrab, String comuna, String nac, String causal) {
        if (!Comunas.COMUNAS.containsKey(comuna)) {
            throw new IllegalArgumentException("La personas con RUT: " + rutTrab + " tiene comuna invalida: " + comuna);
        }

        if (!Nacionalidades.NACIONALIDADES.containsKey(nac)) {
            throw new IllegalArgumentException("La persona con RUT: " + rutTrab + " tiene nacionalidad invalida: " + nac);
        }

        if (!Causales.CAUSALES.containsKey(causal)) {
            throw new IllegalArgumentException("La persona con RUT: " + rutTrab + " tiene causal invalida: " + causal);
        }
    }

    private Long validateLongData(String data, String message) {
        try {
            return Long.parseLong(data);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("En la columna " + message + " no es un numero: " + data);
        }
    }

    private Integer validateIntegerData(String data, String message) {
        try {
            return Integer.parseInt(data);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("En la columna " + message + " no es un numero: " + data);
        }
    }

    private Integer validateSexo(String sexoStr) {
        try {
            Integer sexo = Integer.parseInt(sexoStr);

            if (sexo != 1 && sexo != 2) {
                throw new IllegalArgumentException("En la columna Sexo admite solo 1 y 2, usted puso: " + sexoStr);
            }

            return sexo;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("En la columna Sexo no es un numero: " + sexoStr);
        }
    }

    private Worker mapToWorker(String[] row) {
        return Worker.builder()
                .status(Status.PENDIENTE)
                .RutEmpleador(validateLongData(row[0], "Rut empleador"))
                .RutTrab(validateLongData(row[1], "Rut trabajador"))
                .Nom(row[2])
                .ApePat(row[3])
                .ApeMat(row[4])
                .SelDom(row[5])
                .CodCom(validateIntegerData(row[6], "Codigo Comuna"))
                .Sex(validateSexo(row[7]))
                .Nac(validateIntegerData(row[8], "Nacionalidad"))
                .Forma(validateIntegerData(row[9], "Forma"))
                .FecIniCon(row[10])
                .FecTerCon(row[11])
                .FecComDes(row[12])
                .Ofi(row[13])
                .Causal(row[14])
                .Motivo(row[15])
                .AnoSer(validateLongData(row[16], "Anio de Servicio"))
                .Aviso(validateLongData(row[17], "Aviso Previo"))
                .Prev(row[18])
                .Doc(validateIntegerData(row[19], "Pago previsional"))
                .build();
    }
}