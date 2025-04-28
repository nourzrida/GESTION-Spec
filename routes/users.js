const express = require("express")
const router = express.Router()
const User = require("../models/User")
const Booking = require("../models/Booking")
const { authenticateToken } = require("../middleware/auth")

// Register new user
router.post("/register", async (req, res, next) => {
  try {
    const { name, email, phone, password } = req.body

    // Validate required fields
    if (!name || !email || !phone || !password) {
      return res.status(400).json({
        success: false,
        message: "All fields are required",
      })
    }

    const result = await User.register({ name, email, phone, password })
    res.status(result.success ? 201 : 400).json(result)
  } catch (error) {
    next(error)
  }
})

// Login user
router.post("/login", async (req, res, next) => {
  try {
    const { email, password } = req.body

    // Validate required fields
    if (!email || !password) {
      return res.status(400).json({
        success: false,
        message: "Email and password are required",
      })
    }

    const result = await User.login(email, password)
    res.status(result.success ? 200 : 401).json(result)
  } catch (error) {
    next(error)
  }
})

// Get user profile
router.get("/profile", authenticateToken, async (req, res, next) => {
  try {
    const user = await User.getProfile(req.user.id)

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "User not found",
      })
    }

    res.json({
      success: true,
      user,
    })
  } catch (error) {
    next(error)
  }
})

// Get user's bookings
router.get("/:userId/bookings", authenticateToken, async (req, res, next) => {
  try {
    const { userId } = req.params

    // Check if the user is requesting their own bookings
    if (userId !== req.user.id && userId !== req.user.email) {
      return res.status(403).json({
        success: false,
        message: "Unauthorized access to bookings",
      })
    }

    const bookings = await Booking.getUserBookings(req.user.email)
    res.json(bookings)
  } catch (error) {
    next(error)
  }
})

module.exports = router
