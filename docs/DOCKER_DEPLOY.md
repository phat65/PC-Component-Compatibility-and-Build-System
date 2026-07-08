# Docker Deploy

This setup runs the Spring Boot app and MySQL together. MySQL creates the database, and Flyway applies all migrations on app startup.

## Local Deploy

1. Create an environment file:

```powershell
Copy-Item .env.deploy.example .env.deploy
```

2. Edit `.env.deploy` and set real values for:

- `APP_BASE_URL`
- `DB_PASSWORD`
- `MYSQL_ROOT_PASSWORD`
- mail settings, if email verification is used
- PayOS settings, if online payment is used

3. Build and start:

```powershell
docker compose --env-file .env.deploy -f compose.deploy.yaml up -d --build
```

4. View logs:

```powershell
docker compose --env-file .env.deploy -f compose.deploy.yaml logs -f app
```

5. Stop:

```powershell
docker compose --env-file .env.deploy -f compose.deploy.yaml down
```

## Volumes

- `mysql_data`: database files.
- `uploads_data`: uploaded product images under `/workspace/uploads`.

The Docker image includes the repository's current `uploads/images` seed files. On the first run, Docker copies those files into the new `uploads_data` named volume. If the volume already exists, Docker preserves the existing uploaded files and does not overwrite them.

Use `docker compose --env-file .env.deploy -f compose.deploy.yaml down -v` only when intentionally deleting database and uploaded image data.

## Schema Rule

Schema changes must be added as new Flyway migrations in `src/main/resources/db/migration`. Do not depend on Hibernate `ddl-auto`; it is intentionally set to `none`.

This branch uses a squashed clean migration baseline. If a Docker `mysql_data` volume was created from the old migration chain, remove it before deploying this version:

```powershell
docker compose --env-file .env.deploy -f compose.deploy.yaml down -v
```

For product images used by seed data or staff uploads, see `docs/PRODUCT_IMAGE_GUIDE.md`.
