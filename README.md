# Comics Explorer & Recommendation System

## Overview
This project is a **desktop application for comic book enthusiasts**, developed as part of an academic software engineering course.  
It allows users to **search, explore, manage, and receive recommendations for comics and characters**, inspired by platforms such as Comic Vine.

The application provides both **guest and authenticated modes**. Logged-in users can manage personal collections, track reading progress, and receive personalized recommendations based on their preferences and activity.

---

## Features

###  Search
- Search comics by title
- Search characters by name
- Combined search (titles and characters)
- Access to detailed information:
  - Description
  - List of appearances
  - Publication dates
  - Related characters and comics

###  Recommendations
- General recommendations (recent comics, classics, popular titles)
- Personalized recommendations for logged-in users:
  - Favorite decades
  - Ongoing series
  - Reading habits

###  Collection Management
- Add comics to favorites
- Add comics to a purchase list
- Personal library with reading progress tracking
- Update reading status (not started, in progress, completed)

---

## User Personas
To guide the design of the application, two user personas were defined:

- **Alice** – A casual reader looking for general information and discovery suggestions  
- **Bob** – A passionate collector seeking precise recommendations and advanced library management

---

## Technical Stack
- **Programming Language:** Java 21
- **GUI:** Java Swing
- **Build Tool:** Maven
- **Database:** SQLite (local)
- **External API:** Comic Vine API
- **Architecture:** Desktop application (no web version, no remote server)

---

## Project Structure
comics-explorer-java/
├── src/
│   └── main/
│       └── java/
│           └── info7/
│               ├── affichage_achat              # Purchase list interface
│               ├── affichage_bibliotheque       # Library interface
│               ├── affichage_comics             # Comic details view
│               ├── affichage_favoris            # Favorites view
│               ├── affichage_personnage         # Character details view
│               ├── bibliotheque                 # Reading progress management
│               ├── connexion                    # User authentication and account creation
│               ├── page_accueil                 # Home page and general recommendations
│               ├── page_recherche_personnage    # Character search results
│               ├── page_recherche_titre         # Comic title search results
│               ├── page_recherche_tout          # Combined search results
│               └── suggestions_personnalisees   # Personalized recommendation algorithm
├── images/
│   ├── ClassDiagram.jpg                         # UML class diagram
│   └── BddDiagram.jpg                           # Database schema diagram
├── pom.xml                                     # Maven configuration file
├── README.md                                   # Project documentation
├── bdd_compte.db                               # Local SQLite database
└── .gitignore                                  # Git ignore rules


---

## Database

The application uses a **local SQLite database** to store user-related data, including:
- User accounts
- Favorite comics
- Purchase lists
- Personal libraries
- Reading progress status

This local database approach allows the application to run fully offline without requiring any external server or cloud infrastructure.

A database schema diagram is available in:
- `images/BddDiagram.jpg`

---

## Class Diagram

A UML class diagram illustrating the overall architecture and relationships between the main components is available in:
- `images/ClassDiagram.jpg`

---

## Prerequisites

Before running the application, ensure that the following requirement is met.

### Java 21

This project requires **Java 21**.

Java can be downloaded from:
- https://jdk.java.net/21/
- https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html

Verify the installation with:
```bash
java -version

## Dependencies

All dependencies are managed using **Maven** to ensure consistency and ease of build across different environments.

The main libraries used in this project are:
- **JSON Simple (1.1.1)** – Used for parsing and handling JSON data returned by the API
- **OkHttp (4.9.3)** – HTTP client used to communicate with the Comic Vine API
- **SQLite JDBC (3.42.0.0)** – JDBC driver for the local SQLite database
- **MySQL Connector (8.0.33)** – JDBC connector (optional usage)

All dependencies and their versions are defined in the `pom.xml` file.

---

## Installation

Follow the steps below to install and run the application locally.

### Step 1: Clone the repository

```bash
git clone https://github.com/your-username/comics-explorer-java.git
cd comics-explorer-java

### Step 2: Build the project

Use Maven to compile the project and download all required dependencies.

```bash
mvn clean install
```

This command will:
- Compile the source code
- Resolve and download all dependencies
- Package the application

---

### Step 3: Run the application

Run the application using Maven by specifying the main class:

```bash
mvn exec:java -Dexec.mainClass="info7.page_accueil.Page_accueil"
```

Alternatively, the application can be launched directly from the main class:
- src/main/java/info7/page_accueil/Page_accueil.java

---

## API Usage

This project uses the **Comic Vine API** to retrieve comic and character data.

Please note:
- Each API key is limited to **200 requests per hour**
- Excessive usage may result in temporary access restrictions

For security reasons, **API keys must not be committed** to the public repository.

---

## Known Limitations

- The application does not support remote or cloud-based databases
- Desktop-only application (no web or mobile version)
- API rate limits may restrict intensive usage

---

## Academic Context

This project was developed as part of an **academic software engineering course** and demonstrates:
- Object-oriented programming in Java
- Desktop application development using Java Swing
- External API integration
- Local data persistence with SQLite
- Team-based software development

---

## Authors

- Assia BELLA BACI
- Chayma HARGANE
- Thibault LEPINE
- Yazid EL MAHI
- Tristan BART

---

## License

This project was developed for **academic purposes only**.  
No commercial use is intended.
