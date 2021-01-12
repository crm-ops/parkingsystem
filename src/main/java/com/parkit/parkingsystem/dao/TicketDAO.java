/*
  Mov'it Group - Park'it app  OCR Project 5 initiative

  Start the Interactive Shell with 3 option, parking entry, exit with ticket or shutdown

  @previous_author undocumented
 * @author  Sébastien Vigé
 * @version 1.0
 * @since   2020-12-28
 */
package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TicketDAO {

    private static final Logger logger = LoggerFactory.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public boolean saveTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            //ps.setInt(1,ticket.getId());
            ps.setInt(1, ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
            int val = (ticket.getActive()) ? 1 : 0;
            ps.setInt(6, val);
            return ps.execute();
        } catch (Exception ex) {
            logger.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
            return false;
        }
    }

    public Ticket getTicket(String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1, vehicleRegNumber);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4));
                ticket.setOutTime(rs.getTimestamp(5));
                Boolean val = convertIntBoolean(rs.getInt(7));
                ticket.setActive(val);

            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        } catch (Exception ex) {
            logger.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
            return ticket;
        }
    }

    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
            int val = (ticket.getActive()) ? 1 : 0;
            ps.setInt(3, val);
            ps.setInt(4, ticket.getId());
            ps.execute();
            return true;
        } catch (Exception ex) {
            logger.error("Error saving ticket info", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }

    public Boolean getHasPastTicket(String vehicleRegNumber) {
        Connection con = null;
        Boolean result = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.PAST_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1, vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            int nbExistingPastTickets;
            if (rs.next()) {
                nbExistingPastTickets = rs.getInt(1);
                if (nbExistingPastTickets > 0) {

                    result = true;
                } else if (nbExistingPastTickets <= 0) {
                    result = false;
                } else if (result == null) {
                    throw new NullPointerException("method getHasPasTicket returns null - check scenarii");
                }

            }

            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        } catch (Exception ex) {
            logger.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closeConnection(con);

        }
        return result;
    }



    public int getActiveTicketCount(String vehicleRegNumber) {
        Connection con = null;
        int result = -1;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.ACTIVE_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1, vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);

            } else if(result==-1) {
                throw new Exception("result retunred is -1 failed");
            }

            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        } catch (Exception ex) {
            logger.error("Error getting active ticket count", ex);
        } finally {
            dataBaseConfig.closeConnection(con);

        }
        return result;
    }



    public Boolean convertIntBoolean(int input) {
        Boolean result = null;

        if (input >= 1) {
            result = true;
        }
        else {
            result = false;
        }

        return result;
    }

}
