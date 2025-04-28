const express = require("express")
const router = express.Router()
const Booking = require("../models/Booking")
const { authenticateToken } = require("../middleware/auth")

// Create a new booking
router.post("/create", async (req, res, next) => {
  try {
    const { eventId, selectedSeats, ticketTypes, sessionId, customerInfo, paymentInfo } = req.body

    // Validate required fields
    if (!eventId || !selectedSeats || !ticketTypes || !sessionId || !customerInfo || !paymentInfo) {
      return res.status(400).json({
        success: false,
        message: "Missing required fields",
      })
    }

    // Validate customer info
    if (!customerInfo.name || !customerInfo.email || !customerInfo.phone) {
      return res.status(400).json({
        success: false,
        message: "Missing customer information",
      })
    }

    // Validate payment info
    if (!paymentInfo.cardNumber || !paymentInfo.expiryDate || !paymentInfo.cvv) {
      return res.status(400).json({
        success: false,
        message: "Missing payment information",
      })
    }

    const result = await Booking.create({
      eventId,
      selectedSeats,
      ticketTypes,
      sessionId,
      customerInfo,
      paymentInfo,
    })

    res.json(result)
  } catch (error) {
    next(error)
  }
})

// Get booking details by reference
router.get("/:reference", authenticateToken, async (req, res, next) => {
  try {
    const { reference } = req.params
    const booking = await Booking.getByReference(reference)

    if (!booking) {
      return res.status(404).json({
        success: false,
        message: "Booking not found",
      })
    }

    // Check if the booking belongs to the authenticated user
    if (booking.customerEmail !== req.user.email) {
      return res.status(403).json({
        success: false,
        message: "Unauthorized access to booking",
      })
    }

    res.json(booking)
  } catch (error) {
    next(error)
  }
})

module.exports = router
