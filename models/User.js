const db = require("../config/database")
const bcrypt = require("bcryptjs")
const jwt = require("jsonwebtoken")

class User {
  static async register(userData) {
    const { name, email, phone, password } = userData

    try {
      // Check if user already exists
      const [existingUsers] = await db.query("SELECT * FROM users WHERE email = ?", [email])

      if (existingUsers.length > 0) {
        return {
          success: false,
          message: "User with this email already exists",
        }
      }

      // Hash password
      const salt = await bcrypt.genSalt(10)
      const hashedPassword = await bcrypt.hash(password, salt)

      // Create user
      const [result] = await db.query("INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)", [
        name,
        email,
        phone,
        hashedPassword,
      ])

      return {
        success: true,
        message: "User registered successfully",
        userId: result.insertId,
      }
    } catch (error) {
      throw error
    }
  }

  static async login(email, password) {
    try {
      // Find user
      const [users] = await db.query("SELECT * FROM users WHERE email = ?", [email])

      if (users.length === 0) {
        return {
          success: false,
          message: "Invalid credentials",
        }
      }

      const user = users[0]

      // Check password
      const isMatch = await bcrypt.compare(password, user.password)

      if (!isMatch) {
        return {
          success: false,
          message: "Invalid credentials",
        }
      }

      // Generate JWT token
      const token = jwt.sign({ id: user.id, email: user.email }, process.env.JWT_SECRET || "teskerti_secret", {
        expiresIn: "30d",
      })

      return {
        success: true,
        message: "Login successful",
        token,
        user: {
          id: user.id,
          name: user.name,
          email: user.email,
          phone: user.phone,
        },
      }
    } catch (error) {
      throw error
    }
  }

  static async getProfile(userId) {
    try {
      const [users] = await db.query("SELECT id, name, email, phone, created_at FROM users WHERE id = ?", [userId])

      if (users.length === 0) {
        return null
      }

      return users[0]
    } catch (error) {
      throw error
    }
  }
}

module.exports = User
