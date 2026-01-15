.PHONY: help start stop restart clean build rebuild logs test deploy full-deploy health swagger

.DEFAULT_GOAL := help

GREEN  := \033[0;32m
YELLOW := \033[0;33m
RED    := \033[0;31m
BLUE   := \033[0;34m
NC     := \033[0m

help:
	@echo "$(BLUE)Insurance Platform - Available Commands$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(GREEN)%-15s$(NC) %s\n", $$1, $$2}'
	@echo ""

start:
	@echo "$(BLUE)Starting all services...$(NC)"
	docker-compose up -d
	@echo "$(GREEN)✓ Services started$(NC)"
	@echo ""
	@echo "Waiting for services to be ready..."
	@sleep 30
	@make health

clean:
	@echo "$(RED)Cleaning up containers and volumes...$(NC)"
	docker-compose down -v
	@echo "$(GREEN)✓ Cleanup complete$(NC)"

build:
	@echo "$(BLUE)Building Docker images...$(NC)"
	docker-compose build
	@echo "$(GREEN)✓ Build complete$(NC)"

rebuild:
	@echo "$(BLUE)Rebuilding from scratch (no cache)...$(NC)"
	docker-compose build --no-cache
	@echo "$(GREEN)✓ Rebuild complete$(NC)"

full-deploy:
	@echo "$(BLUE)Full deployment starting...$(NC)"
	@make clean
	@make rebuild
	@make start
	@echo ""
	@echo "$(GREEN)✓ Full deployment complete!$(NC)"
	@echo ""
	@make swagger

health:
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

swagger:
	@echo "$(BLUE)Swagger Documentation:$(NC)"
	@echo ""
	@echo "  Customer Service:  $(GREEN)http://localhost:8080/swagger-ui.html$(NC)"
	@echo "  Insurance Service: $(GREEN)http://localhost:8081/swagger-ui.html$(NC)"
	@echo ""
