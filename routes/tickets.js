const express = require("express")
const router = express.Router()
const TicketType = require("../models/TicketType")

// Get ticket types for an event
router.get("/types/:eventId", async (req, res, next) => {
  try {
    const { eventId } = req.params
    const ticketTypes = await TicketType.getByEventId(eventId)
    res.json(ticketTypes)
  } catch (error) {
    next(error)
  }
})

// Get seat availability for an event
router.get("/seats/:eventId", async (req, res, next) => {
  try {
    const { eventId } = req.params
    const seatAvailability = await TicketType.getSeatAvailability(eventId)
    res.json(seatAvailability)
  } catch (error) {
    next(error)
  }
})

// Reserve seats temporarily
router.post("/reserve", async (req, res, next) => {
  try {
    const { eventId, seats, sessionId } = req.body

    if (!eventId || !seats || !seats.length || !sessionId) {
      return res.status(400).json({
        success: false,
        message: "Missing required fields",
      })
    }

    const result = await TicketType.reserveSeats(eventId, seats, sessionId)
    res.json(result)
  } catch (error) {
    next(error)
  }
})

module.exports = router
