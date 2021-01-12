package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.customExceptions.ParkingExitWithoutValidTicketException;
import com.parkit.parkingsystem.customExceptions.SecondParkingEnteringAttemptWithSamePlate;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.Same;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static Ticket dummyTicket;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
        dataBasePrepareService.clearDataBaseEntries();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
      //  when(inputReaderUtil.readSelection()).thenReturn(1);


    }

    @AfterAll
    private static void tearDown(){

        dataBasePrepareService.clearDataBaseEntries();

    }

    @Test
    public void testParkingACar() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
        // sebastien VIGE ticket and parking are saved in the Test DB
        Thread.sleep(500);
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertEquals(true, ticket.getActive());
        assertNotNull( ticket.getParkingSpot());
        assertEquals(0.0,ticket.getPrice());


    }

    @Test
    public void testParkingLotExit() throws InterruptedException {
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        //testParkingACar();
        //process incoming car instead of executing testPrkingACar()
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        //added wait time 5s to avoid inTime>outTime during test
        Thread.sleep(1000);
        parkingService.processExitingVehicle();
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket.getPrice());
        assertNotNull(ticket.getInTime());
        assertNotNull(ticket.getOutTime());
        assertNotNull(ticket.getActive());


    }

    // test 2 cars entry and one out
    @Test
    public void testParking2Carin1CarOutExpects1PricedFare1ActiveTicket() throws InterruptedException {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        when(inputReaderUtil.readSelection()).thenReturn(1);

        // 2 cars enter the parking successively
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("420-CAR");
        parkingService.processIncomingVehicle();

        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("421-CAR");
        parkingService.processIncomingVehicle();

        //plate 420-CAR gets out after 1 second parking

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("420-CAR");
        Thread.sleep(1000);
        parkingService.processExitingVehicle();

        //retrieving tickets from DB with DAO
        Ticket ticket420 = ticketDAO.getTicket("420-CAR");
        Ticket ticket421 = ticketDAO.getTicket("421-CAR");

        //Asserting expected results - 1 past ticket (420) 1 active ticket(421)
        assertEquals(false,ticket420.getActive());
        assertEquals(true,ticket421.getActive());


    }

    // test the same car/plate entry with an existing open tiket --> should not allow car entry
    @Test
    public void testParkingSamePlateEntersTwice() throws InterruptedException {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        when(inputReaderUtil.readSelection()).thenReturn(1);

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("500-CAR");
        parkingService.processIncomingVehicle();
        //Same car enters again
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("500-CAR");


        assertThrows(SecondParkingEnteringAttemptWithSamePlate.class, ()-> parkingService.processIncomingVehicle());

    }



    // test unknown plate out --> generate a message
    @Test
    public void testUnknownTicketOutGenerateHandledException() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("600-CAR");

        assertThrows(ParkingExitWithoutValidTicketException.class, ()-> parkingService.processExitingVehicle());

    }


    // test 2 bikes entry and one out
    @Test
    public void testParking2Bikesin1BikeOutExpects1PricedFare1ActiveTicket() throws InterruptedException {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        when(inputReaderUtil.readSelection()).thenReturn(2);

        // 2 cars enter the parking successively
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("700-BIKE");
        parkingService.processIncomingVehicle();

        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("701-BIKE");
        parkingService.processIncomingVehicle();

        //plate 700-BIKE gets out after 1 second parking

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("700-BIKE");
        Thread.sleep(1000);
        parkingService.processExitingVehicle();

        //retrieving tickets from DB with DAO
        Ticket ticket700 = ticketDAO.getTicket("700-BIKE");
        Ticket ticket701 = ticketDAO.getTicket("701-BIKE");

        //Asserting expected results - 1 past ticket (420) 1 active ticket(421)
        assertEquals(false,ticket700.getActive());
        assertEquals(true,ticket701.getActive());


    }


    // test 1 car & 1 bike in then out
    @Test
    public void testParking1Bikein1CarInThenBothOut() throws InterruptedException {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //CAR IN
        when(inputReaderUtil.readSelection()).thenReturn(1);
        // 2 cars enter the parking successively
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("800-CAR");
        parkingService.processIncomingVehicle();

        //BIKE IN
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("800-BIKE");
        parkingService.processIncomingVehicle();

        //pause for 1 second parking
        Thread.sleep(1000);

        //exit CAR
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("800-CAR");
        parkingService.processExitingVehicle();

        //exit BIKE
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("800-BIKE");
        parkingService.processExitingVehicle();

        //ARRANGE TICKETS
        //retrieving tickets from DB with DAO
        Ticket ticket420 = ticketDAO.getTicket("800-CAR");
        Ticket ticket421 = ticketDAO.getTicket("800-BIKE");

        //Asserting expected results - both tickets are inactive
        assertEquals(false,ticket420.getActive());
        assertEquals(false,ticket421.getActive());


    }



    // test 30 mins free parking for new client
            //covered in unit test
    // test 5% discount for returning client
            //covered in unit test

    //==> integration test

    //1 - test reports
    // testing parking service only - not to worry if coverage is not 100%

    //code coverage in intelliJ

    // done - joda time to impletment in th full class + test + ticket + factorise (at the end)

}
