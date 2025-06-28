package com.cartadespido.automatizacion.repository;

import com.cartadespido.automatizacion.model.Causales;
import com.cartadespido.automatizacion.model.Comunas;
import com.cartadespido.automatizacion.model.Nacionalidades;
import com.cartadespido.automatizacion.model.Worker;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CsvReader {

    public static <T> List<T> readCsv(String pathCsv, Function<String[], T> mapper) {
        List<T> result = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(pathCsv))) {
            List<String[]> rows = reader.readAll();
            boolean isHeader = true;

            for (String[] row : rows) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                T obj = mapper.apply(row);
                result.add(obj);
            }

        } catch (IOException | CsvException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }

        return result;
    }


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
                System.out.println(worker.getOfi());
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
        Long rutTrab = Long.parseLong(row[0]);
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

    private Worker mapToWorker(String[] row) {
        return Worker.builder()
                .RutTrab(Long.parseLong(row[0]))
                .RutEmpleador(Long.parseLong(row[1]))
                .Nom(row[2])
                .ApePat(row[3])
                .ApeMat(row[4])
                .SelDom(row[5])
                .CodCom(Integer.parseInt(row[6]))
                .Sex(Integer.parseInt(row[7]))
                .Nac(Integer.parseInt(row[8]))
                .Forma(Integer.parseInt(row[9]))
                .FecIniCon(row[10])
                .FecTerCon(row[11])
                .FecComDes(row[12])
                .Ofi(row[13])
                .Causal(row[14])
                .Motivo(row[15])
                .AnoSer(Long.parseLong(row[16]))
                .Aviso(Long.parseLong(row[17]))
                .Prev(row[18])
                .Doc(Integer.parseInt(row[19]))
                .build();
    }

}