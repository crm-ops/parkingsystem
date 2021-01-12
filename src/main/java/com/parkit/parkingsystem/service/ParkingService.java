package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.customExceptions.ParkingExitWithoutValidTicketException;
import com.parkit.parkingsystem.customExceptions.SecondParkingEnteringAttemptWithSamePlate;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


import java.io.IOException;
import java.util.Date;
import java.util.function.BooleanSupplier;

public class ParkingService {

    private static final Logger logger = LoggerFactory.getLogger("ParkingService");

    private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

    private InputReaderUtil inputReaderUtil;
    private ParkingSpotDAO parkingSpotDAO;
    private  TicketDAO ticketDAO;

    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO){
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
    }

    public void processIncomingVehicle() throws NumberFormatException , IllegalArgumentException {


            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if(parkingSpot !=null && parkingSpot.getId() > 0) {
                String vehicleRegNumber = getVehichleRegNumber();
                //test if incoming plate has already a valid ticket
                //retrieve last ticket for plate
                int activeTicketCount = ticketDAO.getActiveTicketCount(vehicleRegNumber);
                if (activeTicketCount >0) {
                    logger.info("Your vehicule is already parked - please park another vehicule");
                    throw new SecondParkingEnteringAttemptWithSamePlate("FRAUD ATTEMPT - DETECTED");
                }

                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);//allot this parking space and mark it's availability as false

                Date inTime = new Date();
                Ticket ticket = new Ticket();
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                //ticket.setId(ticketID);
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setActive(true);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                ticketDAO.saveTicket(ticket);
                System.out.println("Generated Ticket and saved in DB");
                System.out.println("Please park your vehicle in spot number:"+parkingSpot.getId());
                System.out.println("Recorded in-time for vehicle number:"+vehicleRegNumber+" is:"+inTime);
            }


        }


    private String getVehichleRegNumber() throws NullPointerException {
        System.out.println("Please type the vehicle registration number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    //sébastien vige OCR P5 - added throws exception
    public ParkingSpot getNextParkingNumberIfAvailable() throws NumberFormatException {
        int parkingNumber=0;
        ParkingSpot parkingSpot = null;

            ParkingType parkingType = getVehichleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if(parkingNumber > 0){
                parkingSpot = new ParkingSpot(parkingNumber,parkingType, true);

            } else {
                logger.info("No space available for your vehicule type, please come back later");

            }


        return parkingSpot;

    }

    //set protected instead of private access for testing
    public ParkingType getVehichleType() throws NumberFormatException{
        System.out.println("Please select vehicle type from menu");
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch(input){
            case 1: {
                return ParkingType.CAR;
            }
            case 2: {
                return ParkingType.BIKE;
            }
            default: {
                System.out.println("Incorrect input provided");
                throw new NumberFormatException("Entered input is invalid");
                //to test send 3
            }
        }
    }

    public void processExitingVehicle()  throws IllegalArgumentException, NullPointerException {

                String vehicleRegNumber = getVehichleRegNumber();
                //is there an active ticket for the provided plate ?
                int activeTicket = ticketDAO.getActiveTicketCount(vehicleRegNumber);
                if (activeTicket>0) {
                    logger.info("You have a valid ticket - calculating fare ...");
                } else {
                    throw new ParkingExitWithoutValidTicketException("No valid active parking ticket");
                }
                Boolean returningVehicule = ticketDAO.getHasPastTicket(vehicleRegNumber);
                Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
                Date outTime = new Date();
                ticket.setOutTime(outTime);
                ticket.setReturningVehicule(returningVehicule);
                ticket.setActive(false);
                fareCalculatorService.calculateFare(ticket);
                if (ticketDAO.updateTicket(ticket)) {
                    ParkingSpot parkingSpot = ticket.getParkingSpot();
                    parkingSpot.setAvailable(true);
                    parkingSpotDAO.updateParking(parkingSpot);
                    System.out.println("Please pay the parking fare:" + ticket.getPrice());
                    System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
                }



    }





}
