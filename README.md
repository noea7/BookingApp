# BookingApp
Appointment booking application. Assignment for NFQ

### Running the App
- Open booking-api with IntellijIdea or an IDE of your choice and donwload maven dependencies
- Run BookingApiApplication
- Open booking-ui with vsCode or similar editor.
- Run npm install
- Run npm start
- Log in to access service department screen and visit management for specialists. Credentials ghouse/house, jwilson/wilson, mgrey/grey, cyang/yang, akarev/karev for different specialists
- If encountering any authentication issues, clear local storage and refresh page.

### Using the App
- Any public user can create a reservation, choosing a specialist and entering their email. Save the reservation code for later use
- Reservation can be viewed with the reservation code. It can only be cancelled when the same email used for registration is provided
- Any registered user(specialist) can see the service department screen, where all visits that are in progress, as well as a predefined number of upcoming visits (currently configured to 7) are displayed
- A specialist can see the list of their in progress/upcoming visits in my reservations tab, where they can start and end them.
  
