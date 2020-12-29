/*
  Mov'it Group - Park'it app  OCR Project 5 initiative

  Start the Interactive Shell with 3 option, parking entry, exit with ticket or shutdown

  @previous_author undocumented
 * @author  Sébastien Vigé
 * @version 1.0
 * @since   2020-12-28
 */



package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

  //replaced log4j logger by sl4j logger - made change in all classes calling log function

  public static   Logger logger = LoggerFactory.getLogger("App");

    public static void main(String args[]){

        logger.info("Initializing Parking System");
        InteractiveShell.loadInterface();
    }
}
