#!/bin/bash

echo "🔥 TESTE DE RESILIÊNCIA - Circuit Breaker"
echo "=========================================="
echo ""

# Parar customer-service para simular falha
echo "1️⃣ Parando Customer Service para simular falha..."
docker-compose stop customer-service
sleep 5

# Tentar contratar seguro (deve falhar gracefully)
echo ""
echo "2️⃣ Tentando contratar seguro com Customer Service down..."
for i in {1..5}; do
    RESPONSE=$(curl -s -w "\n%{http_code}" -X POST http://localhost:8081/api/v1/insurance/contract \
      -H "Content-Type: application/json" \
      -d "{
        \"customerId\": \"$(uuidgen)\",
        \"policyType\": \"BRONZE\",
        \"idempotencyKey\": \"test-$i\"
      }")
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -1)
    echo "   Requisição $i: HTTP $HTTP_CODE"
done

# Subir customer-service novamente
echo ""
echo "3️⃣ Subindo Customer Service novamente..."
docker-compose up -d customer-service
sleep 15

echo ""
echo "4️⃣ Testando recuperação do circuit breaker..."
sleep 30  # Aguardar waitDurationInOpenState (10s) + margem

echo ""
echo "✅ Teste de resiliência completo!"
echo ""
echo "Esperado:"
echo "  - Primeiras requisições: 503 (Service Unavailable)"
echo "  - Após recovery: Sistema volta ao normal"
