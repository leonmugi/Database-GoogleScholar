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
