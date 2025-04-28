const db = require("../config/database")

class TicketType {
  static async getByEventId(eventId) {
    try {
      const [rows] = await db.query("SELECT * FROM ticket_types WHERE event_id = ?", [eventId])

      return rows.map((row) => ({
        id: row.id,
        name: row.name,
        description: row.description,
        price: row.price,
        maxQuantity: row.max_quantity,
      }))
    } catch (error) {
      throw error
    }
  }

  static async getSeatAvailability(eventId) {
    try {
      // Get all seats for the event
      const [allSeats] = await db.query("SELECT seat_id FROM event_seats WHERE event_id = ?", [eventId])

      // Get reserved seats
      const [reservedSeats] = await db.query(
        `SELECT es.seat_id 
         FROM event_seats es
         JOIN bookings b ON es.booking_id = b.id
         WHERE es.event_id = ? AND es.is_reserved = 1`,
        [eventId],
      )

      const reservedSeatIds = reservedSeats.map((row) => row.seat_id)
      const availableSeatIds = allSeats.map((row) => row.seat_id).filter((seatId) => !reservedSeatIds.includes(seatId))

      return {
        availableSeats: availableSeatIds,
        reservedSeats: reservedSeatIds,
      }
    } catch (error) {
      throw error
    }
  }

  static async reserveSeats(eventId, seats, sessionId) {
    const connection = await db.getConnection()

    try {
      await connection.beginTransaction()

      // Check if seats are available
      const seatPlaceholders = seats.map(() => "?").join(",")
      const params = [eventId, ...seats]

      const [reservedSeats] = await connection.query(
        `SELECT seat_id FROM event_seats 
         WHERE event_id = ? AND seat_id IN (${seatPlaceholders}) AND is_reserved = 1`,
        params,
      )

      if (reservedSeats.length > 0) {
        await connection.rollback()
        return {
          success: false,
          message: "Some selected seats are already reserved",
          reservedSeats: reservedSeats.map((row) => row.seat_id),
        }
      }

      // Remove any expired temporary reservations for this session
      await connection.query(
        `DELETE FROM temp_reservations 
         WHERE session_id = ? OR created_at < DATE_SUB(NOW(), INTERVAL 15 MINUTE)`,
        [sessionId],
      )

      // Insert new temporary reservations
      for (const seat of seats) {
        await connection.query(
          `INSERT INTO temp_reservations (event_id, seat_id, session_id, created_at)
           VALUES (?, ?, ?, NOW())`,
          [eventId, seat, sessionId],
        )
      }

      await connection.commit()
      return { success: true }
    } catch (error) {
      await connection.rollback()
      throw error
    } finally {
      connection.release()
    }
  }
}

module.exports = TicketType
