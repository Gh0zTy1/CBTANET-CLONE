# Stage 1: Build Angular Application
FROM node:20-alpine AS build

WORKDIR /app

# Copy package metadata and install dependencies
COPY package*.json ./
RUN npm ci

# Copy application source code
COPY . .

# Build application for production
RUN npm run build -- --configuration production

# Stage 2: Serve application with NGINX
FROM nginx:alpine

# Copy custom NGINX configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy build artifacts from build stage to NGINX web root
COPY --from=build /app/dist/cbta97net-front/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
