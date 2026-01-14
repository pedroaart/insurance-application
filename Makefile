.PHONY: help start stop dml api clean build test logs

# Default command
.DEFAULT_GOAL := help

# Colors for output
BLUE := \033[0;34m
GREEN := \033[0;32m
YELLOW := \033[0;33m
RED := \033[0;31m
NC := \033[0m # No Color

start: ## Start all containers (PostgreSQL, Redis, Services)
	@echo "$(BLUE)Starting Insurance Platform...$(NC)"
	@docker-compose up -d postgres redis
	@echo "$(YELLOW)Waiting for databases to be healthy...$(NC)"
	@sleep 10
	@echo "$(GREEN)✓ Infrastructure started successfully!$(NC)"

ddl: ## Create database schema (tables, indexes, triggers)
	@echo "$(BLUE)Creating database schema...$(NC)"
	@docker-compose exec -T postgres psql -U insurance_user -d insurance_db < init-scripts/01-schema.sql
	@echo "$(GREEN)✓ Database schema created successfully!$(NC)"

dml: ## Insert seed data (fictitious data for testing)
	@echo "$(BLUE)Inserting seed data...$(NC)"
	@docker-compose exec -T postgres psql -U insurance_user -d insurance_db < init-scripts/02-seed-data.sql
	@echo "$(GREEN)✓ Seed data inserted successfully!$(NC)"

db-reset: ## Drop all tables and recreate (DANGER: deletes all data)
	@echo "$(RED)⚠️  WARNING: This will delete all data!$(NC)"
	@echo "$(YELLOW)Press Ctrl+C to cancel, or Enter to continue...$(NC)"
	@read confirm
	@echo "$(BLUE)Dropping all tables...$(NC)"
	@docker-compose exec -T postgres psql -U insurance_user -d insurance_db -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
	@echo "$(GREEN)✓ Database reset completed!$(NC)"
	@make ddl
	@make dml


build: ## Build application JARs using Docker
	@echo "$(BLUE)Building customer-service with Docker...$(NC)"
	@docker run --rm \
		-v "$(CURDIR)/customer-service":/app \
		-w /app \
		maven:3.9-eclipse-temurin-17 \
		mvn clean package -DskipTests
	@echo "$(GREEN)✓ customer-service built!$(NC)"
	@echo ""
	@echo "$(BLUE)Building insurance-service with Docker...$(NC)"
	@docker run --rm \
		-v "$(CURDIR)/insurance-service":/app \
		-w /app \
		maven:3.9-eclipse-temurin-17 \
		mvn clean package -DskipTests
	@echo "$(GREEN)✓ insurance-service built!$(NC)"
	@echo "$(GREEN)✓ All applications built successfully!$(NC)"

generate-wrapper: ## Generate Maven wrapper using Docker
	@echo "$(BLUE)Generating Maven wrapper for customer-service...$(NC)"
	@docker run --rm \
		-v "$(CURDIR)/customer-service":/app \
		-w /app \
		maven:3.9-eclipse-temurin-17 \
		mvn wrapper:wrapper
	@echo "$(BLUE)Generating Maven wrapper for insurance-service...$(NC)"
	@docker run --rm \
		-v "$(CURDIR)/insurance-service":/app \
		-w /app \
		maven:3.9-eclipse-temurin-17 \
		mvn wrapper:wrapper
	@echo "$(GREEN)✓ Maven wrappers generated!$(NC)"

test: ## Run tests using Docker
	@echo "$(BLUE)Running tests...$(NC)"
	@docker run --rm \
		-v "$(CURDIR)/customer-service":/app \
		-v maven-cache:/root/.m2 \
		-w /app \
		maven:3.9-eclipse-temurin-17 \
		mvn test
	@docker run --rm \
		-v "$(CURDIR)/insurance-service":/app \
		-v maven-cache:/root/.m2 \
		-w /app \
		maven:3.9-eclipse-temurin-17 \
		mvn test
	@echo "$(GREEN)✓ All tests passed!$(NC)"


api: ## Start API services
	@echo "$(BLUE)Starting API services...$(NC)"
	@docker-compose up -d customer-service insurance-service
	@echo "$(YELLOW)Waiting for services to be ready...$(NC)"
	@sleep 15
	@echo "$(GREEN)✓ API services started!$(NC)"
	@echo ""
	@echo "$(BLUE)Service URLs:$(NC)"
	@echo "  Customer Service: $(GREEN)http://localhost:8080$(NC)"
	@echo "  Customer Swagger:  $(GREEN)http://localhost:8080/swagger-ui.html$(NC)"
	@echo "  Insurance Service: $(GREEN)http://localhost:8081$(NC)"
	@echo "  Insurance Swagger: $(GREEN)http://localhost:8081/swagger-ui.html$(NC)"

stop: ## Stop all containers
	@echo "$(YELLOW)Stopping all containers...$(NC)"
	@docker-compose down
	@echo "$(GREEN)✓ All containers stopped$(NC)"

clean: ## Remove all containers, volumes and build artifacts
	@echo "$(RED)Cleaning up...$(NC)"
	@docker-compose down -v
	@cd customer-service && ./mvnw clean || true
	@cd insurance-service && ./mvnw clean || true
	@echo "$(GREEN)✓ Cleanup completed$(NC)"

logs: ## Show logs from all services
	@docker-compose logs -f

logs-customer: ## Show logs from customer service
	@docker-compose logs -f customer-service

logs-insurance: ## Show logs from insurance service
	@docker-compose logs -f insurance-service