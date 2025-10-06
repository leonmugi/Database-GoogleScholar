## Repository
https://github.com/leonmugi/Database-GoogleScholar/tree/main/CH3S2_codigo

# Database-GoogleScholar
A project about use an API to collect information in google Schoolar and save it in a Database
# Google Scholar Integration Project

## Project Purpose  
The goal of this project is to **automate the integration of academic information** (authors, research articles, and citation data) from **Google Scholar** using the **SerpApi service**.  
The project demonstrates how to consume an API in Java, apply the **MVC design pattern**, and finally integrate the retrieved data into a **MySQL database** for storage, queries, and analysis.  

---

## Key Functionalities  
- **API Consumption**: Perform GET requests to the Google Scholar API using SerpApi.  
- **Data Processing**: Parse and structure the returned JSON responses in Java.  
- **MVC Implementation**: Apply the Model–View–Controller design pattern to separate logic, data, and presentation.  
- **Database Integration**: Store author and article information in a relational database (MySQL).  
- **Error Handling**: Manage network issues, API usage limits (429), and database exceptions.  
- **Deliverables**: Provide documentation, technical analysis, and a demo video presentation.  

---

## Project Relevance  
Research institutions and universities often need efficient ways to organize and analyze academic data.  
This project addresses the challenge of **manual data collection from Google Scholar** by:  
- Automating the retrieval of author profiles and article metadata.  
- Structuring the data for **reliable storage** in a database.  
- Enabling integration with institutional research systems for long-term use.  

---

## Project Development Workflow  
1. Research and document the Google Scholar API (via SerpApi).  
2. Develop Java code to perform GET requests, parse results, and structure the data.  
3. Implement the MVC pattern:  
   - **Model**: Author and Article classes.  
   - **View**: Console-based output of results.  
   - **Controller**: Handle GET requests, process responses, and update the view.  
4. Create and configure a **MySQL database** to store author and article information (title, year, link, citation count, keywords, abstract, etc.).  
5. Integrate the database layer with the MVC application to persist and query results.  
6. Add robust error handling for API usage limits, HTTP errors, and SQL exceptions.  

---

## How to Run the Project  

### Requirements  
- Java 17+  
- Maven  
- MySQL Server  
- SerpApi account and API key  

### Setup  
1. Clone the repository.  
2. Set the environment variable `SERPAPI_KEY`.  
3. Run the Java application (initial version prints results in console).  
4. Configure MySQL connection in `dao/MySql.java` and execute `schema.sql` to create the database.  

---

## Access & Evaluation  
- Ensure the repository is public or grant access to the **Digital NAO team**.  
- Include all deliverables inside the repository:  
  - Code (organized in packages).  
  - Documentation (API technical report).  
  - Database schema file.  
  - Final PDF report and MP4 video.  

---

# 📑 Technical Documentation (Google Scholar API via SerpApi)

### 🔹 Endpoints  
- **Base endpoint**:  
  ```
  https://serpapi.com/search.json?engine=google_scholar
  ```  
- Other engines:  
  - `engine=google` → Standard Google Search.  
  - `engine=google_scholar_profiles` → Author profiles.  
  - `engine=google_scholar_author` → Author details + articles.  

---

### 🔹 Authentication  
- Each user has a unique **API key** obtained from the [SerpApi dashboard](https://serpapi.com/).  
- Authentication is done by adding the `api_key` parameter to the request URL.  
- Example:  
  ```
  https://serpapi.com/search.json?engine=google_scholar&q=deep+learning&api_key=YOUR_API_KEY
  ```  
- Without a valid key → returns `401 Unauthorized`.  

---

### 🔹 Query Parameters  
Common parameters:  
- `q` → Search query (e.g., `q=deep+learning`).  
- `hl` → Language (`hl=en`, `hl=es`).  
- `num` → Number of results per page (max 20).  
- `start` → Pagination index (0 = first page, 10 = second page if `num=10`).  
- `author_id` → Scholar author identifier (used in `google_scholar_author`).  
- `cites` → Search for works citing a specific article.  
- `as_ylo` / `as_yhi` → Year filters (min and max year).  

Example:  
```
https://serpapi.com/search.json?engine=google_scholar&q=AI&hl=en&num=10&start=0&api_key=YOUR_API_KEY
```  

---

### 🔹 Response Format  
- Returned as **JSON**.  
- Main sections:  
  - `search_metadata` → request details.  
  - `search_parameters` → parameters used.  
  - `search_information` → additional info (time, results).  
  - `organic_results` → list of results (title, link, snippet, citations).  
  - `pagination` → next/previous page links.  

Example (trimmed):  
```json
{
  "search_metadata": { "status": "Success" },
  "search_parameters": { "engine": "google_scholar", "q": "deep learning" },
  "organic_results": [
    { "title": "Deep Learning", "link": "https://example.com", "cited_by": {"value": 1200} }
  ]
}
```  

---

### 🔹 Usage Limits  
- **Free plan** → limited monthly searches.  
- **Paid plans** → higher quotas.  
- If limits exceeded → error `429 Too Many Requests`.  
- Best practice: implement retries and monitor quota in dashboard.  

---

### 🔹 Code Examples  

#### Java (HttpClient + Gson)
```java
HttpClient client = HttpClient.newHttpClient();
HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
System.out.println(res.body());
```  

#### Python (requests)
```python
import requests, os
url = "https://serpapi.com/search.json"
params = {"engine":"google_scholar","q":"deep learning","api_key":os.getenv("SERPAPI_KEY")}
res = requests.get(url, params=params)
print(res.json())
```  

#### JavaScript (Node.js + fetch)
```javascript
import fetch from "node-fetch";
const url = "https://serpapi.com/search.json?engine=google_scholar&q=deep+learning&api_key=" + process.env.SERPAPI_KEY;
const res = await fetch(url);
const data = await res.json();
console.log(data);
``` 

## Repository Access
This repository is set to **public**, ensuring that the team can easily access all project deliverables.  
If any access issues occur, please notify me so I can add specific collaborators.
 

---

### 🔹 SPRINT 2

# Project Overview
This project demonstrates how to combine Java, the Model–View–Controller (MVC) pattern, a MySQL database, and a public API to collect and organize information from Google Scholar. The app asks for a search term, finds the first relevant article that actually lists authors, enriches those author records when possible, saves them to MySQL, and can display the saved table in a simple window.

--------------------------------------------------------------------------

## Java with the MVC structure
What it is: MVC keeps code tidy by separating responsibilities.

- Model — the data and how we store it (our Author object and the database access code).
- View — how information is shown to the user (console output and a small table window).
- Controller — the “traffic cop” that talks to the API, turns results into data, and tells the Model and View what to do.

Why it matters: This separation makes the code easier to read, test, and change. If you swap MySQL for another database or replace the console with a web page, the rest of the app can largely stay the same.

--------------------------------------------------------------------------

## API and SerpAPI
API (Application Programming Interface): a contract that lets software talk to other software.
SerpAPI: a service that exposes Google search results (including Google Scholar) through a developer-friendly API.

How we use it here:
- We call SerpAPI’s Google Scholar Search to get a list of results for the user’s query.
- We pick the first result that actually lists authors.
- For each author, if we can determine their Google Scholar author id (the `user=` value in profile links), we call SerpAPI’s Scholar Author endpoint to fetch citations and other basic details.

Note: Some results don’t include an author profile id. In those cases we store what we have (the author’s name and the article where it appeared).

--------------------------------------------------------------------------

## MySQL
What it is: a widely used relational database—well suited for small, structured datasets.

How we use it here:
- We store one row per author with columns:
  - `author_name`, `author_id` (if available), `citations` (if available),
  - `article_title` (where we found them), `profile_url`, and a timestamp.
- We add a uniqueness rule so the same author for the same article isn’t inserted twice.

--------------------------------------------------------------------------

## How the program is organized
The app consists of 7 classes arranged by MVC.

Models
- Author — a simple container for one author’s data:
  name, author id (if found), total citations (if found), the article title we used, and the author’s profile link.
- AuthorRepository — database access:
  `saveAll(...)` inserts authors into MySQL using a batch (fast, and ignores duplicates).
  `findAll()` reads the most recent rows so the table window can display them.
- Db — opens a MySQL connection using environment variables (`DB_URL`, `DB_USER`, `DB_PASS`). Keeping this separate avoids scattering connection details around the code.

Views
- ConsoleView — prints a clean, readable summary to the terminal: each author’s name, id, citations, and profile link if there is one.
- TableWindow — a small Swing (desktop) window that shows the saved table in a sortable grid with a “Close” button. It opens modally, so the program waits for the user to close it.

Controller
- ScholarController — the heart of the logic:
  Calls SerpAPI for Scholar search results, chooses the first result that actually has authors, extracts author names, tries to resolve a Scholar author id (from the result or by parsing the profile link), fetches citations when an id exists, and returns a clean list of Author objects ready to save.

Orchestrator
- MainApp — the app’s entry point (the “conductor”):
  Asks the user for a search phrase, invokes the controller to get authors for the first suitable article, saves them through the repository into MySQL, shows a summary in the console, and asks whether to open the table window. If the user agrees, it opens the modal window and returns to the prompt afterward. It repeats until the user types `salir`/`exit`.

--------------------------------------------------------------------------

## Program Flow
1) The program starts by asking the user to enter a word or phrase to search on Google Scholar.
2) Using the API, it retrieves the first article that appears in the results and extracts the authors listed for that article.
3) For each author, the program gathers these fields:
   - Author (name)
   - Author ID (Google Scholar profile id, if available)
   - Citations (total, if available)
   - Article Title (the article where the author was found)
   - Profile URL (link to the author’s Google Scholar profile, if available)
4) The collected records are saved into a MySQL database.
5) The program then displays the saved author information in the console and confirms that the data was successfully stored.
6) Next, the user is asked if they want to view the saved data in a simple window.
   - If the user says yes, a small interface opens showing the table.
   - When the window is closed, the program asks whether the user wants to perform another search or finish the program.
  
## SPRINT 3

# 📚 Google Scholar MVC (Java + MySQL)

A small MVC app that searches **two authors** on Google Scholar (via **SerpAPI**), collects the **top 3 articles** for each, validates/normalizes the data, stores it in **MySQL**, logs ingestion issues, and optionally shows a **table view** of saved records.

---

## ✨ At a Glance

- **Language:** Java  
- **Architecture:** Model–View–Controller (MVC)  
- **API:** SerpAPI (Google Scholar)  
- **DB:** MySQL (JDBC, prepared statements, unique constraints)  
- **UI:** Console + Swing table window  
- **Resilience:** HTTP 200 validation, simple retry/backoff, issue logging, null-safe placeholders

---

## 🧭 Table of Contents
- [Concepts (brief)](#-concepts-brief)
- [Project Structure (MVC)](#-project-structure-mvc)
- [Models & Repositories](#-models--repositories)
- [Controller](#-controller)
- [Views](#-views)
- [Program Flow](#-program-flow)
- [Data Model (tables)](#-data-model-tables)
- [Setup](#-setup)
- [Resilience & Quality](#-resilience--quality)
- [Maintenance SQL](#-maintenance-optional-sql)
- [What You Get](#-what-you-get)

---

## 🔎 Concepts (brief)

### 🔌 What is an API?
An **API** is a contract that lets software talk to software. You send a request (URL + parameters) and receive a structured response (e.g., JSON).

### 🗄️ What is a database?
A **database** stores and organizes data so you can save, query, and reuse it. Relational databases (like **MySQL**) use tables (rows/columns) and **SQL** for queries.

### 🔍 What is SerpAPI?
**SerpAPI** gives you Google results (including **Google Scholar**) as clean **JSON**—no scraping required.

### 🐬 What is MySQL?
**MySQL** is a fast, widely used relational DB, perfect for small/medium structured datasets.

### 🧩 What is MVC?
**MVC** separates responsibilities:
- **Model** → data & persistence  
- **View** → how information is shown  
- **Controller** → orchestrates API calls and app logic

This separation makes the code easier to read, test, and change.

---

## 🧱 Project Structure (MVC)

org.GoogleScholar
├── controller
│ └── ScholarController # Calls SerpAPI, validates, normalizes, logs issues
├── model
│ ├── Article # Article data (title, authors, abstract, etc.)
│ ├── ArticleRepository # JDBC batch inserts, read queries, dedupe
│ ├── Issue # Ingestion problem (message, HTTP status, raw snippet)
│ ├── IssueRepository # Persists issues, ensures table exists
│ └── Db # Centralized MySQL connection (env vars)
├── view
│ ├── ConsoleView # Human-readable console summary
│ └── TableWindow # Swing window with a read-only table
└── MainApp # Entry point: prompts → fetch → save → show


---

## 🧩 Models & Repositories

### `Article`
Holds one scholarly article:
- `author_query` (author we searched)  
- `article_id` *(when present; otherwise placeholder)*  
- `title`, `authors` *(comma-separated)*  
- `publication_date` *(year if available)*  
- `abstract` *(snippet if available)*  
- `link`  
- `keywords` *(computed from title/abstract)*  
- `citations` *(if available)*  
- `created_at`

> Missing upstream values are normalized to friendly placeholders (e.g., *“No se encontró fecha”*)—not raw `null`.

---

### `ArticleRepository`
- Batch inserts using **PreparedStatement** (safe & fast)  
- Unique constraint to avoid duplicate author/article combos  
- `findAll()` to populate the table window

---

### `Issue`
- Captures ingestion problems: author query, article title, short message (e.g., *Artículo sin autores*), optional HTTP status, optional trimmed raw JSON.

---

### `IssueRepository`
- Ensures `ingest_issues` exists  
- Batch-inserts issue records for later review

---

### `Db`
- One place for the MySQL connection using env vars: `DB_URL`, `DB_USER`, `DB_PASS`.

---

## 🎛️ Controller

### `ScholarController`
- Builds GET requests to **SerpAPI** (Google Scholar)  
- Validates **HTTP 200**; simple **retry/backoff** for transient failures  
- **Skips items without authors** and logs the incident  
- Extracts: `article_id`, `title`, `authors`, `publication_date (year)`, `abstract`, `link`, `cited_by`  
- **Computes keywords** from title/abstract (top frequent non-stopwords, excluding author surnames)  
- Normalizes missing values to user-friendly placeholders  
- Returns a clean `List<Article>` and a `List<Issue>`

---

## 🖥️ Views

### `ConsoleView`
- Prints what was saved and why something was skipped  
- Shows counts, links, and computed keywords

### `TableWindow`
- Swing window, read-only table  
- Columns sized so **abstract** is readable  
- Modal: the console waits until the window is closed

---

## ▶️ Program Flow

1. Prompt user for **two author names**.  
2. For each author, call SerpAPI → take the **first 3 results with authors**.  
3. Validate **HTTP 200** (retry/backoff on errors).  
4. Extract & normalize fields (no raw nulls).  
5. Save to **MySQL** (dedupe on unique key).  
6. **Log issues** (HTTP errors, missing authors/fields) into `ingest_issues`.  
7. Show a summary in the console and ask to open the **table window**.  
8. Repeat (another pair of authors) or **exit**.  
   - *(Optional)* Ask to **reset** tables (truncate) for a clean run.

---

## 🗃️ Data Model (tables)

### `articles`

| Column           | Type          | Notes                                |
|------------------|---------------|--------------------------------------|
| `id` (PK)        | INT           | AUTO_INCREMENT                       |
| `author_query`   | VARCHAR(255)  | Input author name                    |
| `article_id`     | VARCHAR(64)   | From SerpAPI (if present)            |
| `title`          | VARCHAR(512)  |                                      |
| `authors`        | VARCHAR(1024) | Comma-separated                      |
| `publication_date` | VARCHAR(32) | Parsed year or placeholder           |
| `abstract`       | TEXT          | Snippet or placeholder               |
| `link`           | VARCHAR(1024) |                                      |
| `keywords`       | VARCHAR(512)  | Computed from text                   |
| `citations`      | INT           | If available                         |
| `created_at`     | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP            |

> **Unique key** (e.g., `author_query + article_id + title`) enforces dedupe.

---

### `ingest_issues`

| Column         | Type          | Notes                               |
|----------------|---------------|-------------------------------------|
| `id` (PK)      | INT           | AUTO_INCREMENT                      |
| `author_query` | VARCHAR(255)  |                                     |
| `article_title`| VARCHAR(512)  |                                     |
| `issue`        | VARCHAR(255)  | Short message                       |
| `http_status`  | INT (NULL)    | When it’s an HTTP problem           |
| `raw_json`     | TEXT (NULL)   | Trimmed snippet for diagnosis       |
| `created_at`   | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP           |

---

## ⚙️ Setup

### 1) Environment variables
```bash
SERPAPI_KEY=your_key_here
DB_URL=jdbc:mysql://localhost:3306/scholar?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USER=scholar_user
DB_PASS=StrongPassword!
```
### 2) Create schema (once)

CREATE DATABASE IF NOT EXISTS scholar
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE scholar;

🧪 Resilience & Quality

✅ HTTP 200 validation before parsing

🔁 Simple retry/backoff on transient failures

🧯 Issue logging to ingest_issues (skipped items & HTTP errors)

🚫 Null-safe placeholders to keep UI clean

🔐 Prepared statements (no SQL injection)

⚡ Batch inserts + indexes/unique keys for performance & dedupe

### 🧹 Maintenance (optional SQL)
Reset tables (clean run):

USE scholar;
TRUNCATE TABLE articles;
TRUNCATE TABLE ingest_issues;

Only reset counters:

ALTER TABLE articles AUTO_INCREMENT = 1;
ALTER TABLE ingest_issues AUTO_INCREMENT = 1;

###  🏁 What You Get

A tidy MVC Java app that:

Searches two authors, collects 3 articles each

Validates, normalizes, logs issues, and persists to MySQL

Shows a console summary and an optional table window

Is easy to extend: more fields, more sources, or a different UI with minimal changes

