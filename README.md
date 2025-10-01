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

## Repository Access
This repository is set to **public**, ensuring that the team can easily access all project deliverables.  
If any access issues occur, please notify me so I can add specific collaborators.
```  

---
