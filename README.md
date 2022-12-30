# Doctor Appointment
Doctor Appointment helps you to create adding your available times as a doctor and also reserving a time as a patient.
Doctor View (admin):
* Add your available time, and we split it into sections.
* See all periods, including open and taken times.
* Delete any Reservation you want, exceptions:taken times.
Patient View (user):
* See all open times of Doctor.
* Reserve any time you need by entering the name and phone number.
* See all the recent reserved times.
# Getting Started
* Docker:
  * docker build -t doctor-reservation
  * docker run -dp 3000:3000 getting-started
* Install Maven, Jdk11:
  * cd into the project's root directory
  * ./mvnw clean package
  * java -jar target/Reservation-0.0.1-SNAPSHOT.jar com.blu.reservation.DoctorReservationApplication
Now you can use this app by :
- mvn install
### Guides
This application does not include tokens and authentication process, 
but every rest service, gets a userId as input to indicate who you are when using the app.
Just doctors can see some services.



