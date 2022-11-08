FROM alpine:3.12

RUN apk --no-cache update; apk upgrade --no-cache --ignore alpine-baseline
RUN apk add --no-cache openssl curl libstdc++
