const db = require("../config/database")

class Event {
  static async getAll() {
    try {
      const [rows] = await db.query(`
        SELECT * FROM events 
        WHERE event_date >= CURDATE() 
        ORDER BY is_featured DESC, event_date ASC
      `)
      return rows.map(this.formatEvent)
    } catch (error) {
      throw error
    }
  }

  static async getById(id) {
    try {
      const [rows] = await db.query("SELECT * FROM events WHERE id = ?", [id])
      if (rows.length === 0) return null
      return this.formatEvent(rows[0])
    } catch (error) {
      throw error
    }
  }

  static async search(filters) {
    try {
      const { title, place, category, startDate, endDate } = filters

      let query = `
        SELECT * FROM events 
        WHERE 1=1
      `

      const params = []

      if (title) {
        query += ` AND title LIKE ?`
        params.push(`%${title}%`)
      }

      if (place) {
        query += ` AND venue = ?`
        params.push(place)
      }

      if (category) {
        query += ` AND category = ?`
        params.push(category)
      }

      if (startDate) {
        query += ` AND event_date >= ?`
        params.push(startDate)
      }

      if (endDate) {
        query += ` AND event_date <= ?`
        params.push(endDate)
      }

      query += ` ORDER BY event_date ASC`

      const [rows] = await db.query(query, params)
      return rows.map(this.formatEvent)
    } catch (error) {
      throw error
    }
  }

  static async getCategories() {
    try {
      const [rows] = await db.query("SELECT DISTINCT category FROM events ORDER BY category")
      return rows.map((row) => row.category)
    } catch (error) {
      throw error
    }
  }

  static async getVenues() {
    try {
      const [rows] = await db.query("SELECT DISTINCT venue FROM events ORDER BY venue")
      return rows.map((row) => row.venue)
    } catch (error) {
      throw error
    }
  }

  static formatEvent(row) {
    return {
      id: row.id,
      title: row.title,
      description: row.description,
      date: row.event_date,
      venue: row.venue,
      imageUrl: row.image_url,
      price: row.price,
      category: row.category,
      isFeatured: Boolean(row.is_featured),
      isSoldOut: Boolean(row.is_sold_out),
    }
  }
}

module.exports = Event
