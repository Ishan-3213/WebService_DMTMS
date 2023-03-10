package Server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Logger;
import Implementation.ImplementationOperations;
import javax.xml.ws.Endpoint;

public class Servers implements Runnable {
    int WebServicePortNumber;
    String server_name;
    Logger LogObj;
    Servers(int WebServicePortNumber, String server_name, Logger LogObj){
        super();
            this.server_name = server_name;
            this.WebServicePortNumber = WebServicePortNumber;
            this.LogObj = LogObj;
    }

    public void serve_listener(ImplementationOperations impobj, Logger LogObj){
        DatagramSocket datasocket = null;
        String customer_id;
        String movie_id;
        String movie_name;
        String method;
        try {
            datasocket = new DatagramSocket(this.WebServicePortNumber);
            byte [] buffer = new byte[1024];
//            System.out.println("Server ->" + this.server_name +"\n" + "Port ->" + this.WebServicePortNumber);

            while(true){
                DatagramPacket received = new DatagramPacket(buffer, buffer.length);
                datasocket.receive(received);

                String data = new String(buffer, 0, received.getLength());
                String [] splitted = data.split("<>");

                Integer tickets = Integer.parseInt(splitted[4]);
                customer_id = splitted[3];
                movie_id = splitted[2];
                movie_name = splitted[1];
                method = splitted[0];
                System.out.println(data+ "\n" +  method + "\n" + movie_name + "\n" + movie_id + "\n" + customer_id +"\n" + tickets + "\n");
                switch(method){
                    case "list_movie":
                        String received_data = impobj.list_movie(movie_name);
                        System.out.println("----------" + received_data + "----------");
                        byte [] byte_data = received_data.getBytes();
                        DatagramPacket reply = new DatagramPacket(byte_data, received_data.length() ,received.getAddress(), received.getPort());
                        System.out.println("Message from the server " + server_name + " at the port " +WebServicePortNumber);
                        LogObj.info("Message from the server " + server_name + " in list_movie at the port " +WebServicePortNumber);
                        datasocket.send(reply);
                        break;
                    case "bookMovieTickets":
                        String data_received = impobj.bookMovieTickets(customer_id, movie_id, movie_name, tickets);
                        System.out.println("----------" + data_received + "----------");
                        byte [] data_byte = data_received.getBytes();
                        DatagramPacket response = new DatagramPacket(data_byte, data_received.length() ,received.getAddress(), received.getPort());
                        System.out.println("Message from the server " + server_name + " at the port " +WebServicePortNumber);
                        LogObj.info("Message from the server " + server_name + " in bookMovieTickets at the port " +WebServicePortNumber);
                        datasocket.send(response);
                        break;
                    case "booking_schedule":
                        String received_str = impobj.booking_schedule(customer_id);
                        System.out.println("----------" + received_str + "----------");
                        byte [] data_byt = received_str.getBytes();
                        DatagramPacket answer = new DatagramPacket(data_byt, received_str.length() ,received.getAddress(), received.getPort());
                        System.out.println("Message from the server " + server_name + " at the port " +WebServicePortNumber);
                        LogObj.info("Message from the server " + server_name + " in booking_schedule at the port " +WebServicePortNumber);
                        datasocket.send(answer);
                        break;
                    case "cancelMovieTickets":
                        String str_received = impobj.cancelMovieTickets(customer_id, movie_id, movie_name, tickets);
                        System.out.println("----------" + str_received + "----------");
                        byte [] byt_data = str_received.getBytes();
                        DatagramPacket acknowledgment = new DatagramPacket(byt_data, str_received.length() ,received.getAddress(), received.getPort());
                        System.out.println("Message from the server " + server_name + " at the port " +WebServicePortNumber);
                        LogObj.info("Message from the server " + server_name + " in cancelMovieTickets at the port " +WebServicePortNumber);
                        datasocket.send(acknowledgment);
                        break;
                    case "condition_checks":
                        String condition_checks = "No";
                        boolean check = impobj.conditionChecks(movie_name, movie_id, tickets);
                        System.out.println("check----------" + check + "----------check");
                        if (check){
                            condition_checks = "Yes";
                        }
                        byte [] bt_data = condition_checks.getBytes();
                        DatagramPacket ack = new DatagramPacket(bt_data, condition_checks.length() ,received.getAddress(), received.getPort());
                        System.out.println("Message from the server " + server_name + " at the port " +WebServicePortNumber);
                        LogObj.info("Message from the server " + server_name + " in cancelMovieTickets at the port " +WebServicePortNumber);
                        datasocket.send(ack);
                        break;
                    case "ExchangeMovieShow":
                        String data_rcved = impobj.ExchangeMovieShow(customer_id, movie_name, movie_id, tickets);
                        System.out.println("----------" + data_rcved + "----------");
                        byte [] dt_byt = data_rcved.getBytes();
                        DatagramPacket rspns = new DatagramPacket(dt_byt, data_rcved.length() ,received.getAddress(), received.getPort());
                        System.out.println("Message from the server " + server_name + " at the port " +WebServicePortNumber);
                        LogObj.info("Message from the server " + server_name + " in bookMovieTickets at the port " +WebServicePortNumber);
                        datasocket.send(rspns);
                        break;
                    default:
                        System.out.println("Not working...!");
                }
            }
        }catch (SocketException e) {System.out.println("Something wrong with the SKT-ServerSide: " + e.getMessage());
        }catch(IOException e){System.out.println("Somthing went wrong in IO: " + e.getMessage());
        }finally{if(datasocket != null) datasocket.close();}
    }

    @Override
    public void run() {
        ImplementationOperations impobj = new ImplementationOperations(server_name, LogObj);
        Runnable task = () -> {serve_listener(impobj, LogObj);};
        Thread t1 = new Thread(task);
        t1.start();
        Endpoint endpoint = Endpoint.publish("http://localhost:8080/" + server_name, impobj);
        System.out.println("Server ->" + server_name +"\n"+ endpoint.isPublished()+ "\n" + "Port ->" + WebServicePortNumber);
    }
}

