package com.cartadespido.automatizacion.util;

import com.cartadespido.automatizacion.model.Worker;
import com.cartadespido.automatizacion.repository.CsvReader;

import java.io.File;
import java.util.List;

public class FileLoader {

    public static List<Worker> cargarWorkers(File archivo) {
        if (archivo == null || !archivo.exists()) {
            throw new IllegalArgumentException("El archivo no existe o es nulo.");
        }

        CsvReader reader = new CsvReader();
        List<Worker> workers = reader.leerDesdeCsvWorkers(archivo.getAbsolutePath());

        if (workers == null || workers.isEmpty()) {
            throw new IllegalArgumentException("El archivo CSV está vacío o mal formado.");
        }

        return workers;
    }
}
