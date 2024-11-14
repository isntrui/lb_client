FROM nginx:alpine
COPY composeApp/build/dist/wasmJs /usr/share/nginx/html/
EXPOSE 80