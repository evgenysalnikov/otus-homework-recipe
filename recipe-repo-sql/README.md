## How to connect to PostgreSQL

```shell
docker run \
  --name recipe-postgres \
  -e POSTGRES_PASSWORD=recipe-pass \
  -e POSTGRES_USER=recipe \
  -e POSTGRES_DB=recipedb \
  -p 5432:5432 \
  -d postgres
```
