
-- Table for Members
CREATE TABLE Members (
    MemberID SERIAL PRIMARY KEY,
    FirstName VARCHAR(255),
    LastName VARCHAR(255),
    Email VARCHAR(255) UNIQUE,
    Password VARCHAR(255),
    DoB TEXT,
    StreetName VARCHAR(255),
    City VARCHAR(255),
    Province VARCHAR(255),
    PostalCode VARCHAR(255)
);


-- Table for Personalized Dashboard
CREATE TABLE PersonalizedDashboards (
    DashboardID SERIAL PRIMARY KEY,
    ExerciseRoutine TEXT,
    FitnessGoals TEXT,
    FitnessAchievements Text,
    HealthMetrics DOUBLE PRECISION,
    HealthStatistics TEXT,
	MemberID INT UNIQUE,
    FOREIGN KEY (MemberID) REFERENCES Members(MemberID)
);

-- Table for Trainers
CREATE TABLE Trainers (
    TrainerID SERIAL PRIMARY KEY,
    FirstName VARCHAR(255),
    LastName VARCHAR(255),
    Email VARCHAR(255) UNIQUE,
    Password VARCHAR(255),
    Availability TIME[]
);

-- Table for Classes
CREATE TABLE Classes (
    ClassID SERIAL PRIMARY KEY,
    Name VARCHAR(255),
    Description TEXT,
    ScheduledTime Time
);

-- Table for Administrative Staff
CREATE TABLE AdministrativeStaff (
    StaffID SERIAL PRIMARY KEY,
    FirstName VARCHAR(255),
    LastName VARCHAR(255),
    Email VARCHAR(255) UNIQUE,
    Password VARCHAR(255)
);

-- Table for Rooms
CREATE TABLE Rooms (
    RoomID SERIAL PRIMARY KEY,
    Name VARCHAR(255),
    RoomType VARCHAR(255),
    Availability BOOLEAN,
    StaffID INT,
    FOREIGN KEY (StaffID) REFERENCES AdministrativeStaff(StaffID)
);

-- Table for Equipment
CREATE TABLE Equipment (
    EquipmentID SERIAL PRIMARY KEY,
    EquipmentType VARCHAR(255),
    Description TEXT,
    StaffID INT,
    FOREIGN KEY (StaffID) REFERENCES AdministrativeStaff(StaffID)
);



-- Table for Personal Training Sessions
CREATE TABLE PersonalTrainingSessions (
    SessionID SERIAL PRIMARY KEY,
    SessionSchedule DATE,
    Time TIME,
    MemberID INT,
    FOREIGN KEY (MemberID) REFERENCES Members(MemberID),
    TrainerID INT,
    FOREIGN KEY (TrainerID) REFERENCES Trainers(TrainerID)
);

-- Table for Services
CREATE TABLE Services (
    ServiceID SERIAL PRIMARY KEY,
    ServiceDescription VARCHAR(255),
    Amount INT
);

-- Table for Billing
CREATE TABLE Billing (
    TransactionID SERIAL PRIMARY KEY,
    Date DATE,
    PaymentMethod VARCHAR(255),
    Service VARCHAR(255),
    MemberID INT,
    FOREIGN KEY (MemberID) REFERENCES Members(MemberID),
    ServiceID INT,
    FOREIGN KEY (ServiceID) REFERENCES Services(ServiceID)
);

-- Table for SessionDuration
CREATE TABLE SessionDuration (
    Duration INT,
    MemberID INT,
    FOREIGN KEY (MemberID) REFERENCES Members(MemberID),
    TrainerID INT,
    FOREIGN KEY (TrainerID) REFERENCES Trainers(TrainerID)
);

-- Associative table for the many-to-many relationship between Members and Classes
CREATE TABLE Members_Classes (
    MemberID INT,
    ClassID INT,
    PRIMARY KEY (MemberID, ClassID),
    FOREIGN KEY (MemberID) REFERENCES Members(MemberID),
    FOREIGN KEY (ClassID) REFERENCES Classes(ClassID)
);

-- Associative table for the relationship between Classes and Rooms
CREATE TABLE Classes_Rooms (
    ClassID INT,
    RoomID INT,
    PRIMARY KEY (ClassID, RoomID),
    FOREIGN KEY (ClassID) REFERENCES Classes(ClassID),
    FOREIGN KEY (RoomID) REFERENCES Rooms(RoomID)
);

-- Associative table for the relationship between Rooms and Equipment
CREATE TABLE Rooms_Equipment (
    RoomID INT,
    EquipmentID INT,
    PRIMARY KEY (RoomID, EquipmentID),
    FOREIGN KEY (RoomID) REFERENCES Rooms(RoomID),
    FOREIGN KEY (EquipmentID) REFERENCES Equipment(EquipmentID)
);
