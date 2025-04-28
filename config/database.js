const mysql = require("mysql2/promise")
const dotenv = require("dotenv")

dotenv.config()

// Create connection pool
const pool = mysql.createPool({
  host: process.env.DB_HOST || "localhost",
  user: process.env.DB_USER || "root",
  password: process.env.DB_PASSWORD || "",
  database: process.env.DB_NAME || "teskerti_events",
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
})

// Test connection function
async function testConnection() {
  try {
    const connection = await pool.getConnection()
    console.log("Database connection successful")
    connection.release()
    return true
  } catch (error) {
    console.error("Database connection error:", error)
    return false
  }
}

module.exports = pool
