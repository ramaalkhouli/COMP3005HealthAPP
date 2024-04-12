----- Populating Tables -----------

-- Populate the Members table
INSERT INTO Members (FirstName, LastName, Email, Password, DoB, StreetName, City, Province, PostalCode)
VALUES
('SpongeBob', 'SquarePants', 'spongebob@bikinibottom.net', 'pineapple123', '1986-07-14', '124 Conch Street', 'Bikini Bottom', 'Ocean', '12345'),
('Patrick', 'Star', 'patrick@bikinibottom.net', 'rockstar123', '1984-02-26', '120 Conch Street', 'Bikini Bottom', 'Ocean', '12345'),
('Squidward', 'Tentacles', 'squidward@bikinibottom.net', 'clarinet123', '1972-10-09', '122 Conch Street', 'Bikini Bottom', 'Ocean', '12345');

-- Populate the Trainers table
INSERT INTO Trainers (FirstName, LastName, Email, Password, Availability)
VALUES
('Sandy', 'Cheeks', 'sandy@bikinibottom.net', 'karate123', '{"09:00", "11:00", "15:00"}'),
('Larry', 'Lobster', 'larry@bikinibottom.net', 'weights123', '{"10:00", "12:00", "16:00"}');

-- Populate the AdministrativeStaff table
INSERT INTO AdministrativeStaff (FirstName, LastName, Email, Password)
VALUES
('Pearl', 'Krabs', 'pearl@krustykrab.net', 'shopping123'),
('Karen', 'Plankton', 'karen@chum.bucket', 'computer123');

-- Populate the Classes table
INSERT INTO Classes (Name, Description, ScheduledTime)
VALUES
('Spinning', 'Intense spinning class to boost your endurance.', '09:00'),
('Yoga', 'Relaxing yoga session to improve flexibility.', '10:00'),
('Pilates', 'A class aimed at strengthening the body with an emphasis on core strength.', '11:00'),
('Boxing', 'High-intensity class focusing on strength, cardio, and technique.', '12:00'),
('Kickboxing', 'A class combining boxing with elements of karate, especially kicks.', '16:00');

-- Populate the Rooms table
INSERT INTO Rooms (Name, RoomType, Availability, StaffID)
VALUES
('Room A', 'Spinning', TRUE, 1),
('Room B', 'Yoga', TRUE, 2);

-- Populate the Equipment table
INSERT INTO Equipment (EquipmentType, Description, StaffID)
VALUES
('Treadmill', 'High-speed treadmill for cardio workouts.', 1),
('Yoga Mats', 'Eco-friendly yoga mats.', 2),
('Elliptical', 'Well-maintained and functional', 1),
('Stationary Bike', 'Newly serviced, works perfectly', 1),
('Dumbbells', 'Complete set, varying weights, all in good condition', 1),
('Kettlebells', 'Recently acquired, no signs of wear', 1);

-- populate Services Table
INSERT INTO Services (ServiceDescription, Amount) VALUES
('Gym Membership - Monthly', 89),
('Personal Training - Single Session', 30),
('Yoga Class - Drop-in', 25),
('Boxing Workshop', 25),
('Pilates Class Package', 100),
('Nutrition Consultation', 80),
('Health Assessment', 60);

-- populate PersonalizedDashboards Table
INSERT INTO PersonalizedDashboards (ExerciseRoutine, FitnessGoals, FitnessAchievements, HealthMetrics, HealthStatistics, MemberID)
VALUES ('Monday: Cardio, Tuesday: Arms, Wednesday: Abs and Obliques, Thursday: Lower Body, Friday: Cardio, Saturday and Sunday: Rest', 'Run a marathon', 'Goal is in progress', 75.0, 'No data yet', 1);


---- Sample Queries used in the project -----------

--  register a new member
INSERT INTO Members (FirstName, LastName, Email, Password, DoB, StreetName, City, Province, PostalCode)
VALUES ('New', 'Member', 'newmember@bikinibottom.net', 'newpassword123', '1990-01-01', '100 New Street', 'Bikini Bottom', 'Ocean', '12349');

-- update a member's profile information
UPDATE Members
SET Email = 'updatedemail@bikinibottom.net', City = 'New Bikini Bottom'
WHERE MemberID = 1;

-- update a member’s fitness goals in the PersonalizedDashboards table
UPDATE PersonalizedDashboards
SET FitnessGoals = 'Run a 10 km marathon'
WHERE MemberID = 1;

-- Update Health Metrics in PersonalizedDashboards
UPDATE PersonalizedDashboards
SET HealthMetrics = 70.0
WHERE DashboardID = 1;

-- Update Member's ExcerciseRoutine. Assume the routine is generated by third part service
UPDATE PersonalizedDashboards
SET ExerciseRoutine = 'Monday: Cardio, Tuesday: Arms, Wednesday: Abs and Obliques, Thursday: upper Body, Friday: Cardio & swimming, Saturday and Sunday: Rest'
WHERE DashboardID = 1;

-- Display member's dashboard
SELECT * FROM PersonalizedDashboards
WHERE MemberID = 1;

-- Member books a personal training session
INSERT INTO PersonalTrainingSessions (SessionSchedule, Time, MemberID, TrainerID)
VALUES ('2024-04-20', '10:00', 1, 1);

-- Schedule Group Fitness Classes
INSERT INTO Members_Classes (MemberID, ClassID)
VALUES (1, (SELECT ClassID FROM Classes WHERE Name = 'Yoga'));

-- Trainer Setting Availability - updating the availability for a trainer
UPDATE Trainers
SET Availability = ARRAY['10:00:00'::TIME, '12:00:00'::TIME, '14:00:00'::TIME]
WHERE TrainerID = 1;

-- Trainers views a member's profile by searching Member's FirstName
SELECT * FROM Members
WHERE FirstName = 'Patrick';

-- AdministrativeStaff: book a room for a class
UPDATE Rooms
SET Availability = FALSE
WHERE RoomID = 1;

-- AdministrativeStaff- Equipment Maintenance Monitoring: updating the equipment status
UPDATE Equipment
SET Description = 'out of order'
WHERE EquipmentID = 1;

-- Administrative Staff : Class Schedule Updating
UPDATE Classes
SET ScheduledTime = '15:00'
WHERE ClassID = 1;

-- Admin Issuing a bill for a member
INSERT INTO Billing (Date, PaymentMethod, Service, MemberID, ServiceID)
VALUES (
CURRENT_DATE,
    'Credit Card',
    (SELECT ServiceDescription FROM Services WHERE ServiceID = 1),
    1,
    1);

--Find the availability of all rooms:
SELECT RoomID, Name, Availability
FROM Rooms
WHERE Availability = TRUE;

