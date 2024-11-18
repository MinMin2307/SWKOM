import pytesseract
from pdf2image import convert_from_path

def perform_ocr(file_path):
    try:
        # Convert PDF pages to images
        images = convert_from_path(file_path)

        # Perform OCR on each image
        text = ""
        for image in images:
            text += pytesseract.image_to_string(image)

        return text.strip()
    except Exception as e:
        raise RuntimeError(f"Error during OCR processing: {e}")
