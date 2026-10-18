# Vantail Update Server

## Local PostgreSQL

The application uses PostgreSQL through Spring Data JPA. To start the local database:

1. Copy `.env.example` to `.env` and change the password if needed.
2. Start PostgreSQL with `docker compose up -d database`.
3. Load the same settings into the application process and start it with `set -a; . ./.env; set +a; ./gradlew bootRun`.

The default JDBC URL is `jdbc:postgresql://localhost:5432/vantail_update_server`. Set
`DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in the environment to connect to a different
database. The Compose service uses `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, and `DB_PORT`.
