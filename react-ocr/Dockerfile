# Use an official Node.js image as a parent image
FROM node:14-alpine

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the current directory contents into the container at /app
COPY . .

# Build the React app for production
RUN npm run build

# Install serve to serve the build files
RUN npm install -g serve

# Expose port 80
EXPOSE 80

# Serve the app on port 80
CMD ["serve", "-s", "build", "-l", "80"]
