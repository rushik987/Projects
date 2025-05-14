
# Importing Flask and necessary modules for web app, routing, sessions, and database
from flask import Flask, render_template, request, redirect, url_for, flash, session, jsonify, send_from_directory
from flask_sqlalchemy import SQLAlchemy
from flask_login import LoginManager, login_user, login_required, logout_user, current_user, UserMixin


# For password hashing and verification
from werkzeug.security import generate_password_hash, check_password_hash

# OS module for file and directory operations
import os

# Pandas is used for reading and processing CSV files
import pandas as pd

# For timestamping uploads
from datetime import datetime, timedelta

app = Flask(__name__)                         # Initialize the Flask app and set configurations
app.secret_key = 'your_secret_key_here'
app.config['UPLOAD_FOLDER'] = 'uploads'       # Folder path to save uploaded files
                                              # Set up SQLite database URI for SQLAlchemy
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///site.db'
db = SQLAlchemy(app)                          # Initialize SQLAlchemy with the Flask app
login_manager = LoginManager(app)             # Initialize Flask-Login manager
login_manager.login_view = 'login'

if not os.path.exists(app.config['UPLOAD_FOLDER']):   # Create folder path to save uploaded files
    os.makedirs(app.config['UPLOAD_FOLDER'])          # if it does not exist

class User(UserMixin, db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(120), unique=True, nullable=False)
    password = db.Column(db.String(120), nullable=False)

def get_edt_fixed():                          # For EDT timezone
    return datetime.utcnow() - timedelta(hours=4)

class UploadedFile(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    filename = db.Column(db.String(255), nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    upload_date = db.Column(db.DateTime, default=get_edt_fixed)

@login_manager.user_loader
def load_user(user_id):
    return User.query.get(int(user_id))

@app.route('/')
def home():
    return render_template('home.html')

@app.route('/register', methods=['GET', 'POST'])
def register():                                       # Handles user registration logic
    if request.method == 'POST':
        username = request.form['username']
        password = generate_password_hash(request.form['password'], method='pbkdf2:sha256')
        if User.query.filter_by(username=username).first():
            flash('Username already exists')
        else:
            new_user = User(username=username, password=password)
            db.session.add(new_user)
            db.session.commit()
            flash('Registered successfully! Please log in.')
            return redirect(url_for('login'))
    return render_template('register.html')

@app.route('/login', methods=['GET', 'POST'])
def login():                                         # Handles login validation and session
    if request.method == 'POST':
        username = request.form['username']
        password_input = request.form['password']
        user = User.query.filter_by(username=username).first()
        if user and check_password_hash(user.password, password_input):
            login_user(user)
            return redirect(url_for('dashboard'))
        flash('Invalid credentials')
    return render_template('login.html')

@app.route('/logout')
@login_required
def logout():                                        # Clears session and logs user out
    logout_user()
    return redirect(url_for('login'))

@app.route('/upload', methods=['GET', 'POST'])
@login_required
def upload_file():                                   # File upload logic, saves and stores metadata
    if request.method == 'POST':
        file = request.files['file']
        if file:
            filepath = os.path.join(app.config['UPLOAD_FOLDER'], file.filename)
            file.save(filepath)
            new_file = UploadedFile(filename=file.filename, user_id=current_user.id)
            db.session.add(new_file)
            db.session.commit()
            flash('File uploaded successfully!')
            return redirect(url_for('dashboard'))
    return render_template('upload.html')

@app.route('/dashboard')
@login_required
def dashboard():                                     # Displays uploaded files for current user
    files = UploadedFile.query.filter_by(user_id=current_user.id).all()
    # Add row/col count for display
    for f in files:
        path = os.path.join(app.config['UPLOAD_FOLDER'], f.filename)
        try:
            df = pd.read_csv(path)
            f.rows = len(df)
            f.columns = len(df.columns)
        except:
            f.rows = f.columns = 0
    return render_template('dashboard.html', files=files)

@app.route('/file/<int:file_id>')
@login_required
def view_file_data(file_id):                         # Loads the analysis view for selected file
    return render_template('analysis.html', file_id=file_id)

@app.route('/api/file/<int:file_id>', methods=['GET'])
@login_required 
def get_file_data_api(file_id):                      # Returns file data as JSON for chart/table rendering
    file = UploadedFile.query.get_or_404(file_id)
    if file.user_id != current_user.id:
        return jsonify({'error': 'Unauthorized'}), 403
    filepath = os.path.join(app.config['UPLOAD_FOLDER'], file.filename)
    try:                                             # Folder path to save uploaded files
        df = pd.read_csv(filepath)
        df.fillna("", inplace=True)
        return jsonify({
            'columns': df.columns.tolist(),
            'data': df.values.tolist()
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/api/file/<int:file_id>', methods=['PATCH'])
@login_required
def patch_file_cell(file_id):                   # API endpoint to update a specific cell in CSV
    data = request.get_json()
    file = UploadedFile.query.get_or_404(file_id)
    if file.user_id != current_user.id:
        return jsonify({'error': 'Unauthorized'}), 403       # Folder path to save uploaded files
    filepath = os.path.join(app.config['UPLOAD_FOLDER'], file.filename)
    df = pd.read_csv(filepath)
    df.at[data['row'], data['column']] = data['new_value']
    df.to_csv(filepath, index=False)
    return jsonify({'message': 'Value updated'})

@app.route('/download/<int:file_id>')
@login_required
def download_file(file_id):                           # Allows user to download uploaded CSV
    file = UploadedFile.query.get_or_404(file_id)
    if file.user_id != current_user.id:
        return "Unauthorized", 403
    return send_from_directory(app.config['UPLOAD_FOLDER'], file.filename, as_attachment=True)

@app.route('/api/file/<int:file_id>', methods=['DELETE'])
@login_required
def delete_file_api(file_id):                         # Deletes file entry and removes file from server
    file = UploadedFile.query.get_or_404(file_id)
    if file.user_id != current_user.id:
        return jsonify({'error': 'Unauthorized'}), 403
    os.remove(os.path.join(app.config['UPLOAD_FOLDER'], file.filename))
    db.session.delete(file)
    db.session.commit()
    return jsonify({'message': 'File deleted successfully'})


@app.route('/toggle-theme')
def toggle_theme():                               # Toggles light/dark theme in session
    current = session.get('theme', 'dark')
    session['theme'] = 'light' if current == 'dark' else 'dark'
    return '', 204  # No Content

# Start the Flask server and ensure DB is initialized
if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.run(debug=True)
