package com.parkit.parkingsystem;


import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;


//import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.BaseStubbing;
import org.mockito.junit.MockitoJUnitRunner;





import org.mockito.Mock;
import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
//@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
    @Mock
    private static ParkingService mockService ;






    @Test
    public void processExitingVehicleTest(){

        try {

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");


            // when(dbConf.getConnection()).thenReturn(dbTest.getConnection());
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
            when(ticketDAO.updateTicket(ticket)).thenReturn(true);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processExitingVehicle();

            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects in processExitingVehicleTest()");
        }
    }


    //
    @Test
    public void processIncomingVehicleTest() {

        try {
            when(inputReaderUtil.readSelection()).thenReturn(1);

            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");


            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);


            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processIncomingVehicle();

            verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));




        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects in processIncomingVehicleTest()");
        }
    }


    @Test(expected=Exception.class)
    public void processIncomingVehicleExceptionTest() throws Exception {

        try {

                when(ticketDAO.saveTicket(any(Ticket.class))).thenThrow(new Exception("Unable to process incoming vehicle" ));

                parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
                parkingService.processIncomingVehicle();






        } catch (Exception e) {
                e.printStackTrace();
               throw new Exception("Failed to set up test mock objects in processIncomingVehicleTest()");
            }


    }

}
