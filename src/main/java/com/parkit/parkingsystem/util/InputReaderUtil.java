/*
  Mov'it Group - Park'it app  OCR Project 5 initiative

  Start the Interactive Shell with 3 option, parking entry, exit with ticket or shutdown

  @previous_author undocumented
 * @author  Sébastien Vigé
 * @version 1.0
 * @since   2020-12-28
 */


package com.parkit.parkingsystem.util;

// svige replace Log4j support by sl4j
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.NoSuchElementException;
import java.util.Scanner;

public class InputReaderUtil {

    private static Scanner scan = new Scanner(System.in);
    private static final Logger logger = LoggerFactory.getLogger("InputReaderUtil");

    public int readSelection()  throws NoSuchElementException{
        int input = 4;
        try {
            input = Integer.parseInt(scan.nextLine());


        } catch (NumberFormatException e) {
            logger.error("You entered an empty string or a non-numeric character, please enter a valid number");

            return input;
        }
        return input;
    }

    public String readVehicleRegistrationNumber()  {

            String vehicleRegNumber= scan.nextLine();
            if(vehicleRegNumber == null || vehicleRegNumber.trim().length()==0) {
                scan.nextLine();
            }
            return vehicleRegNumber;

    }


}
