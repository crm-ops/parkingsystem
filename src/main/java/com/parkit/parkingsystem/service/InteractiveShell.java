/*
  Mov'it Group - Park'it app  OCR Project 5 initiative

  Start the Interactive Shell with 3 option, parking entry, exit with ticket or shutdown

  @previous_author undocumented
 * @author  Sébastien Vigé
 * @version 1.0
 * @since   2020-12-28
 */

package com.parkit.parkingsystem.service;


import com.parkit.parkingsystem.customExceptions.ParkingExitWithoutValidTicketException;
import com.parkit.parkingsystem.customExceptions.SecondParkingEnteringAttemptWithSamePlate;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.NoSuchElementException;

public class InteractiveShell {

    private static final Logger logger = LoggerFactory.getLogger("InteractiveShell");

    public static void loadInterface() {
        try {
            logger.info("App initialized!!!");
            System.out.println("Welcome to Parking System!");

            boolean continueApp = true;
            InputReaderUtil inputReaderUtil = new InputReaderUtil();
            ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
            TicketDAO ticketDAO = new TicketDAO();
            ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

            while (continueApp) {
                loadMenu();
                int option = inputReaderUtil.readSelection();
                switch (option) {
                    case 1: {
                        logger.debug("option 1 selected - processing incoming vehicule");
                        parkingService.processIncomingVehicle();
                        break;
                    }
                    case 2: {

                        logger.debug("option 1 selected - processing incoming vehicule");
                        parkingService.processExitingVehicle();
                        break;
                    }
                    case 3: {
                        System.out.println("Exiting from the system!");
                        continueApp = false;
                        break;
                    }
                    default:
                        System.out.println("Unsupported option. Please enter a number corresponding to the provided menu");
                }
            }

        } catch (NumberFormatException e) {
            logger.info("Please input numbers corresponding to menu option in all menus");
            logger.info("Reloading interface");
            loadInterface();
        } catch (NullPointerException e) {
            logger.info("You entered an empty plate #");
            logger.info("Please renew your demand");
            loadInterface();
        }catch(NoSuchElementException  e) {
            logger.error("You pressed keys CTRL-D or COMMAND-D");
            logger.error("This kills the console text scanner");
            logger.error("Exiting");
            logger.error("Please restart the app");
        }catch(ParkingExitWithoutValidTicketException e){
            logger.info("You attempt to exit the parking with no valid active ticket - Please contact parking office urgently");
            loadInterface();
        }catch(SecondParkingEnteringAttemptWithSamePlate e){
            logger.info("A vehicule with your plate already holds an active ticket - FRAUD IN PROGRESS - Please contact parking office urgently");
            loadInterface();
        }

    }

    private static void loadMenu(){
        System.out.println("Please select an option. Simply enter the number to choose an action");
        System.out.println("1 New Vehicle Entering - Allocate Parking Space");
        System.out.println("2 Vehicle Exiting - Generate Ticket Price");
        System.out.println("3 Shutdown System");
    }

}
