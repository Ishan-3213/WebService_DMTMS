package Client;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.*;
import InterfaceOperationApp.InterfaceOperations;
import Logs.Log;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class Client {
    static String user_id;
//    static Integer WebServicePortNumber;
    public static void main(String[] args) {
        Log logger;
        try (Scanner read = new Scanner(System.in)){
            System.out.println("\nPlease enter your UserID: ");
            user_id = (read.nextLine()).toUpperCase();
            while(user_id.length()!=8 ){
                System.out.println("\nPlease enter valid UserId! Enter it again.");
                user_id = (read.nextLine()).toUpperCase();
            }
            while(true){
                if (user_id.startsWith("ATW")){
                    break;
                }else if (user_id.startsWith("VER")){
                    break;
                } else if (user_id.startsWith("OUT")) {
                    break;
                }else {
                    System.out.println("\nPlease enter valid UserId !!");
                    user_id = (read.nextLine()).toUpperCase();
                }
            }
            Logger LogObj = Logger.getLogger(user_id.substring(0,3));
            logger = new Log(user_id, true, false);
            LogObj = logger.attachFileHandlerToLogger(LogObj);

            URL url = new URL("http://localhost:8080/" + user_id.substring(0,3)+ "?wsdl");
            QName qName = new QName("http://Implementation/", "ImplementationOperationsService");
            Service service = Service.create(url, qName);
            InterfaceOperations intOpr = service.getPort(InterfaceOperations.class);

            AvailableOptions(intOpr, user_id, LogObj);

        }catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }
    public static void AvailableOptions(InterfaceOperations intOpr, String user_id, Logger LogObj) {
        String user_choice;
        boolean is_admin;
        String movieName;
        String movieID;
        int capacity;
        boolean login = true;
        boolean choice;
        try (Scanner read = new Scanner(System.in)){
            while(login){

                is_admin = user_id.substring(0, 4).endsWith("A") && (!user_id.substring(0, 4).endsWith("C"));
                choice = true;
                // Customer Options CLI
                if(!is_admin){
                    LogObj.info("Client has been logged with ID: " + user_id);
                    System.out.println();
                    System.out.println("\t Hey there Customer - " + user_id);
                    while (choice) {
                        System.out.println("\nSelect the choice given below: ");
                        System.out.println();
                        System.out.println("1. Book movie tickets.");
                        System.out.println("2. List your booked movie tickets.");
                        System.out.println("3. Cancel movie tickets. ");
                        System.out.println("4. Exchange the movie tickets. ");
                        System.out.println("5. Exit.");
                        user_choice = read.nextLine();
                            try{
                            }catch (NullPointerException e){
                                System.out.println("Please enter valid number..!!");
                            }

                       switch (user_choice) {
                            case "1":
                                LogObj.info(user_id + " wants to book movie.");
                                System.out.println("Enter movie name you want to add from the option.");
                                System.out.println("AVATAR \t AVENGER \t TITANIC");
                                movieName = (read.nextLine()).toUpperCase();
                                if (movieName.isEmpty()){
                                    System.out.println("Please enter valid movie-Name\n");
                                    LogObj.info("Please enter valid movie-Name\n");
                                    break;
                                }
                                String movie_shows =intOpr.listMovieShowsAvailability(movieName);
                                if(movie_shows.startsWith("No")){
                                    System.out.println("Sorry there is no show available for " + movieName);
                                    LogObj.info("Sorry there is no show available for " + movieName);
                                    break;
                                }
                                else{
                                    System.out.println();
                                    System.out.println("Here is the movie shows available for the "+movieName);
                                    System.out.println(movie_shows.replace("<>", "-"));
                                }
                                System.out.println();
                                System.out.println("Enter the movieId you want to book.");
                                movieID = (read.nextLine()).toUpperCase();
                                if (!movieID.isEmpty() && movieID.length() < 11){
                                    System.out.println("Please enter valid movie-ID\n");
                                    LogObj.info(user_id + " User has entered invalid movie-ID\n");
                                    break;
                                }
                                long days = DaysLeft(movieID);
                                if (days>7 | days<1) {
                                    System.out.println("Can not book tickets later than 1 week/before today.\n");
                                    LogObj.info("Can not book tickets later than 1 week/before today.\n");
                                    break;
                                }
                                System.out.println("Please enter number of tickets for the movie " + movieName + "-" +movieID);
                                try {
                                    capacity = Integer.parseInt(read.nextLine());
                                    String data = intOpr.bookMovieTickets(user_id, movieID, movieName, capacity);
                                    System.out.println(data);
                                    LogObj.info(data);
                                    break;
                                }catch (NumberFormatException e){System.out.println("Please enter valid number!");}
                                break;
                            case "2":
                                System.out.println();
                                String booking_schedule = intOpr.getBookingSchedule(user_id);
                                if (booking_schedule.isEmpty()){
                                    System.out.println("There is no booked movie tickets found with the ID - " + user_id);
                                }else{
                                    System.out.println("Here is your booking schedule..!!\n");
                                    System.out.println(booking_schedule);
                                    LogObj.info(user_id + "'s booking schedule: \n" + booking_schedule);
                                }
                                break;
                            case "3":
                                System.out.println("\nPlease Enter UserId again: ");
                                String userId_cancel = (read.nextLine()).toUpperCase();

                                while(userId_cancel.length() > 8){
                                    System.out.println("Please enter valid UserId !!");
                                    userId_cancel = (read.nextLine()).toUpperCase();
                                }
                                while(!user_id.equals(userId_cancel)){
                                    System.out.println("Unauthorized..!! You are not logged in with the given userID!!");
                                    // userId_cancel = (read.nextLine()).toUpperCase();
                                    LogObj.info(user_id + " has no booked movie tickets.");
                                }
                                String booked_movie =intOpr.getBookingSchedule(user_id);
                                if(booked_movie.isEmpty()){
                                    System.out.println("There is no booked movie tickets found with the ID -" + user_id);
                                    LogObj.info(user_id + " has no booked movie tickets.");
                                    break;
                                }
                                else{
                                    System.out.println("\nHere is the booked shows with the userID - "+user_id);
                                    System.out.println(booked_movie + "\n");
                                }
                                System.out.println("Enter movie name you want to cancel from the option.");
                                System.out.println("AVATAR \t AVENGER \t TITANIC");
                                movieName = (read.nextLine()).toUpperCase();
                                if (!booked_movie.contains(movieName)){
                                    System.out.println("You have no show booked for the movie "+ movieName );
                                    LogObj.info(user_id + " has no booked for the movie "+ movieName);
                                    break;
                                }
                                System.out.println();
                                System.out.println("Enter the movieId you want to cancel.");
                                movieID = (read.nextLine()).toUpperCase();
                                if (!booked_movie.contains(movieID) | movieID.length()<11){
                                    System.out.println("You have no show booked for the movieID "+ movieID );
                                    LogObj.info(user_id + " has no booked for the movieID "+ movieID);
                                    break;
                                }
                                long days_check = DaysLeft(movieID);
                                if (days_check<1) {
                                    System.out.println("Can not cancel tickets before today.\n");
                                    LogObj.info("Can not cancel tickets before today.\n");
                                    break;
                                }
                                System.out.println("\nPlease enter number of tickets for the movie " + movieName + "-" +movieID);
                                capacity = Integer.parseInt(read.nextLine());
                                String reply = intOpr.cancelMovieTickets(user_id, movieID, movieName, capacity);
                                System.out.println(reply);
                                LogObj.info(reply);
                                break;
                            case "4":
                                String movie_booked =intOpr.getBookingSchedule(user_id);
                                if(movie_booked.isEmpty()){
                                    System.out.println("There is no booked movie tickets found with the ID -" + user_id);
                                    LogObj.info(user_id + " has no booked movie tickets.");
                                    break;
                                }
                                else{
                                    System.out.println("\nHere is the booked shows with the userID - "+user_id);
                                    System.out.println(movie_booked + "\n");
                                }
                                System.out.println("Enter movie name you want to exchange.");
                                movieName = (read.nextLine()).toUpperCase();
                                if (!movie_booked.contains(movieName) | movieName.isEmpty()){
                                    System.out.println("You have no show booked for the movie "+ movieName );
                                    LogObj.info(user_id + " has no booked for the movie "+ movieName);
                                    break;
                                }
                                System.out.println("\nEnter the movieId you want to exchange.");
                                movieID = (read.nextLine()).toUpperCase();
                                if (!movie_booked.contains(movieID) | movieID.isEmpty() | movieID.length()<11){
                                    System.out.println("You have no show booked with the movieID "+ movieID );
                                    LogObj.info(user_id + " has no booked for the movieID "+ movieID);
                                    break;
                                }
                                System.out.println("Enter movie name you want to exchange with.");
                                System.out.println("AVATAR \t AVENGER \t TITANIC");
                                String New_movieName = (read.nextLine()).toUpperCase();
                                if (New_movieName.isEmpty()){
                                    System.out.println("Invalid movie-Name\n");
                                    LogObj.info("Invalid movie-Name\n");
                                    break;
                                }
                                String new_movie_shows =intOpr.listMovieShowsAvailability(New_movieName);
                                if(new_movie_shows.isEmpty()){
                                    System.out.println("Sorry there is no show available for " + New_movieName);
                                    LogObj.info("Sorry there is no show available for " + New_movieName);
                                    break;
                                }
                                else{
                                    System.out.println();
                                    System.out.println("Here is the movie shows available for the "+New_movieName);
                                    System.out.println(new_movie_shows.replace("<>", "-"));
                                }
                                System.out.println("\nBooked-Movie: " + movieName + "-"+movieID);
                                System.out.println("\nEnter the movieId you want to exchange tickets with.");
                                String new_movieID = (read.nextLine()).toUpperCase();
                                if (new_movieID.isEmpty() | new_movieID.length()<11){
                                    System.out.println("Invalid movie-ID!\n");
                                    LogObj.info(user_id + " User has entered invalid movie-ID\n");
                                    break;
                                }
                                long day = DaysLeft(new_movieID);
                                if (day>7 | day<1) {
                                    System.out.println("Can not book tickets later than 1 week/before today.\n");
                                    LogObj.info("Can not book tickets later than 1 week/before today.\n");
                                    break;
                                }
                                System.out.println("Please enter number of tickets for the movie " + movieName + "-" +new_movieID);
                                int Capacity = Integer.parseInt(read.nextLine());
//                                int Capacity = 0;
                                String exchange_movie = intOpr.exchangeMovieTickets(user_id, movieID, movieName, new_movieID, New_movieName, Capacity);
                                System.out.println(exchange_movie);
                                LogObj.info(exchange_movie);
                                break;
                            case "5":
                                System.out.println("Logging Out...!");
                                choice = false;
                                login = false;
                                break;
                            default:
                                System.out.println("Invalid Choice..!!");
                                System.out.println();
                                break;
                        }
                    }
                }

                // Admin Options CLI
                else if(is_admin){
                    LogObj.info("Admin has logged in with Id: " + user_id);
                    System.out.println();
                    System.out.println("\n---------------\tHey there Admin("+user_id+") ---------------");
                    while (choice) {
                        System.out.println("\n*******\tSelect the choice given below\t*******");
                        System.out.println();
                        // String region = user_id.substring(0, 3);
                        System.out.println("1. Add movie slots.");
                        System.out.println("2. Remove movie slots.");
                        System.out.println("3. List out movie shows available.");
                        System.out.println("4. Book movie tickets.");
                        System.out.println("5. List your booked movie tickets.");
                        System.out.println("6. Exchange the movie Tickets.");
                        System.out.println("7. Cancel movie tickets. ");
                        System.out.println("8. Exit.");
//                        user_choice = Integer.parseInt(read.nextLine());
                        user_choice = read.nextLine();
                        switch (user_choice) {
                            case "1":
                                LogObj.info("Admin wants to add movie slots.");
                                System.out.println("Enter movie name you want to add from the option.");
                                System.out.println("AVATAR \t AVENGER \t TITANIC");
                                movieName = (read.nextLine()).toUpperCase();
                                if (movieName.isEmpty()){
                                    System.out.println("Please enter valid movie-Name\n");
                                    break;
                                }
                                System.out.println();
                                System.out.println("Enter movieId for the movie - " + movieName);
                                movieID = (read.nextLine()).toUpperCase();
                                if (movieID.isEmpty() | movieID.length()<11){
                                    System.out.println("Invalid movie-ID!\n");
                                    LogObj.info(user_id + " User has entered invalid movie-ID\n");
                                    break;
                                }
                                if(movieID.substring(0,3).equals(user_id.substring(0,3))){
                                    while(movieID.length()<11){
                                        System.out.println("\nPlease enter valid movie details..!!");
                                        System.out.println();
                                        System.out.println("Enter movie name you want to add from the option.");
                                        System.out.println("AVATAR\nAVENGER\nTITANIC");
                                        movieName = (read.nextLine()).toUpperCase();
                                        System.out.println();
                                        System.out.println("Enter movieId for the movie - " + movieName + " ");
                                        movieID = (read.nextLine()).toUpperCase();
                                    }
                                    long days = DaysLeft(movieID);
                                    if (days>7 | days<1) {
                                        System.out.println("Can not add slots later than 1 week/before today/today.");
                                        break;
                                    }
                                    System.out.println();
                                    System.out.println("Enter capacity for the Movie: " + movieName + " with the MovieId: "+ movieID);
                                    capacity = Integer.parseInt(read.nextLine());
                                    String response = intOpr.addMovieSlots(movieID, movieName, capacity);
                                    System.out.println(response);
                                    LogObj.info(response);
                                    break;
                                }else{
                                    System.out.println("You have no permission to add movies in region! " + movieID.substring(0,3));
                                    LogObj.info("You have no permission to add movies in region! " + movieID.substring(0,3));
                                    break;
                                }

                            case "2":
                                System.out.println("Enter movie name you want to remove from the option.");
                                System.out.println("AVATAR \t AVENGER \t TITANIC");
                                movieName = (read.nextLine()).toUpperCase();
                                System.out.println();
                                System.out.println("Enter movieId for the movie - " + movieName);
                                movieID = (read.nextLine()).toUpperCase();
                                if (movieID.isEmpty() | movieID.length()<11){
                                    System.out.println("Invalid movie-ID!\n");
                                    LogObj.info(user_id + " User has entered invalid movie-ID\n");
                                    break;
                                }
                                if(movieID.substring(0,3).equals(user_id.substring(0,3))){

                                }else{
                                System.out.println("You have no permission to remove movies in region! " + movieID.substring(0,3));
                                LogObj.info("You have no permission to remove movies in region! " + movieID.substring(0,3));
                                break;
                                }
                                long remove_day_check = DaysLeft(movieID);
                                if (remove_day_check>7 | remove_day_check<0) {
                                    System.out.println("Can not remove slots later than 1 months/before today.");
                                    break;
                                }
                                while(movieID.length() < 11 | movieName.isEmpty()){
                                    System.out.println("Please enter valid movie details..!!");
                                    System.out.println();
                                    System.out.println("Enter movie name you want to add from the option.");
                                    System.out.println("AVATAR \t AVENGER \t TITANIC");
                                    movieName = (read.nextLine()).toUpperCase();
                                    System.out.println();
                                    System.out.println("Enter movieId for the movie - " + movieName);
                                    movieID = (read.nextLine()).toUpperCase();
                                }
                                String data = intOpr.removeMovieSlots(movieID, movieName);
                                System.out.println(data);
                                LogObj.info(data);
                                break;
                            case "3":
                                LogObj.info("Admin wants to list movie shows.");
                                System.out.println();
                                System.out.println("Enter movie name you want to add from the option.");
                                System.out.println("\nAVATAR\nAVENGER\nTITANIC");
                                movieName = (read.nextLine()).toUpperCase();
                                while(movieName.isEmpty()){
                                    System.out.println();
                                    System.out.println("Enter movie name you want to add from the option.");
                                    System.out.println("AVATAR \t AVENGER \t TITANIC");
                                    movieName = (read.nextLine()).toUpperCase();
                                }
                                String movie_shows = intOpr.listMovieShowsAvailability(movieName);
                                if(movie_shows.isEmpty() | movie_shows.toUpperCase().startsWith("NO")){
                                    System.out.println();
                                    System.out.println("There is no show available for-> " + movieName + "\n");
                                    LogObj.info("Sorry there is no show available for-> " + movieName);
                                }
                                else{
                                    System.out.println();
                                    System.out.println("Here is the movie shows available for the "+movieName);
                                    System.out.println(movie_shows.replace("<>", "-"));
                                    LogObj.info(movie_shows.replace("<>", "-"));
                                }
                                break;
                            case "4":
                                System.out.println();
                                LogObj.info(user_id + " wants to book movie.");
                                System.out.println("Enter movie name you want to add from the option.");
                                System.out.println("AVATAR \t AVENGER \t TITANIC");
                                movieName = (read.nextLine()).toUpperCase();
                                String available_movie_shows =intOpr.listMovieShowsAvailability(movieName);
                                if(available_movie_shows.isEmpty()){
                                    System.out.println("Sorry there is no show available for " + movieName);
                                    break;
                                }
                                else{
                                    System.out.println();
                                    System.out.println("Here is the movie shows available for the "+movieName);
                                    System.out.println(available_movie_shows.replace("<>", "-"));
                                }
                                System.out.println();
                                System.out.println("Enter the movieId you want to book.");
                                movieID = (read.nextLine()).toUpperCase();
                                if (movieID.isEmpty() | movieID.length()<11){
                                    System.out.println("Invalid movie-ID!\n");
                                    LogObj.info(user_id + " User has entered invalid movie-ID\n");
                                    break;
                                }
                                long days_check = DaysLeft(movieID);
                                if (days_check>7 | days_check<1) {
                                    System.out.println("Can not book tickets later than 1 week.");
                                    break;
                                }
                                System.out.println();
                                System.out.println("Please enter number of tickets for the movie " + movieName + "-" +movieID);
                                capacity = Integer.parseInt(read.nextLine());
                                String received_data = intOpr.bookMovieTickets(user_id, movieID, movieName, capacity);
                                System.out.println(received_data);
                                LogObj.info(received_data);
                                break;
                            case "5":
                                System.out.println();
                                System.out.println("Enter UserId: ");
                                String userId_booking = (read.nextLine()).toUpperCase();
                                while(userId_booking.length()>8){
                                    System.out.println("Please enter valid UserId !!");
                                    userId_booking = (read.nextLine()).toUpperCase();
                                }
                                String booking_schedule = intOpr.getBookingSchedule(userId_booking);
                                if (booking_schedule.isEmpty()){
                                    System.out.println("There is no booked movie tickets found with the ID - " + userId_booking);
                                }else{
                                    System.out.println("Here is your booking schedule..!!");
                                    System.out.println(booking_schedule);
                                }
                                break;
                            case "6":
                                String movie_booked =intOpr.getBookingSchedule(user_id);
                                if(movie_booked.isEmpty()){
                                    System.out.println("There is no booked movie tickets found with the ID -" + user_id);
                                    LogObj.info(user_id + " has no booked movie tickets.");
                                    break;
                                }
                                else{
                                    System.out.println("\nHere is the booked shows with the userID - "+user_id);
                                    System.out.println(movie_booked + "\n");
                                }
                                System.out.println("Enter movie name you want to exchange.");
                                movieName = (read.nextLine()).toUpperCase();
                                if (!movie_booked.contains(movieName) | movieName.isEmpty()){
                                    System.out.println("You have no show booked for the movie "+ movieName );
                                    LogObj.info(user_id + " has no booked for the movie "+ movieName);
                                    break;
                                }
                                System.out.println("\nEnter the movieId you want to exchange.");
                                movieID = (read.nextLine()).toUpperCase();
                                if (!movie_booked.contains(movieID) | movieID.isEmpty()){
                                    System.out.println("You have no show booked with the movieID "+ movieID );
                                    LogObj.info(user_id + " has no booked for the movieID "+ movieID);
                                    break;
                                }
                                System.out.println("Enter movie name you want to exchange with.");
                                System.out.println("AVATAR \t AVENGER \t TITANIC");
                                String New_movieName = (read.nextLine()).toUpperCase();
                                if (New_movieName.isEmpty()){
                                    System.out.println("Invalid movie-Name\n");
                                    LogObj.info("Invalid movie-Name\n");
                                    break;
                                }
                                String new_movie_shows =intOpr.listMovieShowsAvailability(New_movieName);
                                if(new_movie_shows.isEmpty()){
                                    System.out.println("Sorry there is no show available for " + New_movieName);
                                    LogObj.info("Sorry there is no show available for " + New_movieName);
                                    break;
                                }
                                else{
                                    System.out.println();
                                    System.out.println("Here is the movie shows available for the "+New_movieName);
                                    System.out.println(new_movie_shows.replace("<>", "-"));
                                }
                                System.out.println("\nOld-Movie: " + movieName + "-"+movieID);
                                System.out.println("\nEnter the movieId you want to exchange tickets with.");
                                String new_movieID = (read.nextLine()).toUpperCase();
                                if (new_movieID.isEmpty() | new_movieID.length()<12){
                                    System.out.println("Invalid movie-ID!\n");
                                    LogObj.info(user_id + " User has entered invalid movie-ID\n");
                                    break;
                                }
                                long day = DaysLeft(new_movieID);
                                if (day>7 | day<1) {
                                    System.out.println("Can not book tickets later than 1 week/before today.\n");
                                    LogObj.info("Can not book tickets later than 1 week/before today.\n");
                                    break;
                                }
                                System.out.println("Please enter number of tickets for the movie " + movieName + "-" +new_movieID);
                                int Capacity = Integer.parseInt(read.nextLine());
                                String exchange_movie = intOpr.exchangeMovieTickets(user_id, movieID, movieName, new_movieID, New_movieName, Capacity);
                                System.out.println(exchange_movie);
                                LogObj.info(exchange_movie);
                                break;
                            case "7":
                                System.out.println("\nPlease Enter UserId again: ");
                                String userId_cancel = (read.nextLine()).toUpperCase();
                                boolean check_data = true;
                                while(userId_cancel.length() > 8 | !user_id.equals(userId_cancel)){
                                    System.out.println("Unauthorized ... !! Please enter valid UserId !!");
                                    LogObj.info(user_id + " has no authorization.");
                                    check_data = false;
                                    break;
                                }
                                if (!check_data)break;
                                String booked_movie =intOpr.getBookingSchedule(user_id);
                                if(booked_movie.isEmpty()){
                                    System.out.println("There is no booked movie tickets found with the ID -" + user_id);
                                    LogObj.info(user_id + " has no booked movie tickets.");
                                    break;
                                }
                                else{
                                    System.out.println("\nHere is the booked shows with the userID - "+user_id);
                                    System.out.println(booked_movie + "\n");
                                }
                                System.out.println("Enter movie name you want to cancel from the option.");
                                System.out.println("AVATAR \t AVENGER \t TITANIC");
                                movieName = (read.nextLine()).toUpperCase();
                                if (!booked_movie.contains(movieName)){
                                    System.out.println("You have no show booked for the movie "+ movieName );
                                    LogObj.info(user_id + " has no booked for the movie "+ movieName);
                                    break;
                                }
                                System.out.println();
                                System.out.println("Enter the movieId you want to cancel.");
                                movieID = (read.nextLine()).toUpperCase();
                                if (!booked_movie.contains(movieID) | movieID.length()<11){
                                    System.out.println("You have no show booked for the movieID "+ movieID );
                                    LogObj.info(user_id + " has no booked for the movieID "+ movieID);
                                    break;
                                }
                                long days_chck = DaysLeft(movieID);
                                if (days_chck<1) {
                                    System.out.println("Can not cancel tickets before today.\n");
                                    LogObj.info("Can not cancel tickets before today.\n");
                                    break;
                                }
                                System.out.println("\nPlease enter number of tickets for the movie " + movieName + "-" +movieID);
                                capacity = Integer.parseInt(read.nextLine());
                                String reply = intOpr.cancelMovieTickets(user_id, movieID, movieName, capacity);
                                System.out.println(reply);
                                LogObj.info(reply);
                                break;
                            case "8":
                                System.out.println("Logging out from the - " + user_id);
                                choice = false;
                                login = false;
                                break;
                            default:
                                System.out.println();
                                System.out.println("!!..Invalid Choice..!!");
                                break;
                        }
                    }
                }
            }
        }
    }

    private static long DaysLeft(String MovieID){

        String movieID = MovieID.substring(4,12);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDate movie_date = LocalDate.parse(movieID, formatter);
        LocalDate currentDate = LocalDate.now();
        long days = ChronoUnit.DAYS.between(currentDate, movie_date);
        return days;
    }
}
