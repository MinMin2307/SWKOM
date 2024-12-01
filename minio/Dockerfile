# Use the official MinIO image as the base
FROM minio/minio:latest

# Set the working directory
WORKDIR /data

# Expose the MinIO ports (9000 for API and 9001 for console)
EXPOSE 9000
EXPOSE 9001

# Define environment variables for MinIO configuration
ENV MINIO_ACCESS_KEY=admin
ENV MINIO_SECRET_KEY=admin123

# Run the MinIO server in distributed mode for /data directory
ENTRYPOINT ["minio", "server", "/data", "--console-address", ":9001"]
