package Server;

import java.rmi.RemoteException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import Logs.Log;
public class ServerInstance {
    public static void main(String args[]) throws RemoteException {
        Log logger_atw;
        logger_atw = new Log("ATW", false, true);
        Logger Logobj_atw = Logger.getLogger("ATW");
        Logobj_atw = logger_atw.attachFileHandlerToLogger(Logobj_atw);
        Runnable server_instance_atwater = new Servers(8001, "ATW", Logobj_atw);


        Log logger_ver;
        logger_ver = new Log("VER", false, true);
        Logger logObj_ver = Logger.getLogger("VER");
        logObj_ver = logger_ver.attachFileHandlerToLogger(logObj_ver);
        Runnable server_instance_verdnum = new Servers(8002, "VER", logObj_ver);

        Log logger_out;
        logger_out = new Log("OUT", false, true);
        Logger logobJ_out = Logger.getLogger("OUT");
        logobJ_out = logger_out.attachFileHandlerToLogger(logobJ_out);
        Runnable server_instance_outremont = new Servers(8003, "OUT",logobJ_out);

        Executor executor = Executors.newFixedThreadPool(3);
        executor.execute(server_instance_atwater);
        executor.execute(server_instance_verdnum);
        executor.execute(server_instance_outremont);

    }

}