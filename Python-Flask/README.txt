📊 Grp7Final: Data Upload, Visualization, and Editing Dashboard

This web application allows users to securely upload CSV files, view, analyze,
edit data tables, and generate interactive charts. It includes authentication,
file management, undo options, chart filters, and more.

---

🔧 Features

- 📁 Upload and manage CSV files
- 🔐 Secure login and registration
- 🧮 Interactive data table with:
    - Add/Remove rows and columns
    - Excel-style dropdown filters for each column
    - Editable cells
    - Undo up to 3 steps
- 📊 Chart generation:
    - Bar, Line, Pie, and TreeMap charts using Google Charts
- 🌙 Theme toggle (light/dark)
- 🗑 Delete files from dashboard

---

📂 Folder Structure

Grp7Final/
│
├── app.py               # Main Flask backend
├── config.py            # Configuration
├── requirements.txt     # Dependencies
├── uploads/             # Uploaded CSVs (auto-created)
├── instance/            # Flask instance folder (used for secret keys/db)
├── static/
│   └── js/
│       └── analysis.js  # JavaScript logic for edit/analyze page
├── templates/
│   ├── base.html        # Base layout
│   ├── analysis.html    # Data analysis page
│   ├── dashboard.html   # File dashboard
│   ├── home.html        # Landing page
│   ├── login.html       # Login screen
│   ├── register.html    # Register screen
│   ├── upload.html      # Upload form
│   └── others...        # (templates and actions)

---

🚀 Getting Started

1. Clone or Copy the App Files

Ensure you have the full "Grp7Final" folder with all subfolders and files.


2. Run cmd as administrator (recommended) and set Up the Virtual Environment (Recommended)

>python -m venv venv

# On Windows:
>venv\Scripts\activate
# On macOS/Linux:
>source venv/bin/activate


3. Install Required Packages

>pip install -r requirements.txt
(if this doesn't work then clearly define the path of "requirements.txt" file. Example, C:/Users/"Yash Mehar"/Downloads/Grp7Final/requirements.txt)


4. Run the App

>python app.py
(Again, if it doesn't work clearly define the path of "app.py")

Once the app starts running, open your browser and navigate to 'http://127.0.0.1:5000' to access the application.

---

🔑 Credentials (Necessary for new users)

Register a new user before uploading/viewing files.
Login with you credentials, for pre-exiting users.

---

📎 Notes

- All uploaded files are saved in the uploads/ folder.
- TreeMap, PieChart and other charts are powered by Google Charts — internet connection is required.
- Undo works for last 2–3 actions (row/column edits or deletions).
- Only .CSV files work.
- Make sure your CSVs are clean and UTF-8 encoded for best results.

---

📬 Contact

Maintained by Group 7 
Mark Leitao     -  200613614
Muskan Bhasin   -  200619439
Dima Adil       -  200345213
Tanvi Singh     -  200622238
Yash Mehar      -  200624783

Feel free to reach out for requesting assistance or bug reports.
