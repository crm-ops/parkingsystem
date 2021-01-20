/*
  Mov'it Group - Park'it app  OCR Project 5 initiative

  Start the Interactive Shell with 3 option, parking entry, exit with ticket or shutdown

  @previous_author undocumented
 * @author  Sébastien Vigé
 * @version 1.0
 * @since   2020-12-28
 */

package com.parkit.parkingsystem.config;

import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataBaseConfig {

    private static final Logger logger = LoggerFactory.getLogger("DataBaseConfig");

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        logger.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/prod","root","rootroot");
    }

    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
                logger.info("Closing DB connection");
            } catch (SQLException e) {
                logger.error("Error while closing connection",e);
            }
        }
    }

    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (SQLException e) {
                logger.error("Error while closing prepared statement",e);
            }
        }
    }

    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (SQLException e) {
                logger.error("Error while closing result set",e);
            }
        }
    }
}
