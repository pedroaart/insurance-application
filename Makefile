.PHONY: db-reset db build test api all stop

db-cache:
	docker-compose up -d postgres redis
	sleep 2

db-sample:
	docker-compose exec -T postgres psql -U insurance_user -d insurance_db -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
	docker-compose exec -T postgres psql -U insurance_user -d insurance_db < init-scripts/01-schema.sql
	docker-compose exec -T postgres psql -U insurance_user -d insurance_db < init-scripts/02-seed-data.sql

db-reset:
	docker-compose exec -T postgres psql -U insurance_user -d insurance_db -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
	docker-compose exec -T postgres psql -U insurance_user -d insurance_db < init-scripts/01-schema.sql

build:
	docker run --rm -v "$(CURDIR)/customer-service":/app -w /app maven:3.9-eclipse-temurin-17 mvn clean package -DskipTests
	docker run --rm -v "$(CURDIR)/insurance-service":/app -w /app maven:3.9-eclipse-temurin-17 mvn clean package -DskipTests

test:
	docker run --rm -v "$(CURDIR)/customer-service":/app -v maven-cache:/root/.m2 -w /app maven:3.9-eclipse-temurin-17 mvn test
	docker run --rm -v "$(CURDIR)/insurance-service":/app -v maven-cache:/root/.m2 -w /app maven:3.9-eclipse-temurin-17 mvn test

api:
	docker-compose up -d customer-service insurance-service

stop:
	docker-compose down

setup: db-cache db-reset build api