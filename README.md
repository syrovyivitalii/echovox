# Echovox File Manager

**Echovox File Manager is a project designed to upload, validate, and manage XML files, converting them to JSON storage**

## ⚙️ Getting Started
### Prerequisites
* Docker installed
* Java 21 (optional, if running locally without Docker)

### Installation
* Clone the Repository:
`git clone https://github.com/syrovyivitalii/echovox.git `

`cd .\echovox\`

### Run the Application

`docker-compose up --build`

## 📄 Usage

### Swagger
* Access the REST API documentation using [Swagger](http://localhost:8080/swagger-ui/index.html#/)

### Endpoints Overview:

- **File Operations**
  - **POST:** `api/v1/files`: upload an XML file (validates filename `customer_type_date.xml`, converts to JSON)
  - **PUT:** `api/v1/files`: replace an existing file (overwrites if name matches)
  - **DELETE:** `api/v1/files/{filename}`: delete a file permanently
  - **GET:** `api/v1/files/{filename}`: fetch parsed JSON content of a specific file

- **Search & Filtering**
  - **GET:** `api/v1/files/by/date?date={yyyy-mm-dd}`: fetch all files matching a specific date
  - **GET:** `api/v1/files/by/customer?customer={name}`: fetch all files matching a customer name
  - **GET:** `api/v1/files/by/type?type={doctype}`: fetch all files matching a document type

## 🛠 Technologies Used

* Java 21
* Maven
* Spring Boot 3
* Jackson (XML/JSON Processing)
* MapStruct
* Lombok
* Docker
* OpenAPI (Swagger)
* REST
