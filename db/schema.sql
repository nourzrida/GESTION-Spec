-- Create database
CREATE DATABASE IF NOT EXISTS teskerti_events;
USE teskerti_events;

-- Users table
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  phone VARCHAR(20) NOT NULL,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Events table
CREATE TABLE IF NOT EXISTS events (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  event_date DATETIME NOT NULL,
  venue VARCHAR(255) NOT NULL,
  image_url VARCHAR(255),
  price DECIMAL(10, 2) NOT NULL,
  category VARCHAR(100) NOT NULL,
  is_featured BOOLEAN DEFAULT FALSE,
  is_sold_out BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Ticket types table
CREATE TABLE IF NOT EXISTS ticket_types (
  id INT AUTO_INCREMENT PRIMARY KEY,
  event_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  price DECIMAL(10, 2) NOT NULL,
  max_quantity INT DEFAULT 10,
  FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
  id INT AUTO_INCREMENT PRIMARY KEY,
  reference VARCHAR(20) NOT NULL UNIQUE,
  event_id INT NOT NULL,
  customer_name VARCHAR(255) NOT NULL,
  customer_email VARCHAR(255) NOT NULL,
  customer_phone VARCHAR(20) NOT NULL,
  total_price DECIMAL(10, 2) NOT NULL,
  booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

-- Event seats table
CREATE TABLE IF NOT EXISTS event_seats (
  id INT AUTO_INCREMENT PRIMARY KEY,
  event_id INT NOT NULL,
  seat_id VARCHAR(10) NOT NULL,
  booking_id INT,
  is_reserved BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
  FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL,
  UNIQUE KEY (event_id, seat_id)
);

-- Booking tickets table
CREATE TABLE  ON DELETE SET NULL,
  UNIQUE KEY (event_id, seat_id)
);

-- Booking tickets table
CREATE TABLE IF NOT EXISTS booking_tickets (
  id INT AUTO_INCREMENT PRIMARY KEY,
  booking_id INT NOT NULL,
  ticket_type_id INT NOT NULL,
  quantity INT NOT NULL,
  price_per_ticket DECIMAL(10, 2) NOT NULL,
  FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
  FOREIGN KEY (ticket_type_id) REFERENCES ticket_types(id) ON DELETE CASCADE
);

-- Temporary reservations table
CREATE TABLE IF NOT EXISTS temp_reservations (
  id INT AUTO_INCREMENT PRIMARY KEY,
  event_id INT NOT NULL,
  seat_id VARCHAR(10) NOT NULL,
  session_id VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
  UNIQUE KEY (event_id, seat_id)
);
