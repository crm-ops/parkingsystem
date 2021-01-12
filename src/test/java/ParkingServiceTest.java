import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.customExceptions.ParkingExitWithoutValidTicketException;
import com.parkit.parkingsystem.customExceptions.SecondParkingEnteringAttemptWithSamePlate;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;




@ExtendWith(MockitoExtension.class) //to use to get the test coverage results with mvn site
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;


    // TESTS SECTION

    @Test
    public void processExitingVehicleTest(){

            //ARRANGE
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            ticket.setActive(true);

            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
            when(ticketDAO.getActiveTicketCount("ABCDEF")).thenReturn(1);
            when(ticketDAO.updateTicket(ticket)).thenReturn(true);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
            //ACT
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processExitingVehicle();

            //ASSERT
            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));


    }


   @Test
    public void processIncomingVehicleTest() {


            //ARRANGE
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

            //ACT
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processIncomingVehicle();

            //ASSERT
            verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));


    }



    @Test
    public void processgetNextParkingNumberIfAvailableIllegalArgumentExceptionTest()  {


            //ARRANGE
            when(inputReaderUtil.readSelection()).thenReturn(2);
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenThrow(new IllegalArgumentException("Error parsing user input for type of vehicle"));

            //ACT
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

            //ASSERT
            assertThrows(IllegalArgumentException.class,()->parkingService.getNextParkingNumberIfAvailable());


        }


    @Test
    public void processIncomingBikeTest() {

            //ARRANGE
            when(inputReaderUtil.readSelection()).thenReturn(2);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(1);

            //ACT
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processIncomingVehicle();

            //ASSERT
            verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));

    }


    @Test
    public void processIncomingBikeTest_noParkingAvailableComeBack() {

            //ARRANGE
            when(inputReaderUtil.readSelection()).thenReturn(2);
            //return 0 parking available
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(0);

            //ACT
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

            //ASSERT
            ParkingSpot expected = null;
            assertEquals(expected, parkingService.getNextParkingNumberIfAvailable());

    }


    @Test
    public void getVehiculeTypeTest_throwsIllegalArgumentException() {

            //ARRANGE
            when(inputReaderUtil.readSelection()).thenReturn(3);

            //ACT
            ParkingService service = new ParkingService(inputReaderUtil, parkingSpotDAO,ticketDAO);

            //ASSERT
            ParkingType expected = ParkingType.BIKE;
            assertThrows(IllegalArgumentException.class, ()-> service.getVehichleType());

    }

    @Test
    public void processExitingVehiculeTest_emptyplate_throwsNullException()  {

            //ARRANGE -- null parkingspot - no input to code
            //ACT
            ParkingService svc = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            //ASSERT
            assertThrows(ParkingExitWithoutValidTicketException.class, ()->svc.processExitingVehicle()) ;


    }


    @Test
    public void processIncomingVehiculeTestWithExistingActiveTicketThrowException()  {


        //ARRANGE
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(1);
        when(ticketDAO.getActiveTicketCount("ABCDEF")).thenReturn(1);

        //ACT
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);


        //ASSERT
        assertThrows(SecondParkingEnteringAttemptWithSamePlate.class,()->parkingService.processIncomingVehicle());


    }



}
