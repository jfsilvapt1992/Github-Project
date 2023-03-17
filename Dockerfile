FROM gradle:jdk17 as BUILD

ENV APP_HOME /app
WORKDIR $APP_HOME

# Copy dependencies
COPY ./ $APP_HOME

# Build
RUN gradle clean build

FROM eclipse-temurin:17-jdk-jammy as app_image

USER root

RUN apt-get update && apt-get install -y libexpat1 libtasn1-6

WORKDIR /apps

COPY --from=BUILD /app/build/libs/ /apps/
