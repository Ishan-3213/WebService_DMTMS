package Implementation;
import InterfaceOperationApp.InterfaceOperations;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.jws.WebService;

@WebService(endpointInterface = "InterfaceOperationApp.InterfaceOperations")
public class ImplementationOperations implements InterfaceOperations {
    HashMap<String, HashMap<String, Integer>> datastorage;
    HashMap<String, HashMap<String, Integer>> user_data;
    HashMap<String, Integer> booking_hashmap;
    HashMap<String, Integer> customer_booking_hashmap;
    String server_name;
    Logger LogObj;

    public ImplementationOperations(String server_name, Logger LogObj) {
        super();
        this.server_name = server_name;
        this.LogObj = LogObj;
        datastorage = new HashMap<>();
        user_data = new HashMap<>();
        booking_hashmap = new HashMap<String, Integer>();
        customer_booking_hashmap = new HashMap<String, Integer>();

        datastorage.put("AVATAR", new HashMap<String, Integer>());
        datastorage.put("AVENGER", new HashMap<String, Integer>());
        datastorage.put("TITANIC", new HashMap<String, Integer>());

        if(server_name.equals("ATW")){

            booking_hashmap = datastorage.get("AVENGER");
            booking_hashmap.put("ATWM17032023", 10);
            booking_hashmap.put("ATWE19032023", 10);

            datastorage.put("AVENGER", booking_hashmap);

            booking_hashmap = datastorage.get("TITANIC");
            booking_hashmap.put("ATWA08032023", 10);
            datastorage.put("TITANIC", booking_hashmap);

            user_data.put("ATWC1234", new HashMap<String, Integer>());

            customer_booking_hashmap = user_data.get("ATWC1234");
            customer_booking_hashmap.put("AVENGER-ATWE19032023", 3);
            user_data.put("ATWC1234", customer_booking_hashmap);

        }else if(server_name.equals("VER")){
            booking_hashmap = datastorage.get("AVATAR");
            booking_hashmap.put("VERA09032023", 10);
            datastorage.put("AVATAR", booking_hashmap);

            booking_hashmap = datastorage.get("TITANIC");
            booking_hashmap.put("VERE08032023", 10);
            booking_hashmap.put("VERM09032023", 10);
            datastorage.put("TITANIC", booking_hashmap);

            user_data.put("VERC4321", new HashMap<String, Integer>());

            customer_booking_hashmap = user_data.get("VERC4321");
            customer_booking_hashmap.put("TITANIC-VERE08032023", 10);
            user_data.put("VERC4321", customer_booking_hashmap);

        }else if(server_name.equals("OUT")){

//            booking_hashmap = datastorage.get("AVATAR");
//            booking_hashmap.put("OUTM06032023", 100);
//            booking_hashmap.put("OUTE15032023", 100);
//            datastorage.put("AVATAR", booking_hashmap);

            booking_hashmap = datastorage.get("AVENGER");
            booking_hashmap.put("OUTM09032023", 10);
            booking_hashmap.put("OUTA11032023", 10);
            datastorage.put("AVENGER", booking_hashmap);

            user_data.put("OUTC4321", new HashMap<String, Integer>());
            customer_booking_hashmap = user_data.get("OUTC4321");
            customer_booking_hashmap.put("AVENGER-OUTM09032023", 10);
            user_data.put("OUTC4321", customer_booking_hashmap);
        }
      }

    @Override
    public String exchangeMovieTickets(String customerID, String movieId, String movieName, String new_movieId, String new_movieName, int numberOfTickets) {
        // String customer_schedule = getBookingSchedule(customerID);
//        for (String x : customer_schedule.split("\n")){
//            if (x.contains(movieName+"-"+movieId+":")){
//                numberOfTickets = Integer.parseInt(x.replace(movieName+"-"+movieId+":", "").trim());
//                System.out.println("\n\n\nchecking the condition-->>" + numberOfTickets);
//            }
//        }
        boolean can_book = conditionChecks(new_movieName, new_movieId, numberOfTickets);
        if (can_book) {
            String check_cancel = cancelMovieTickets(customerID, movieId, movieName, numberOfTickets);
            System.out.println("\nProcess says-->" + check_cancel);
            if (check_cancel.toUpperCase().substring(0,2).equals("NO") ){
                return "Process failed to cancel movie tickets.";
            }
            String check_booking = ExchangeMovieShow(customerID, new_movieName, new_movieId, numberOfTickets);
            if (check_booking.toUpperCase().substring(0,2).equals("NO")){
                return "Process failed to book new movie tickets.";
            }
            return check_cancel + " & " + check_booking;
        }
        return "Booking can not be done.";
    }

    public String ExchangeMovieShow(String customerID,String new_movieName, String new_movieId, int numberOfTickets ){
            String methodsList;
            StringBuilder sBuilder = new StringBuilder();
        if (customerID.isEmpty() | customerID.equals(null)){
            return "No boking done.";
        }
            if(new_movieId.substring(0,3).equals(this.server_name))  {
                user_data.putIfAbsent(customerID, new HashMap<String, Integer>());
                if(datastorage.containsKey(new_movieName)){
                    if(datastorage.get(new_movieName).containsKey(new_movieId)){
                        if(datastorage.get(new_movieName).get(new_movieId) >= numberOfTickets){
                            System.out.println("Tickets in the server----->>" + server_name +  "\nStrg-->" + datastorage.get(new_movieName));
                            System.out.println("\n\n" + " tickets removal done-->> " + datastorage.get(new_movieName).get(new_movieId));
                            // Tickets are available
                            String movie_string = new_movieName + "-" + new_movieId;
                            if (user_data.containsKey(customerID)){
                                System.out.println("\n\nUser data in server " + server_name + " data-->" + user_data);
                                if (user_data.get(customerID).containsKey(movie_string) ){
                                    datastorage.get(new_movieName).put(new_movieId, datastorage.get(new_movieName).get(new_movieId) - numberOfTickets);
                                    user_data.get(customerID).put(movie_string, user_data.get(customerID).get(movie_string) + numberOfTickets);
                                    System.out.println(user_data.get(customerID) + " --Already booked tickets for the same movie id-- " + customer_booking_hashmap);
                                    LogObj.info(numberOfTickets + " tickets booked for the movie " + new_movieName + "-" + new_movieId);
                                    return numberOfTickets + " tickets booked for the movie " + new_movieName + "-" + new_movieId;
                                }else{
                                    // add data to the existing cutomer id in hashmap
                                    System.out.println("USer nor has movie ticket booked ---?? " + customerID + " number of tickets... " + numberOfTickets);
                                    datastorage.get(new_movieName).put(new_movieId, datastorage.get(new_movieName).get(new_movieId) - numberOfTickets);
                                    user_data.get(customerID).put(movie_string, numberOfTickets);
                                    System.out.println("Customer-ID --->>"+ customerID + " ---" + user_data.get(customerID) + " --Tickets added -- " + customer_booking_hashmap);
                                    LogObj.info(numberOfTickets + " tickets booked for the movie " + new_movieName + "-" + new_movieId);
                                    return numberOfTickets + " tickets booked for the movie " + new_movieName + "-" + new_movieId;
                                }
                            }else{
                                // create new customer id in hasmap
                                System.out.println("User not in hashmap,,,!!!!\t" + customerID + "---->>>" +" tickets " + numberOfTickets);
                                datastorage.get(new_movieName).put(new_movieId, datastorage.get(new_movieName).get(new_movieId) - numberOfTickets);
                                System.out.println("data storage after negation---??? " + datastorage);
                                customer_booking_hashmap = user_data.get(customerID);
                                customer_booking_hashmap.put(movie_string, numberOfTickets);
                                user_data.put(customerID, customer_booking_hashmap);
                                System.out.println(user_data.get(customerID) + " <<<---Logged in user data --New User---->> " + user_data);
                                LogObj.info(numberOfTickets + " tickets booked for the movie " + new_movieName + "-" + new_movieId);
                                return numberOfTickets + " tickets booked for the movie " + new_movieName + "-" + new_movieId;
                            }
                        }else{
                            LogObj.info(numberOfTickets + " Seats are not available for the " + new_movieName + " - " + new_movieId);
                            return "NO " + numberOfTickets + " Seats are not available for the " + new_movieName + "-" + new_movieId;
                        }
                    }else{
                        LogObj.info("No movie found with the ID" + new_movieId);
                        return "No movie found with the ID" + new_movieId;
                    }
                }
                LogObj.info("No movie found with the namw" + new_movieName);
                return "No movie found with the name " + new_movieName;
            }else{
                // method + "<>" + movie_name + "<>" + movie_id + "<>" + customer_id + "<>" + tickets
                methodsList = "ExchangeMovieShow" + "<>" + new_movieName + "<>" + new_movieId + "<>" + customerID + "<>" + numberOfTickets;
                if(new_movieId.substring(0, 3).equals("VER") && !(new_movieId.substring(0,3).equals(this.server_name))){
                    sBuilder.append(sending_message(methodsList, "VER", 8002));
                }else if(new_movieId.substring(0, 3).equals("OUT") && !(new_movieId.substring(0,3).equals(this.server_name))){
                    sBuilder.append(sending_message(methodsList, "OUT", 8003));
                }else if(new_movieId.substring(0, 3).equals("ATW") && !(new_movieId.substring(0,3).equals(this.server_name))){
                    sBuilder.append(sending_message(methodsList, "ATW", 8001));
                }
                return sBuilder.toString();
            }
    }

    public boolean conditionChecks(String movieName, String movieID, int Capacity){
        String movie_prefix = movieID.substring(0,3);
        String methodsList;
        String reply = null;
        if (movie_prefix.equals(this.server_name)){
            if (this.datastorage.containsKey(movieName)){
                if (this.datastorage.get(movieName).containsKey(movieID)){
                    if ((this.datastorage.get(movieName).get(movieID))>=Capacity){
                        return true;
                    }
                    else {
                        System.out.println("Seats not available...!!");
                    }
                }
                else {
                    System.out.println("No movie id found.!!");
                }
            }
            else {
                System.out.println("No movie found.");
            }
        }else {
            // method + "<>" + movie_name + "<>" + movie_id + "<>" + customer_id + "<>" + tickets
            methodsList = "condition_checks" + "<>" + movieName + "<>" + movieID + "<>" + null + "<>" + Capacity;
            if(movie_prefix.equals("VER") && !(movie_prefix.equals(this.server_name))){
                reply = sending_message(methodsList, "VER", 8002);
            }else if(movie_prefix.equals("OUT") && !(movie_prefix.equals(this.server_name))){
                reply = sending_message(methodsList, "OUT", 8003);
            }else if(movie_prefix.equals("ATW") && !(movie_prefix.equals(this.server_name))){
                reply = sending_message(methodsList, "ATW", 8001);
            }
            if (!(reply.isEmpty()) && reply.equals("Yes")){
                return true;
            }
        }
        return false;
    }
    @Override
    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {
        if (movieID.substring(0,3).equals(this.server_name)){
            if(user_data.containsKey(customerID)){
                String movie_string = movieName + "-" + movieID;
                if(user_data.get(customerID).containsKey(movie_string)){
                    if(user_data.get(customerID).get(movie_string) > numberOfTickets){
                        datastorage.get(movieName).put(movieID, datastorage.get(movieName).get(movieID) + numberOfTickets);
                        user_data.get(customerID).put(movie_string, user_data.get(customerID).get(movie_string) - numberOfTickets);
                        System.out.println(user_data.get(customerID).get(movie_string) + " IF---Here is the user data " + "\n------>>" + "\n...custome id"+user_data.get(customerID)+"\n ehole user data" + user_data);
                        return numberOfTickets + " Movie tickets for " + movieName + " has been removed";
                    }else if((user_data.get(customerID).get(movie_string).equals(numberOfTickets))){
                        datastorage.get(movieName).put(movieID, datastorage.get(movieName).get(movieID) + numberOfTickets);
                        int tickets = user_data.get(customerID).get(movie_string);
                        user_data.get(customerID).remove(movie_string, tickets);
                        System.out.println(user_data.get(customerID).get(movie_string) + " ELSE Here is the user data "+ "\n...customer id"+user_data.get(customerID)+"\n whole user data" + user_data);
                        return numberOfTickets + " Movie tickets for " + movieName + " has been removed";
                    }else{
                        return "NO..!Please enter valid ticket number to be removed!!";
                    }
                }else{
                    return "NO movie found with the movieID- " + movieID;
                }
            }else{
                return "NO userdata found with the id " + customerID;
            }
        }else {
            StringBuilder sBuilder = new StringBuilder();
            // method + "<>" + movie_name + "<>" + movie_id + "<>" + customer_id + "<>" + tickets
            String methodsList = "cancelMovieTickets" + "<>" + movieName + "<>" + movieID + "<>" + customerID + "<>" + numberOfTickets;
            if(movieID.substring(0, 3).equals("VER") && !(movieID.substring(0,3).equals(this.server_name))){
                sBuilder.append(sending_message(methodsList, "VER", 8002));
            }else if(movieID.substring(0, 3).equals("OUT") && !(movieID.substring(0,3).equals(this.server_name))){
                sBuilder.append(sending_message(methodsList, "OUT", 8003));
            }else if(movieID.substring(0, 3).equals("ATW") && !(movieID.substring(0,3).equals(this.server_name))){
                sBuilder.append(sending_message(methodsList, "ATW", 8001));
            }
            return sBuilder.toString();
        }    }

    @Override
    public String addMovieSlots(String movieId, String movieName, int bookingCapacity) {
        try {
            if (datastorage.containsKey(movieName)){
                if (datastorage.get(movieName).containsKey(movieId)) {
                    datastorage.get(movieName).put(movieId, bookingCapacity + datastorage.get(movieName).get(movieId));
                    System.out.println();
                    System.out.println("Movie's slot with the ID " + movieId + " has been updated!");
                    System.out.println(datastorage);
                    LogObj.info("Movie slot updated.");
                    return "Movie slot updated.";
                }else{
                    datastorage.get(movieName).put(movieId, bookingCapacity);
                    System.out.println("Data has been added" + datastorage);
                    LogObj.info("Movie slot updated.");
                    return "Movie slot added.";
                }
            }else{
                System.out.println();
                System.out.println("Movie is not there..!!");
                booking_hashmap.put(movieId, bookingCapacity);
                datastorage.put(movieName, booking_hashmap);
                System.out.println("Movie slot has been added..!!" + datastorage);
                LogObj.info("New Movie slot added.");
                return "New movie slot has been added..!!";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }    }

    @Override
    public String bookMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets)  {
        String methodsList;
        StringBuilder sBuilder = new StringBuilder();
        if (customerID.isEmpty() | customerID.equals(null)){
            return "No boking done.";
        }
        if(customerID.substring(0,3).equals(server_name)){}else
        {
            if(numberOfTickets >3){
                System.out.println("You cannot book more than 3 tickets on other theater");
                return "No You cannot book more than 3 tickets on other theater";
            }
        }
        for (String check_data : getBookingSchedule(customerID).split("\n")){
            if (check_data.contains(movieName+"-"+movieId+":")){}
            else if (check_data.contains(movieName) && check_data.contains(movieId.substring(4,12))) {
                System.out.println("Already Booked ticket in another theater!");
                return "No Tickets are already booked in another theater.";
            }
        }
        if(movieId.substring(0,3).equals(this.server_name))  {
            if(datastorage.containsKey(movieName)){
                if(datastorage.get(movieName).containsKey(movieId)){
                    if(datastorage.get(movieName).get(movieId) >= numberOfTickets){
                        System.out.println("Tickets in the server----->>" + server_name +  "\nStrg-->" + datastorage.get(movieName).get(movieId));
                        datastorage.get(movieName).put(movieId, datastorage.get(movieName).get(movieId) - numberOfTickets);
                        System.out.println("\n\n" + " tickets removal done-->> " + datastorage.get(movieName).get(movieId));
                        // Tickets are available 0
                        String movie_string = movieName + "-" + movieId;
                        if (user_data.containsKey(customerID)){
                            System.out.println("\n\nUSer data in server " + server_name + " data-->" + user_data);
                            if (user_data.get(customerID).containsKey(movie_string) ){
                                user_data.get(customerID).put(movie_string, user_data.get(customerID).get(movie_string) + numberOfTickets);
                                System.out.println(user_data.get(customerID) + " --Already booked tickets for the same movie id-- " + customer_booking_hashmap);
                                LogObj.info(numberOfTickets + " tickets booked for the movie " + movieName + "-" + movieId);

                                return numberOfTickets + " tickets booked for the movie " + movieName + "-" + movieId;
                            }else{
                                // add data to the existing cutomer id in hashmap
                                user_data.get(customerID).put(movie_string, numberOfTickets);
                                System.out.println("Customer-ID --->>"+ customerID + " ---" + user_data.get(customerID) + " --Tickets added -- " + customer_booking_hashmap);
                                LogObj.info(numberOfTickets + " tickets booked for the movie " + movieName + "-" + movieId);
                                return numberOfTickets + " tickets booked for the movie " + movieName + "-" + movieId;
                            }
                        }else{
                            // create new customer id in hasmap
                            user_data.putIfAbsent(customerID, new HashMap<String, Integer>());
                            customer_booking_hashmap = user_data.get(customerID);
                            customer_booking_hashmap.put(movie_string, numberOfTickets);
                            user_data.put(customerID, customer_booking_hashmap);
                            System.out.println(user_data.get(customerID) + " --New User-- " + user_data);
                            LogObj.info(numberOfTickets + " tickets booked for the movie " + movieName + "-" + movieId);
                            return numberOfTickets + " tickets booked for the movie " + movieName + "-" + movieId;
                        }
                    }else{
                        LogObj.info(numberOfTickets + " Seats are not available for the " + movieName + " - " + movieId);
                        return "No " + numberOfTickets + " Seats are not available for the " + movieName + " - " + movieId;
                    }
                }else{
                    LogObj.info("No movie found with the ID" + movieId);
                    return "No movie found with the ID" + movieId;
                }
            }
            LogObj.info("No movie found with the namw" + movieName);
            return "No movie found with the name " + movieName;
        }else{
            // method + "<>" + movie_name + "<>" + movie_id + "<>" + customer_id + "<>" + tickets
            methodsList = "bookMovieTickets" + "<>" + movieName + "<>" + movieId + "<>" + customerID + "<>" + numberOfTickets;
            if(movieId.substring(0, 3).equals("VER") && !(movieId.substring(0,3).equals(this.server_name))){
                sBuilder.append(sending_message(methodsList, "VER", 8002));
            }else if(movieId.substring(0, 3).equals("OUT") && !(movieId.substring(0,3).equals(this.server_name))){
                sBuilder.append(sending_message(methodsList, "OUT", 8003));
            }else if(movieId.substring(0, 3).equals("ATW") && !(movieId.substring(0,3).equals(this.server_name))){
                sBuilder.append(sending_message(methodsList, "ATW", 8001));
            }
            return sBuilder.toString();
        }
    }
    @Override
    public String removeMovieSlots(String movieId, String movieName) {
        if(datastorage.containsKey(movieName)){
            if (datastorage.get(movieName).containsKey(movieId)){
                String customers = RetrieveCustomers(movieId, movieName);
                for (String cancel_movie_ticekts : customers.split("\n")){
                    if (!cancel_movie_ticekts.isEmpty()){
                        int tickets = Integer.parseInt(cancel_movie_ticekts.split(":")[1]);
                        String response = cancelMovieTickets(cancel_movie_ticekts.split(":")[0], movieId, movieName, tickets);
                        if (!response.toUpperCase().startsWith("NO")){
                        }
                    }
                }
                datastorage.get(movieName).remove(movieId, datastorage.get(movieName).get(movieId));
                MovieTicketsToNextShow(customers, movieName);
                System.out.println("\n");
                System.out.println(datastorage.get(movieName) +" after removal..!! ");
                LogObj.info("Movie slot for " + movieName + " has been removed");
                return "Movie slot for " + movieName + " has been removed";
            }else {
                System.out.println("there is no movie slot for this movie..!!!");
                LogObj.info("No movie slot found for the movie " + movieName + " at " + movieId.substring(0, 3) + " region");
                return "No movie slot found for the movie " + movieName + " at " + movieId.substring(0, 3) + " region";
            }
        }else{
            LogObj.info("No movie slot found for the movie " + movieName + " at " + movieId.substring(0, 3) + " region");
            return "No movie slot found for the movie " + movieName + " at " + movieId.substring(0, 3) + " region";
        }    }

    public String RetrieveCustomers(String MovieId, String MovieName){
        String movie_string = MovieName + "-" + MovieId;
        StringBuilder SB = new StringBuilder();
        for (HashMap.Entry<String, HashMap<String, Integer>> entry : user_data.entrySet()) {
            if (entry.getValue().containsKey(movie_string)) {
                String key = entry.getKey();
                int tickets = entry.getValue().get(movie_string);
                System.out.println("Key for value " + movie_string + " is: " + key);
                SB.append(key).append(":").append(tickets).append("\n");
            }
//            System.out.println("Not working the eqals condition...!!"  + movie_string + "-----" + entry);
        }
        return SB.toString();
    }

    public void MovieTicketsToNextShow(String Customers, String MovieName){
        String [] split_customer = Customers.split("\n");
        String [] movie_shows = listMovieShowsAvailability(MovieName).split("\n");
        for (String CustomerLoop : split_customer){
            if (!CustomerLoop.isEmpty()) {
                for (String MovieLoop : movie_shows) {
                    if (!MovieLoop.isEmpty()){
                        int tickets = Integer.parseInt(CustomerLoop.split(":")[1]);
                        String response = ExchangeMovieShow(CustomerLoop.split(":")[0], MovieName, MovieLoop.split("<>")[0], tickets);
                        if (!response.toUpperCase().startsWith("NO")) {
                            System.out.println("Print found the tickets..." + response);
                            break;
                        }
                        System.out.println("Not Found--->>>" + response);
                    }
                }
            }
        }
    }
    @Override
    public String listMovieShowsAvailability(String movieName) {
        StringBuilder sBuilder = new StringBuilder();
        if (datastorage.containsKey(movieName)){
            for (String OuterKey : this.datastorage.keySet()) {
//                System.out.println(OuterKey);
                if(OuterKey.equals(movieName)){
                    for (String InnerKey : this.datastorage.get(OuterKey).keySet()){
                        sBuilder.append(InnerKey).append("<>").append(this.datastorage.get(OuterKey).get(InnerKey)).append("\n");
                    }
                }
            }
            // method + "<>" + movie_name + "<>" + movie_id + "<>" + customer_id + "<>" + tickets
            String data = this.UDPcall("list_movie" + "<>" + movieName + "<>" + null + "<>" + null + "<>" + 0);
            data = data.replace(movieName+"<>", "");
            sBuilder.append(data);
            System.out.println("------------------------datastoragedatastoragedatastorage----------------------------------\n" +datastorage + "\n"+sBuilder.toString() + "\n----------------------------------------\n" );

            LogObj.info("List of movie shows has been shown.");
            return sBuilder.toString().replace(movieName + "<>", "");
        }else{
            return "No movie slot found for the movie " + movieName;
        }
    }

    @Override
    public String getBookingSchedule(String customerID) {
        StringBuilder sBuilder = new StringBuilder();
        if (this.user_data.containsKey(customerID)){
            for (String x: this.user_data.keySet()){
                if (x.equals(customerID)){
                    for (String InnerKey: this.user_data.get(x).keySet()){
                        sBuilder.append(InnerKey).append(":").append(this.user_data.get(x).get(InnerKey)).append("\n");
                    }
                }
            }
        }
        // method + "<>" + movie_name + "<>" + movie_id + "<>" + customer_id + "<>" + tickets
        String data = this.UDPcall("booking_schedule" + "<>" + null + "<>" + null + "<>" + customerID + "<>" + 0);
        return sBuilder.toString() + data;    }
    public String booking_schedule(String customerID){

        StringBuilder sb = new StringBuilder();
        for (String OuterKey : this.user_data.keySet()) {
            System.out.println(customerID + " OuterKey---->> " + OuterKey);
            if(OuterKey.equals(customerID)){
                for (String InnerKey : this.user_data.get(OuterKey).keySet()){
                    sb.append(InnerKey).append(":").append(this.user_data.get(OuterKey).get(InnerKey)).append("\n");
                }
            }
        }
        return  sb.toString();
    }

    public String UDPcall(String methodsList) {

        StringBuilder sb = new StringBuilder();
        if(this.server_name.equals("ATW")) {
            sb.append(sending_message(methodsList, "OUT", 8003));
            sb.append(sending_message(methodsList, "VER", 8002));
        }else if(this.server_name.equals("VER")){
            sb.append(sending_message(methodsList, "OUT", 8003));
            sb.append(sending_message(methodsList, "ATW", 8001));

        }else if(this.server_name.equals("OUT")){
            sb.append(sending_message(methodsList, "ATW", 8001));
            sb.append(sending_message(methodsList, "VER", 8002));
        }
        return sb.toString();
    }

    public String list_movie(String movie_name){
        StringBuilder sb = new StringBuilder();
        for (String OuterKey : this.datastorage.keySet()) {
            System.out.println(OuterKey);
            if(OuterKey.equals(movie_name)){
                for (String InnerKey : this.datastorage.get(OuterKey).keySet()){
                    sb.append(OuterKey).append("<>").append(InnerKey).append("<>").append(this.datastorage.get(OuterKey).get(InnerKey)).append("\n");
                }
            }
        }
        return  sb.toString();
    }

    public String sending_message(String method_name , String server_name, Integer PortNumber) {
        // args give message contents and destination hostname


        DatagramSocket datasocket = null;
        try{
            System.out.println("server is--->>" + server_name + " \nport-number" + PortNumber);
            datasocket = new DatagramSocket();
            byte[] arguments = method_name.getBytes();
            InetAddress host_name = InetAddress.getLocalHost();

            DatagramPacket request = new DatagramPacket(arguments, method_name.length(), host_name, PortNumber);
            datasocket.send(request);

            byte[] response = new byte[1024];
            DatagramPacket reply = new DatagramPacket(response, response.length);
            datasocket.receive(reply);
            String data = new String(response, 0, reply.getLength());
            // String [] splitted = data.split("<>");

            System.out.println("Here is the data you recieved...!! " + (data).toString());
            return data;
        }catch(SocketException e){ System.out.println("Something went wrong with SKT: " + e.getMessage());
        }catch(IOException e){System.out.println("Something went wrong in IO: " + e.getMessage());
        }finally{if(datasocket != null){datasocket.close();}
        }
        return "Udp connection not worked..!!";
    }
}
