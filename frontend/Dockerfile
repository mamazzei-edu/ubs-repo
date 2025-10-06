# syntax=docker/dockerfile:1

# Comments are provided throughout this file to help you get started.
# If you need more help, visit the Dockerfile reference guide at
# https://docs.docker.com/go/dockerfile-reference/

# Want to help us make this template better? Share your feedback here: https://forms.gle/ybq9Krt8jtBL3iCk7

ARG NODE_VERSION=22

################################################################################
# Use node image for base image for all stages.
FROM node:${NODE_VERSION}-alpine as base

# Set working directory for all build stages.
WORKDIR /app

################################################################################
# Create a stage for installing production dependecies.
FROM base as deps

# Download dependencies as a separate step to take advantage of Docker's caching.
# Leverage a cache mount to /root/.npm to speed up subsequent builds.
# Leverage bind mounts to package.json and package-lock.json to avoid having to copy them
# into this layer.
RUN --mount=type=bind,source=package.json,target=package.json \
    --mount=type=bind,source=package-lock.json,target=package-lock.json \
    --mount=type=cache,target=/root/.npm \
    npm ci

# ################################################################################
# # Create a stage for building the application.
# FROM deps as build

# # Download additional development dependencies before building, as some projects require
# # "devDependencies" to be installed to build. If you don't need this, remove this step.
# RUN --mount=type=bind,source=package.json,target=package.json \
#     --mount=type=bind,source=package-lock.json,target=package-lock.json \
#     --mount=type=cache,target=/root/.npm \
#     npm ci

ARG SPRING_DATASOURCE_URL
ARG SPRING_DATASOURCE_USERNAME
ARG SPRING_DATASOURCE_PASSWORD
ARG SPRING_JPA_HIBERNATE_DDL_AUTO
ARG SPRING_JPA_DATABASE_PLATFORM
ARG SPRING_DATASOURCE_DRIVER-CLASS-NAME
ARG SPRING_JPA_SHOW_SQL
ARG SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA
ARG SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL
ARG SPRING_JPA_PROPERTIES_HIBERNATE_USE_SQL_COMMENTS
ARG SPRING_JPA_GENERATE_DDL
ARG SERVER_PORT
ARG SECURITY_JWT_SECRET
ARG SECURITY_JWT_EXPIRATION
ARG SPRING_SECURITY_ENABLED


# Copy the rest of the source files into the image.
COPY . .
# Run the build script.
RUN npm run build

################################################################################
# Create a new stage to run the application with minimal runtime dependencies
# where the necessary files are copied from the build stage.
# FROM base as final

# Use production node environment by default.
# ENV NODE_ENV production

# Run the application as a non-root user.
# USER node

# Copy package.json so that package manager commands can be used.
# COPY package.json .

# Copy the production dependencies from the deps stage and also
# the built application from the build stage into the image.
# COPY --from=deps /usr/src/app/node_modules ./node_modules
# COPY --from=build /usr/src/app/dist ./dist

# ENV PATH ./node_modules/.bin:$PATH

# Expose the port that the application listens on.
EXPOSE 4200

# Run the application.
CMD npm start
