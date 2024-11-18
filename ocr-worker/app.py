import pika
import json
import os
import logging
from ocr_processor import perform_ocr

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# RabbitMQ configuration
RABBITMQ_HOST = os.getenv('RABBITMQ_HOST', 'rabbitmq')
OCR_QUEUE = 'ocr_queue'
OCR_RESPONSE_QUEUE = 'ocr_response_queue'

def on_message(channel, method, properties, body):
    try:
        logging.info(f"Received body: {body}")
        # Parse the incoming message
        message = json.loads(body)
        document_id = message.get("id")
        file_path = message.get("path")

        if not document_id or not file_path:
            raise ValueError("Message must contain 'id' and 'path'")

        logging.info(f"Received message for document ID: {document_id}, file path: {file_path}")

        # Perform OCR
        try:
            ocr_result = perform_ocr(file_path)
        except FileNotFoundError:
            logging.error(f"File not found: {file_path}")
            ocr_result = f"Error: File not found: {file_path}"
        except Exception as e:
            logging.error(f"OCR processing failed for document ID {document_id}: {e}")
            ocr_result = f"Error: OCR processing failed: {e}"

        # Send the OCR result back to the response queue
        response = {"id": document_id, "ocrResult": ocr_result}
        try:
            channel.basic_publish(
                exchange='',
                routing_key=OCR_RESPONSE_QUEUE,
                body=json.dumps(response)
            )
            logging.info(f"Sent OCR result for document ID: {document_id}")
        except Exception as e:
            logging.error(f"Failed to publish OCR result for document ID {document_id}: {e}")

        # Acknowledge the message
        channel.basic_ack(delivery_tag=method.delivery_tag)
    except Exception as e:
        logging.error(f"Error processing message: {e}")
        # Negative acknowledgment for requeueing (optional)
        channel.basic_nack(delivery_tag=method.delivery_tag, requeue=False)

def start_ocr_service():
    try:
        logging.info(f"Connecting to RabbitMQ at {RABBITMQ_HOST}...")
        connection = pika.BlockingConnection(pika.ConnectionParameters(host=RABBITMQ_HOST))
        channel = connection.channel()

        # Declare queues
        channel.queue_declare(queue=OCR_QUEUE, durable=True)
        channel.queue_declare(queue=OCR_RESPONSE_QUEUE, durable=True)

        # Confirm successful queue connection
        logging.info(f"Successfully connected to RabbitMQ. Listening on queue: {OCR_QUEUE}")

        # Start consuming
        channel.basic_consume(
            queue=OCR_QUEUE,
            on_message_callback=on_message,
            auto_ack=False  # Explicit acknowledgment
        )
        channel.start_consuming()
    except KeyboardInterrupt:
        logging.info("Shutting down OCR service...")
    except Exception as e:
        logging.error(f"Error starting OCR service: {e}")
    finally:
        try:
            channel.close()
            connection.close()
        except Exception:
            pass

if __name__ == "__main__":
    start_ocr_service()
