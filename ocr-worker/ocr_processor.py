import pytesseract
from pdf2image import convert_from_path
import os
import logging

def perform_ocr(file_path):
    try:
        # Check if the file exists
        if not os.path.exists(file_path):
            raise FileNotFoundError(f"The file {file_path} does not exist.")

        # Log the OCR process start
        logging.info(f"Starting OCR for file: {file_path}")

        # Convert PDF pages to images
        images = convert_from_path(file_path)

        if not images:
            raise RuntimeError("No pages found in the PDF file for OCR processing.")

        # Perform OCR on each image
        text = ""
        for page_number, image in enumerate(images, start=1):
            page_text = pytesseract.image_to_string(image)
            logging.info(f"Processed page {page_number}, extracted text size: {len(page_text)} characters.")
            text += page_text + "\n"

        # Log the completion of OCR
        logging.info(f"Completed OCR for file: {file_path}. Total extracted text size: {len(text)} characters.")
        
        return text.strip()

    except Exception as e:
        logging.error(f"Error during OCR processing for file {file_path}: {e}")
        raise RuntimeError(f"Error during OCR processing: {e}")
