const express = require("express")
const cors = require("cors")
const morgan = require("morgan")
const dotenv = require("dotenv")
const eventRoutes = require("./routes/events")
const ticketRoutes = require("./routes/tickets")
const bookingRoutes = require("./routes/bookings")
const userRoutes = require("./routes/users")
const db = require("./config/database")

// Load environment variables
dotenv.config()

// Initialize express app
const app = express()
const PORT = process.env.PORT || 3000

// Middleware
app.use(cors())
app.use(express.json())
app.use(morgan("dev"))

// Routes
app.use("/api/v1/events", eventRoutes)
app.use("/api/v1/tickets", ticketRoutes)
app.use("/api/v1/bookings", bookingRoutes)
app.use("/api/v1/users", userRoutes)

// Root route
app.get("/", (req, res) => {
  res.json({
    message: "Welcome to TeskertiEvents API",
    version: "1.0.0",
  })
})

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack)
  res.status(500).json({
    success: false,
    message: "Something went wrong!",
    error: process.env.NODE_ENV === "development" ? err.message : undefined,
  })
})

// Test database connection before starting server
db.getConnection()
  .then((connection) => {
    console.log("Database connection successful")
    connection.release()

    // Start server
    app.listen(PORT, () => {
      console.log(`Server running on port ${PORT}`)
    })
  })
  .catch((err) => {
    console.error("Database connection failed:", err)
    process.exit(1)
  })

module.exports = app
