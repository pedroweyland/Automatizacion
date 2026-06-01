package com.cartadespido.automatizacion.util;

import com.cartadespido.automatizacion.model.Worker;
import com.cartadespido.automatizacion.repository.CsvReader;
import javafx.stage.FileChooser;
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

    public static File cargarArchivoBusqueda(String title){
        FileChooser file = cargarArchivo(title, null);
        return file.showOpenDialog(null);
    }

    public static File cargarArchivoGuardar(String title, String initialName){
        FileChooser file = cargarArchivo(title, initialName);
        return file.showSaveDialog(null);
    }

    private static FileChooser cargarArchivo(String title, String initialName){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (initialName != null) fileChooser.setInitialFileName(initialName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));

        return fileChooser;
    }
}
