-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 04, 2026 at 05:47 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `capstone2026`
--

-- --------------------------------------------------------

--
-- Table structure for table `documents`
--

CREATE TABLE `documents` (
  `documentID` int(11) NOT NULL,
  `documentName` varchar(255) DEFAULT NULL,
  `documentType` varchar(100) DEFAULT NULL,
  `status` varchar(50) DEFAULT 'Pending',
  `filePath` varchar(500) DEFAULT NULL,
  `fileSize` mediumtext DEFAULT NULL,
  `uploadDate` datetime DEFAULT current_timestamp(),
  `userID` int(11) DEFAULT NULL,
  `applicationID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `scholarships`
--

CREATE TABLE `scholarships` (
  `scholarshipID` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `sponsor` varchar(255) NOT NULL,
  `gradeRequired` double NOT NULL,
  `location` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `status` enum('Active','Inactive') DEFAULT 'Active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `scholarships`
--

INSERT INTO `scholarships` (`scholarshipID`, `name`, `sponsor`, `gradeRequired`, `location`, `description`, `status`) VALUES
(1, 'Academic Excellence Grant', 'CIT-University', 92, 'Cebu City', 'For top-performing students in IT.', 'Active'),
(2, 'Tech Future Scholarship', 'Global Tech Solutions', 88.5, 'Manila', 'Focuses on software development talent.', 'Active'),
(3, 'LGU Cebu Financial Aid', 'Cebu City Government', 80, 'Cebu City', 'General assistance for local residents.', 'Active'),
(4, 'Davao Tech Leaders', 'Mindanao Dev Group', 85, 'Davao', 'For students pursuing computer science in Davao.', 'Active'),
(5, 'Academic Excellence Grant', 'CIT-University', 92, 'Cebu City', NULL, 'Active'),
(6, 'Tech Future Scholarship', 'Global Tech Solutions', 88.5, 'Manila', NULL, 'Active'),
(7, 'LGU Cebu Financial Aid', 'Cebu City Government', 80, 'Cebu City', NULL, 'Active'),
(8, 'Davao Tech Leaders', 'Mindanao Dev Group', 85, 'Davao', NULL, 'Active');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `userID` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('STUDENT','SPONSOR','ADMIN') NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`userID`, `name`, `email`, `password`, `role`, `created_at`) VALUES
(1, 'Henry James Godinez', 'henry@email.com', 'henry123', 'STUDENT', '2026-05-04 07:07:48'),
(2, 'Global Tech Corp', 'sponsor@tech.com', 'sponsor456', 'SPONSOR', '2026-05-04 07:07:48'),
(3, 'Test Me', 'test@test.com', 'test', 'STUDENT', '2026-05-04 14:17:47'),
(20, 'test2 Me', 'test2@test.com', 'password', 'STUDENT', '2026-05-04 14:23:21'),
(21, 'admin Marauder', 'admin@cit.edu', 'password', 'ADMIN', '2026-05-04 14:59:22');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `documents`
--
ALTER TABLE `documents`
  ADD PRIMARY KEY (`documentID`),
  ADD KEY `userID` (`userID`);

--
-- Indexes for table `scholarships`
--
ALTER TABLE `scholarships`
  ADD PRIMARY KEY (`scholarshipID`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`userID`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `documents`
--
ALTER TABLE `documents`
  MODIFY `documentID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `scholarships`
--
ALTER TABLE `scholarships`
  MODIFY `scholarshipID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `userID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `documents`
--
ALTER TABLE `documents`
  ADD CONSTRAINT `documents_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
