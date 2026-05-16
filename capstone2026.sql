-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 16, 2026 at 04:30 PM
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
-- Table structure for table `applications`
--

CREATE TABLE `applications` (
  `applicationID` int(11) NOT NULL,
  `studentID` int(11) DEFAULT NULL,
  `status` varchar(50) DEFAULT 'Pending',
  `appliedDate` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

--
-- Dumping data for table `documents`
--

INSERT INTO `documents` (`documentID`, `documentName`, `documentType`, `status`, `filePath`, `fileSize`, `uploadDate`, `userID`, `applicationID`) VALUES
(2, '1749552054_swastika-transparent-background.png', 'Transcript of Records', 'Pending', 'uploads\\documents\\user22_1778054184924_1749552054_swastika-transparent-background.png', '38234', '2026-05-06 15:56:24', 22, NULL),
(3, 'Upload Success.png', 'Transcript of Records', 'Pending', 'uploads\\documents\\user22_1778054650648_Upload Success.png', '144447', '2026-05-06 16:04:10', 22, NULL);

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
(4, 'Davao Tech Leaders', 'Mindanao Dev Group', 85, 'Davao', 'For students pursuing computer science in Davao.', 'Active');

-- --------------------------------------------------------

--
-- Table structure for table `scholarship_requirements`
--

CREATE TABLE `scholarship_requirements` (
  `requirementID` int(11) NOT NULL,
  `scholarshipID` int(11) NOT NULL,
  `process_steps` text DEFAULT NULL,
  `award_details` varchar(255) DEFAULT 'Full Tuition',
  `deadline_date` date DEFAULT '2026-10-24',
  `documents_needed` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `scholarship_requirements`
--

INSERT INTO `scholarship_requirements` (`requirementID`, `scholarshipID`, `process_steps`, `award_details`, `deadline_date`, `documents_needed`) VALUES
(1, 1, '1. Fill online form\n2. Submit grades copy\n3. Interview phase.', 'Full Tuition + Allowance', '2026-10-24', '- Report Card\n- Brgy Clearance\n- Parent\'s ITR'),
(2, 2, '1. Technical coding exam\n2. Panel interview.', 'Php 50,000 / Semester', '2026-11-15', '- Transcript of Records\n- Coding Portfolio'),
(3, 3, '1. Visit city hall portal\n2. Submit residency papers.', 'Book Stipend + Partial Fee', '2026-09-30', '- Certificate of Residency\n- Voter\'s ID'),
(4, 4, '1. Application letter submission\n2. Essay evaluation.', 'Full Tuition Coverage', '2026-10-05', '- Report Card\n- Recommendation Letter');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `userID` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `lrn` varchar(20) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('STUDENT','SPONSOR','ADMIN') NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`userID`, `name`, `email`, `lrn`, `password`, `role`, `created_at`) VALUES
(1, 'Henry James Godinez', 'henry@email.com', '119886110189', '123', 'STUDENT', '2026-05-04 07:07:48'),
(2, 'Global Tech Corp', 'sponsor@tech.com', NULL, 'sponsor456', 'SPONSOR', '2026-05-04 07:07:48'),
(3, 'Test Me', 'test@test.com', NULL, 'test', 'STUDENT', '2026-05-04 14:17:47'),
(20, 'test2 Me', 'test2@test.com', NULL, 'password', 'STUDENT', '2026-05-04 14:23:21'),
(21, 'admin Marauder', 'admin@cit.edu', NULL, 'password', 'ADMIN', '2026-05-04 14:59:22'),
(22, 'John Paul', 'jp@email.com', '11223344556677', 'jp123456', 'ADMIN', '2026-05-06 07:17:45');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `applications`
--
ALTER TABLE `applications`
  ADD PRIMARY KEY (`applicationID`),
  ADD KEY `studentID` (`studentID`);

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
-- Indexes for table `scholarship_requirements`
--
ALTER TABLE `scholarship_requirements`
  ADD PRIMARY KEY (`requirementID`),
  ADD KEY `fk_scholarship_req` (`scholarshipID`);

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
-- AUTO_INCREMENT for table `applications`
--
ALTER TABLE `applications`
  MODIFY `applicationID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `documents`
--
ALTER TABLE `documents`
  MODIFY `documentID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `scholarships`
--
ALTER TABLE `scholarships`
  MODIFY `scholarshipID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `scholarship_requirements`
--
ALTER TABLE `scholarship_requirements`
  MODIFY `requirementID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `userID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `applications`
--
ALTER TABLE `applications`
  ADD CONSTRAINT `applications_ibfk_1` FOREIGN KEY (`studentID`) REFERENCES `users` (`userID`) ON DELETE CASCADE;

--
-- Constraints for table `documents`
--
ALTER TABLE `documents`
  ADD CONSTRAINT `documents_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`);

--
-- Constraints for table `scholarship_requirements`
--
ALTER TABLE `scholarship_requirements`
  ADD CONSTRAINT `fk_scholarship_req` FOREIGN KEY (`scholarshipID`) REFERENCES `scholarships` (`scholarshipID`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
