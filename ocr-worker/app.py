import pika
import json
import os
import logging
from ocr_processor import perform_ocr
from minio import Minio
from minio.error import S3Error

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# RabbitMQ configuration from environment variables
RABBITMQ_HOST = os.getenv('RABBITMQ_HOST', 'localhost')
RABBITMQ_PORT = int(os.getenv('RABBITMQ_PORT', 5672))
RABBITMQ_USERNAME = os.getenv('RABBITMQ_USERNAME', 'rabbitmq')
RABBITMQ_PASSWORD = os.getenv('RABBITMQ_PASSWORD', 'rabbitmq')
OCR_QUEUE = os.getenv('OCR_QUEUE', 'ocr_queue')
OCR_RESPONSE_QUEUE = os.getenv('OCR_RESPONSE_QUEUE', 'ocr_response_queue')

# MinIO configuration from environment variables
MINIO_URL = os.getenv('MINIO_URL', 'http://localhost:9000')
MINIO_BUCKET = os.getenv('MINIO_BUCKET_NAME', 'ocr-bucket')
MINIO_ACCESS_KEY = os.getenv('MINIO_ACCESS_KEY', 'gMbdK6OJRhNnq5PYSxLJ')
MINIO_SECRET_KEY = os.getenv('MINIO_SECRET_KEY', 'ZCB9ceGSxDmk48YwM26ixzrpoF7E9Xx5dXKJhnoo')

# Initialize MinIO client
minio_client = Minio(
    MINIO_URL.replace("http://", "").replace("https://", ""),
    access_key=MINIO_ACCESS_KEY,
    secret_key=MINIO_SECRET_KEY,
    secure=MINIO_URL.startswith("https")
)

def download_file_from_minio(bucket_name, object_name, local_file_path):
    """Download a file from MinIO."""
    try:
        minio_client.fget_object(bucket_name, object_name, local_file_path)
        logging.info(f"Downloaded file from MinIO: {object_name} -> {local_file_path}")
        return local_file_path
    except S3Error as e:
        logging.error(f"Failed to download file from MinIO: {e}")
        raise

def on_message(channel, method, properties, body):
    try:
        logging.info(f"Received body: {body}")
        message = json.loads(body)
        document_id = message.get("id")
        file_path = message.get("path")

        if not document_id or not file_path:
            raise ValueError("Message must contain 'id' and 'path'")

        local_file_path = f"/tmp/{os.path.basename(file_path)}"

        # Download the file from MinIO
        download_file_from_minio(MINIO_BUCKET, file_path, local_file_path)

        # Perform OCR
        try:
            ocr_result = perform_ocr(local_file_path)
        except FileNotFoundError:
            logging.error(f"File not found: {local_file_path}")
            ocr_result = f"Error: File not found: {local_file_path}"
        except Exception as e:
            logging.error(f"OCR processing failed: {e}")
            ocr_result = f"Error: OCR processing failed: {e}"

        # Send the OCR result back to the response queue
        response = {"id": document_id, "ocrResult": ocr_result}
        channel.basic_publish(
            exchange='',
            routing_key=OCR_RESPONSE_QUEUE,
            body=json.dumps(response)
        )
        logging.info(f"Sent OCR result for document ID: {document_id}")

        # Acknowledge the message
        channel.basic_ack(delivery_tag=method.delivery_tag)
    except Exception as e:
        logging.error(f"Error processing message: {e}")
        channel.basic_nack(delivery_tag=method.delivery_tag, requeue=False)

def start_ocr_service():
    try:
        credentials = pika.PlainCredentials(RABBITMQ_USERNAME, RABBITMQ_PASSWORD)
        connection = pika.BlockingConnection(
            pika.ConnectionParameters(host=RABBITMQ_HOST, port=RABBITMQ_PORT, credentials=credentials)
        )
        channel = connection.channel()

        # Declare queues
        channel.queue_declare(queue=OCR_QUEUE, durable=True)
        channel.queue_declare(queue=OCR_RESPONSE_QUEUE, durable=True)

        logging.info(f"Connected to RabbitMQ at {RABBITMQ_HOST}:{RABBITMQ_PORT}")
        logging.info(f"Listening for messages on queue: {OCR_QUEUE}")

        # Start consuming messages
        channel.basic_consume(queue=OCR_QUEUE, on_message_callback=on_message, auto_ack=False)
        channel.start_consuming()
    except KeyboardInterrupt:
        logging.info("Shutting down OCR service...")
    except Exception as e:
        logging.error(f"Error starting OCR service: {e}")

if __name__ == "__main__":
    start_ocr_service()
