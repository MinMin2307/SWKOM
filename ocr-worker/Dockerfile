# Base Python image
FROM python:3.9-slim

# Install system dependencies for Tesseract and Poppler
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    libpoppler-cpp-dev \
    poppler-utils \
    && apt-get clean

# Set working directory
WORKDIR /app

# Copy requirements and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application files
COPY . .

# Expose any necessary ports (if applicable)
EXPOSE 5672

# Run the application
CMD ["python", "app.py"]
