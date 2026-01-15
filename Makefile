.PHONY: help start stop restart clean build rebuild logs test deploy full-deploy health swagger

# Default target
.DEFAULT_GOAL := help

# Colors for terminal output
GREEN  := \033[0;32m
YELLOW := \033[0;33m
RED    := \033[0;31m
BLUE   := \033[0;34m
NC     := \033[0m # No Color

help: ## Show this help message
	@echo "$(BLUE)Insurance Platform - Available Commands$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(GREEN)%-15s$(NC) %s\n", $$1, $$2}'
	@echo ""

start: ## Start all services (infra + apps)
	@echo "$(BLUE)Starting all services...$(NC)"
	docker-compose up -d
	@echo "$(GREEN)✓ Services started$(NC)"
	@echo ""
	@echo "Waiting for services to be ready..."
	@sleep 30
	@make health

stop: ## Stop all services
	@echo "$(YELLOW)Stopping all services...$(NC)"
	docker-compose stop
	@echo "$(GREEN)✓ Services stopped$(NC)"

restart: ## Restart all services
	@echo "$(YELLOW)Restarting all services...$(NC)"
	@make stop
	@make start

clean: ## Stop services and remove volumes
	@echo "$(RED)Cleaning up containers and volumes...$(NC)"
	docker-compose down -v
	@echo "$(GREEN)✓ Cleanup complete$(NC)"

build: ## Build docker images
	@echo "$(BLUE)Building Docker images...$(NC)"
	docker-compose build
	@echo "$(GREEN)✓ Build complete$(NC)"

rebuild: ## Clean rebuild (no cache)
	@echo "$(BLUE)Rebuilding from scratch (no cache)...$(NC)"
	docker-compose build --no-cache
	@echo "$(GREEN)✓ Rebuild complete$(NC)"

logs: ## Show logs from all services
	docker-compose logs -f

logs-customer: ## Show customer-service logs
	docker-compose logs -f customer-service

logs-insurance: ## Show insurance-service logs
	docker-compose logs -f insurance-service

logs-postgres: ## Show postgres logs
	docker-compose logs -f postgres

test: ## Run unit tests for all services
	@echo "$(BLUE)Running unit tests...$(NC)"
	@echo ""
	@echo "$(YELLOW)Testing Customer Service...$(NC)"
	cd customer-service && ./mvnw test
	@echo ""
	@echo "$(YELLOW)Testing Insurance Service...$(NC)"
	cd insurance-service && ./mvnw test
	@echo ""
	@echo "$(GREEN)✓ All tests passed$(NC)"

deploy: ## Deploy without rebuild (assumes images exist)
	@echo "$(BLUE)Deploying services...$(NC)"
	@make stop
	@make start

full-deploy: ## Full deployment: clean, rebuild, and start
	@echo "$(BLUE)Full deployment starting...$(NC)"
	@make clean
	@make rebuild
	@make start
	@echo ""
	@echo "$(GREEN)✓ Full deployment complete!$(NC)"
	@echo ""
	@make swagger

health: ## Check health of all services
	@echo "$(BLUE)Checking service health...$(NC)"
	@echo ""
	@echo -n "PostgreSQL:        "
	@curl -s http://localhost:8080/actuator/health > /dev/null 2>&1 && echo "$(GREEN)✓$(NC)" || echo "$(RED)✗$(NC)"
	@echo -n "Redis:             "
	@docker-compose exec -T redis redis-cli --raw incr ping > /dev/null 2>&1 && echo "$(GREEN)✓$(NC)" || echo "$(RED)✗$(NC)"
	@echo -n "Customer Service:  "
	@curl -s http://localhost:8080/actuator/health | grep -q UP && echo "$(GREEN)✓ http://localhost:8080$(NC)" || echo "$(RED)✗$(NC)"
	@echo -n "Insurance Service: "
	@curl -s http://localhost:8081/actuator/health | grep -q UP && echo "$(GREEN)✓ http://localhost:8081$(NC)" || echo "$(RED)✗$(NC)"
	@echo ""

swagger: ## Show Swagger UI URLs
	@echo "$(BLUE)Swagger Documentation:$(NC)"
	@echo ""
	@echo "  Customer Service:  $(GREEN)http://localhost:8080/swagger-ui.html$(NC)"
	@echo "  Insurance Service: $(GREEN)http://localhost:8081/swagger-ui.html$(NC)"
	@echo ""

db-shell: ## Connect to PostgreSQL shell
	docker-compose exec postgres psql -U insurance_user -d insurance_db

db-tables: ## Show database tables
	@echo "$(BLUE)Database Tables:$(NC)"
	@docker-compose exec postgres psql -U insurance_user -d insurance_db -c "\dt"

db-migrations: ## Show Flyway migration history
	@echo "$(BLUE)Flyway Migration History:$(NC)"
	@docker-compose exec postgres psql -U insurance_user -d insurance_db -c "SELECT * FROM flyway_schema_history ORDER BY installed_rank;"

test-journey: ## Run complete customer journey test
	@echo "$(BLUE)Running complete customer journey test...$(NC)"
	@chmod +x test_complete_journey.sh
	./test_complete_journey.sh

test-resilience: ## Run resilience tests (Circuit Breaker)
	@echo "$(BLUE)Running resilience tests...$(NC)"
	@chmod +x test_resilience.sh
	./test_resilience.sh

ps: ## Show running containers
	docker-compose ps

stats: ## Show container resource usage
	docker stats --no-stream
