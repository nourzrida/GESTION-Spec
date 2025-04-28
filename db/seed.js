const mysql = require("mysql2/promise")
const bcrypt = require("bcryptjs")
const dotenv = require("dotenv")

dotenv.config()

async function seed() {
  // Create connection
  const connection = await mysql.createConnection({
    host: process.env.DB_HOST || "localhost",
    user: process.env.DB_USER || "root",
    password: process.env.DB_PASSWORD || "",
    database: process.env.DB_NAME || "teskerti_events",
    multipleStatements: true,
  })

  try {
    console.log("Seeding database...")

    // Create test user
    const salt = await bcrypt.genSalt(10)
    const hashedPassword = await bcrypt.hash("password123", salt)

    await connection.execute(
      `
      INSERT INTO users (name, email, phone, password)
      VALUES ('Test User', 'test@example.com', '+21612345678', ?)
    `,
      [hashedPassword],
    )

    // Insert sample events
    await connection.execute(`
      INSERT INTO events (title, description, event_date, venue, image_url, price, category, is_featured, is_sold_out)
      VALUES 
      (
        'STRAUSS & MOZART', 
        'Découvrez une soirée exceptionnelle avec l\'Orchestre Symphonique de Vienne Weber Sinfonietta sous la direction artistique de Laurent Petitgirard. Au programme : valses, polkas et symphonie dans un cadre magnifique au Théâtre de l\'Opéra de Tunis.',
        '2025-04-28 19:30:00',
        'Théâtre de l\'opéra',
        'https://example.com/images/strauss_mozart.jpg',
        50.0,
        'Classical',
        TRUE,
        FALSE
      ),
      (
        'L\'INTERNATIONAL JAZZ DAY',
        'Célébrez la Journée Internationale du Jazz avec un concert exceptionnel réunissant des artistes de renommée mondiale. Une soirée inoubliable de jazz contemporain et traditionnel.',
        '2025-04-30 20:00:00',
        'Acropolium de Carthage',
        'https://example.com/images/jazz_day.jpg',
        35.0,
        'Jazz',
        TRUE,
        FALSE
      ),
      (
        'FESTIVAL DE CARTHAGE',
        'Le Festival International de Carthage revient avec une programmation exceptionnelle. Musique, théâtre et danse dans le cadre historique de l\'amphithéâtre romain.',
        '2025-07-15 21:00:00',
        'Amphithéâtre de Carthage',
        'https://example.com/images/carthage_festival.jpg',
        60.0,
        'Festival',
        FALSE,
        FALSE
      ),
      (
        'CONCERT DE MUSIQUE ANDALOUSE',
        'Un voyage musical à travers les traditions andalouses avec l\'Orchestre National de Musique Andalouse. Découvrez les mélodies envoûtantes du patrimoine musical méditerranéen.',
        '2025-05-10 19:00:00',
        'Palais Ennejma Ezzahra',
        'https://example.com/images/andalusian_music.jpg',
        40.0,
        'Traditional',
        FALSE,
        FALSE
      ),
      (
        'BALLET CASSE-NOISETTE',
        'Le célèbre ballet Casse-Noisette de Tchaïkovski interprété par la Compagnie Nationale de Ballet. Un spectacle féerique pour toute la famille.',
        '2025-12-20 18:00:00',
        'Théâtre de l\'opéra',
        'https://example.com/images/nutcracker.jpg',
        70.0,
        'Ballet',
        FALSE,
        TRUE
      )
    `)

    // Insert ticket types for each event
    const [events] = await connection.execute("SELECT id FROM events")

    for (const event of events) {
      // Different ticket types based on event
      if (event.id === 1) {
        // STRAUSS & MOZART
        await connection.execute(
          `
          INSERT INTO ticket_types (event_id, name, description, price, max_quantity)
          VALUES 
          (?, 'VIP', 'Front row seats with complimentary drink', 80.0, 10),
          (?, 'Premium', 'Center section seats with good visibility', 50.0, 20),
          (?, 'Standard', 'Regular seating', 30.0, 50)
        `,
          [event.id, event.id, event.id],
        )
      } else if (event.id === 2) {
        // JAZZ DAY
        await connection.execute(
          `
          INSERT INTO ticket_types (event_id, name, description, price, max_quantity)
          VALUES 
          (?, 'Gold', 'Best seats with exclusive access to after-party', 70.0, 15),
          (?, 'Silver', 'Good visibility seats', 35.0, 30),
          (?, 'Bronze', 'Standard seating', 20.0, 40)
        `,
          [event.id, event.id, event.id],
        )
      } else {
        // Generic ticket types for other events
        await connection.execute(
          `
          INSERT INTO ticket_types (event_id, name, description, price, max_quantity)
          VALUES 
          (?, 'Premium', 'Best seats in the house', ?, 20),
          (?, 'Standard', 'Regular seating', ?, 50)
        `,
          [event.id, event.id * 10, event.id, event.id * 5],
        )
      }

      // Create seat map for each event
      const rows = ["A", "B", "C", "D", "E", "F", "G", "H"]
      const seatsPerRow = 10

      for (const row of rows) {
        for (let seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
          const seatId = `${row}${seatNum}`
          await connection.execute(
            `
            INSERT INTO event_seats (event_id, seat_id, is_reserved)
            VALUES (?, ?, FALSE)
          `,
            [event.id, seatId],
          )
        }
      }
    }

    console.log("Database seeded successfully!")
  } catch (error) {
    console.error("Error seeding database:", error)
  } finally {
    await connection.end()
  }
}

seed()
