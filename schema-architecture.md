Section 1: Architecture Summary
This Spring Boot application utilizes a hybrid architectural approach by incorporating both MVC and REST controllers. Thymeleaf templates are implemented to provide server-side rendered dashboards for Admins and Doctors, while REST APIs serve the remaining modules to ensure flexibility.

The system interacts with two distinct database types: MySQL for structured relational data—including patients, doctors, appointments, and admin records—and MongoDB specifically for managing prescription documents. To maintain a clean separation of concerns, all controllers route requests through a common Service Layer, which contains the business logic before delegating data operations to the appropriate JPA or MongoDB repositories.

Section 2: Numbered Flow of Data and Control
User Entry: The user accesses the system through the AdminDashboard, Doctor dashboard, or various Appointment pages.

Routing: The incoming request is routed to either a Thymeleaf controller for UI rendering or a REST controller for API data.

Service Invocation: The controller calls the Service Layer, which acts as the central hub for processing application logic.

Repository Delegation: The service layer identifies the data type and delegates the task to either the JPA Repository (for MySQL) or the Mongo Repository (for MongoDB).

Persistence: * MySQL handles relational entities such as Patient, Doctor, and Appointment using JPA mapping.

MongoDB stores Prescription data as document models.

Data Return: The database returns the requested information back through the repository to the service layer, where it is finalized.

Response Delivery: The controller receives the processed data and delivers it to the user as either a rendered HTML page or a JSON response.
