{ time (docker compose down && ./gradlew clean build -x test && docker compose build && docker compose up -d --force-recreate); }
