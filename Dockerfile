FROM alpine:latest

RUN apk --no-cache update; apk upgrade --no-cache 
RUN apk add --no-cache openssl curl libstdc++
