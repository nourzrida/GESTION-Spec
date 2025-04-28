const express = require("express")
const router = express.Router()
const Event = require("../models/Event")

// Get all events
router.get("/", async (req, res, next) => {
  try {
    const events = await Event.getAll()
    res.json(events)
  } catch (error) {
    next(error)
  }
})

// Search events with filters
router.get("/search", async (req, res, next) => {
  try {
    const filters = {
      title: req.query.title,
      place: req.query.place,
      category: req.query.category,
      startDate: req.query.startDate,
      endDate: req.query.endDate,
    }

    const events = await Event.search(filters)
    res.json(events)
  } catch (error) {
    next(error)
  }
})

// Get event by ID
router.get("/:eventId", async (req, res, next) => {
  try {
    const { eventId } = req.params
    const event = await Event.getById(eventId)

    if (!event) {
      return res.status(404).json({
        success: false,
        message: "Event not found",
      })
    }

    res.json(event)
  } catch (error) {
    next(error)
  }
})

// Get all categories
router.get("/categories", async (req, res, next) => {
  try {
    const categories = await Event.getCategories()
    res.json(categories)
  } catch (error) {
    next(error)
  }
})

// Get all venues
router.get("/venues", async (req, res, next) => {
  try {
    const venues = await Event.getVenues()
    res.json(venues)
  } catch (error) {
    next(error)
  }
})

module.exports = router
