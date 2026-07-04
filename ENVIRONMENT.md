# Environment Configuration

This project now uses Spring profiles for environment-specific configuration:

- `src/main/resources/application.yml`: shared defaults only
- `src/main/resources/application-dev.yml`: fast local development defaults
- `src/main/resources/application-prod.yml`: production-safe config that expects secrets from env vars or Docker secrets

## Local development

1. Copy `.env.dev.example` to `.env.dev`.
2. Start MySQL:

```powershell
docker compose --env-file .env.dev up -d
```

3. Run Spring Boot with the `dev` profile:

```powershell
mvn spring-boot:run
```

`application-dev.yml` automatically imports `.env.dev` and keeps local defaults for:

- `DEV_DB_URL`, `DEV_DB_USERNAME`, `DEV_DB_PASSWORD`
- `APP_BASE_URL`
- optional mail and PayOS placeholders

That keeps local startup quick and avoids accidental overrides from generic machine-level `DB_*` environment variables.

## Production

1. Copy `.env.prod.example` to `.env.prod`.
2. Provide required non-secret env vars in `.env.prod`.
3. Provide secrets either as environment variables or mounted Docker secrets.

`application-prod.yml` supports these Docker secret file names under `/run/secrets`:

- `db.password`
- `mail.username`
- `mail.password`
- `payos.client-id`
- `payos.api-key`
- `payos.checksum-key`

The included `compose.prod.example.yaml` shows one way to wire those secrets into containers.

## Self-hosted Docker deploy

For a single-machine deployment with app + MySQL managed by Docker Compose:

```powershell
Copy-Item .env.deploy.example .env.deploy
docker compose --env-file .env.deploy -f compose.deploy.yaml up -d --build
```

See `docs/DOCKER_DEPLOY.md` for the full command set and volume notes.

## Important note

`PaymentService` now builds PayOS callback URLs from `app.base-url`, so each environment must set the public base URL correctly:

- dev example: `http://localhost:8080`
- prod example: `https://your-domain.example`
