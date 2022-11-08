FROM alpine:latest

RUN apk --no-cache update; apk upgrade --no-cache --ignore alpine-baseline
RUN apk add --no-cache openssl curl libstdc++
