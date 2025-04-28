const db = require("../config/database")
const QRCode = require("qrcode")
const { v4: uuidv4 } = require("uuid")

class Booking {
  static async create(bookingData) {
    const { eventId, selectedSeats, ticketTypes, sessionId, customerInfo, paymentInfo } = bookingData

    const connection = await db.getConnection()

    try {
      await connection.beginTransaction()

      // Check if seats are still available
      const seatPlaceholders = selectedSeats.map(() => "?").join(",")
      const seatParams = [eventId, ...selectedSeats]

      const [reservedSeats] = await connection.query(
        `SELECT seat_id FROM event_seats 
         WHERE event_id = ? AND seat_id IN (${seatPlaceholders}) AND is_reserved = 1`,
        seatParams,
      )

      if (reservedSeats.length > 0) {
        await connection.rollback()
        return {
          success: false,
          message: "Some selected seats are no longer available",
          reservedSeats: reservedSeats.map((row) => row.seat_id),
        }
      }

      // Calculate total price
      const [ticketTypeRows] = await connection.query("SELECT * FROM ticket_types WHERE event_id = ?", [eventId])

      const ticketTypeMap = {}
      ticketTypeRows.forEach((row) => {
        ticketTypeMap[row.name] = row
      })

      let totalPrice = 0
      for (const [typeName, quantity] of Object.entries(ticketTypes)) {
        if (ticketTypeMap[typeName]) {
          totalPrice += ticketTypeMap[typeName].price * quantity
        }
      }

      // Generate booking reference
      const bookingReference = this.generateBookingReference()

      // Create booking record
      const [bookingResult] = await connection.query(
        `INSERT INTO bookings 
         (reference, event_id, customer_name, customer_email, customer_phone, total_price, booking_date)
         VALUES (?, ?, ?, ?, ?, ?, NOW())`,
        [bookingReference, eventId, customerInfo.name, customerInfo.email, customerInfo.phone, totalPrice],
      )

      const bookingId = bookingResult.insertId

      // Reserve seats
      for (const seatId of selectedSeats) {
        await connection.query(
          `INSERT INTO event_seats (event_id, seat_id, booking_id, is_reserved)
           VALUES (?, ?, ?, 1)
           ON DUPLICATE KEY UPDATE booking_id = ?, is_reserved = 1`,
          [eventId, seatId, bookingId, bookingId],
        )
      }

      // Save ticket details
      for (const [typeName, quantity] of Object.entries(ticketTypes)) {
        if (quantity > 0 && ticketTypeMap[typeName]) {
          await connection.query(
            `INSERT INTO booking_tickets 
             (booking_id, ticket_type_id, quantity, price_per_ticket)
             VALUES (?, ?, ?, ?)`,
            [bookingId, ticketTypeMap[typeName].id, quantity, ticketTypeMap[typeName].price],
          )
        }
      }

      // Remove temporary reservations
      await connection.query("DELETE FROM temp_reservations WHERE session_id = ?", [sessionId])

      // Generate QR code data
      const qrData = JSON.stringify({
        reference: bookingReference,
        eventId,
        seats: selectedSeats,
        customerName: customerInfo.name,
      })

      const qrCodeData = await QRCode.toDataURL(qrData)

      await connection.commit()

      return {
        success: true,
        message: "Booking confirmed successfully",
        bookingReference,
        qrCodeData,
      }
    } catch (error) {
      await connection.rollback()
      throw error
    } finally {
      connection.release()
    }
  }

  static async getUserBookings(userEmail) {
    try {
      const [rows] = await db.query(
        `SELECT b.*, e.title as event_title, e.event_date, e.venue
         FROM bookings b
         JOIN events e ON b.event_id = e.id
         WHERE b.customer_email = ?
         ORDER BY b.booking_date DESC`,
        [userEmail],
      )

      const bookings = []

      for (const row of rows) {
        // Get seats for this booking
        const [seatRows] = await db.query("SELECT seat_id FROM event_seats WHERE booking_id = ?", [row.id])

        // Get tickets for this booking
        const [ticketRows] = await db.query(
          `SELECT bt.*, tt.name, tt.description
           FROM booking_tickets bt
           JOIN ticket_types tt ON bt.ticket_type_id = tt.id
           WHERE bt.booking_id = ?`,
          [row.id],
        )

        const tickets = ticketRows.map((ticket) => ({
          name: ticket.name,
          description: ticket.description,
          quantity: ticket.quantity,
          pricePerTicket: ticket.price_per_ticket,
        }))

        bookings.push({
          bookingReference: row.reference,
          eventId: row.event_id,
          eventTitle: row.event_title,
          eventDate: row.event_date,
          eventVenue: row.venue,
          seats: seatRows.map((seat) => seat.seat_id),
          tickets,
          totalPrice: row.total_price,
          bookingDate: row.booking_date,
        })
      }

      return bookings
    } catch (error) {
      throw error
    }
  }

  static generateBookingReference() {
    // Format: TES-XXXXX-XX (where X is alphanumeric)
    const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    let reference = "TES-"

    // Add 5 random alphanumeric characters
    for (let i = 0; i < 5; i++) {
      reference += chars.charAt(Math.floor(Math.random() * chars.length))
    }

    reference += "-"

    // Add 2 more random alphanumeric characters
    for (let i = 0; i < 2; i++) {
      reference += chars.charAt(Math.floor(Math.random() * chars.length))
    }

    return reference
  }
}

module.exports = Booking
