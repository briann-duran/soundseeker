FROM node:20-alpine as build
ARG VITE_APP_BACK_END_URL
ARG VITE_APP_IMAGE_URL
ARG VITE_APP_SERVER_URL
ENV VITE_APP_BACK_END_URL=$VITE_APP_BACK_END_URL
ENV VITE_APP_IMAGE_URL=$VITE_APP_IMAGE_URL
ENV VITE_APP_SERVER_URL=$VITE_APP_SERVER_URL
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
COPY default.conf /etc/nginx/conf.d/
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]