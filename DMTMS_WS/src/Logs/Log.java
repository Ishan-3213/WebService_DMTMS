package Logs;
import java.io.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
public class Log {
    private File file;
    private FileHandler file_handler;

    public Log(String fileName, boolean client_request, boolean server_request) {
        file = CreateFile(fileName,client_request,server_request);
        file_handler = setFileHandler();
    }
    private File CreateFile(String fileName, boolean client_request, boolean server_request) {
        final String dir = "D:\\Academic\\Concordia\\Sem-1\\DSD\\Assignemnt\\Git-Repo\\CORBA_DMTMS\\src\\Logs\\";
        if(client_request) {
            createDirectoryIfNotExist(dir,"Client_Logs");
            file = new File(dir+"Client_Logs/"+fileName+".log");
            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.getStackTrace();
                }
            }
            return file;
        } else if(server_request) {
            createDirectoryIfNotExist(dir,"Server_Logs");
            file = new File(dir+"Server_Logs/"+fileName+".log");
            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.getStackTrace();
                }
            }
            return file;
        }
        return null;
    }

    private void createDirectoryIfNotExist(String dir, String directoryName) {
        try {
            Files.createDirectories(Paths.get(dir+directoryName));
        }catch (IOException ex) {
            ex.getStackTrace();
        }
    }

    private FileHandler setFileHandler() {
        try {
            file_handler = new FileHandler(file.getAbsolutePath(),1024*10000,1,true);
            file_handler.setLevel(Level.ALL);
            file_handler.setFormatter(new SimpleFormatter());
            return file_handler;
        }catch (IOException ex) {
            ex.getStackTrace();
        }
        return file_handler;
    }

    private FileHandler getFileHandlerObj() {
        return file_handler;
    }

    public Logger attachFileHandlerToLogger(Logger logger) {
        logger.addHandler(getFileHandlerObj());
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        return logger;
    }

}
