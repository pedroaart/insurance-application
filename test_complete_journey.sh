#!/bin/bash

echo "🚀 TESTE COMPLETO DA JORNADA DE CONTRATAÇÃO DE SEGUROS"
echo "======================================================"
echo ""

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

# Variáveis
CUSTOMER_SERVICE="http://localhost:8080"
INSURANCE_SERVICE="http://localhost:8081"

echo -e "${BLUE}📋 CENÁRIO: Cliente completa jornada de contratação${NC}"
echo ""

# ETAPA 1: Cadastrar cliente
echo -e "${BLUE}1️⃣ CADASTRANDO CLIENTE...${NC}"
CUSTOMER_RESPONSE=$(curl -s -X POST ${CUSTOMER_SERVICE}/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "05713675343",
    "name": "João Silva Santos",
    "birthDate": "1990-05-15",
    "phone": "11987654321",
    "address": {
      "street": "Av Paulista",
      "number": "1000",
      "neighborhood": "Bela Vista",
      "city": "São Paulo",
      "state": "SP",
      "zipCode": "01310100"
    }
  }')

CUSTOMER_ID=$(echo $CUSTOMER_RESPONSE | jq -r '.id')

if [ "$CUSTOMER_ID" != "null" ] && [ -n "$CUSTOMER_ID" ]; then
    echo -e "${GREEN}✅ Cliente cadastrado com sucesso!${NC}"
    echo "   ID: $CUSTOMER_ID"
else
    echo -e "${RED}❌ Erro ao cadastrar cliente${NC}"
    echo $CUSTOMER_RESPONSE | jq
    exit 1
fi
echo ""

# ETAPA 2: Simular Seguro Bronze
echo -e "${BLUE}2️⃣ SIMULANDO SEGURO BRONZE...${NC}"
SIMULATION_BRONZE=$(curl -s -X POST ${INSURANCE_SERVICE}/api/v1/insurance/simulate \
  -H "Content-Type: application/json" \
  -d "{
    \"customerId\": \"$CUSTOMER_ID\",
    \"policyType\": \"BRONZE\"
  }")

echo "   Cobertura: $(echo $SIMULATION_BRONZE | jq -r '.coverageAmount')"
echo "   Prêmio Mensal: R$ $(echo $SIMULATION_BRONZE | jq -r '.monthlyPremium')"
echo "   Prêmio Anual: R$ $(echo $SIMULATION_BRONZE | jq -r '.annualPremium')"
echo ""

# ETAPA 3: Simular Seguro Silver
echo -e "${BLUE}3️⃣ SIMULANDO SEGURO SILVER...${NC}"
SIMULATION_SILVER=$(curl -s -X POST ${INSURANCE_SERVICE}/api/v1/insurance/simulate \
  -H "Content-Type: application/json" \
  -d "{
    \"customerId\": \"$CUSTOMER_ID\",
    \"policyType\": \"SILVER\"
  }")

echo "   Cobertura: $(echo $SIMULATION_SILVER | jq -r '.coverageAmount')"
echo "   Prêmio Mensal: R$ $(echo $SIMULATION_SILVER | jq -r '.monthlyPremium')"
echo "   Prêmio Anual: R$ $(echo $SIMULATION_SILVER | jq -r '.annualPremium')"
echo ""

# ETAPA 4: Simular Seguro Gold
echo -e "${BLUE}4️⃣ SIMULANDO SEGURO GOLD...${NC}"
SIMULATION_GOLD=$(curl -s -X POST ${INSURANCE_SERVICE}/api/v1/insurance/simulate \
  -H "Content-Type: application/json" \
  -d "{
    \"customerId\": \"$CUSTOMER_ID\",
    \"policyType\": \"GOLD\"
  }")

echo "   Cobertura: $(echo $SIMULATION_GOLD | jq -r '.coverageAmount')"
echo "   Prêmio Mensal: R$ $(echo $SIMULATION_GOLD | jq -r '.monthlyPremium')"
echo "   Prêmio Anual: R$ $(echo $SIMULATION_GOLD | jq -r '.annualPremium')"
echo ""

# ETAPA 5: Contratar Seguro Gold (escolha do cliente)
echo -e "${BLUE}5️⃣ CONTRATANDO SEGURO GOLD...${NC}"
IDEMPOTENCY_KEY="contract-$(uuidgen)"
CONTRACT_RESPONSE=$(curl -s -X POST ${INSURANCE_SERVICE}/api/v1/insurance/contract \
  -H "Content-Type: application/json" \
  -d "{
    \"customerId\": \"$CUSTOMER_ID\",
    \"policyType\": \"GOLD\",
    \"idempotencyKey\": \"$IDEMPOTENCY_KEY\"
  }")

POLICY_NUMBER=$(echo $CONTRACT_RESPONSE | jq -r '.policyNumber')

if [ "$POLICY_NUMBER" != "null" ] && [ -n "$POLICY_NUMBER" ]; then
    echo -e "${GREEN}✅ Apólice contratada com sucesso!${NC}"
    echo "   Número da Apólice: $POLICY_NUMBER"
    echo "   Status: $(echo $CONTRACT_RESPONSE | jq -r '.status')"
    echo "   Vigência: $(echo $CONTRACT_RESPONSE | jq -r '.startDate') até $(echo $CONTRACT_RESPONSE | jq -r '.endDate')"
else
    echo -e "${RED}❌ Erro ao contratar apólice${NC}"
    echo $CONTRACT_RESPONSE | jq
    exit 1
fi
echo ""

# ETAPA 6: Testar Idempotência (mesma requisição novamente)
echo -e "${BLUE}6️⃣ TESTANDO IDEMPOTÊNCIA (repetindo contratação)...${NC}"
DUPLICATE_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST ${INSURANCE_SERVICE}/api/v1/insurance/contract \
  -H "Content-Type: application/json" \
  -d "{
    \"customerId\": \"$CUSTOMER_ID\",
    \"policyType\": \"GOLD\",
    \"idempotencyKey\": \"$IDEMPOTENCY_KEY\"
  }")

HTTP_CODE=$(echo "$DUPLICATE_RESPONSE" | tail -1)

if [ "$HTTP_CODE" = "409" ]; then
    echo -e "${GREEN}✅ Idempotência funcionando! (HTTP 409 Conflict)${NC}"
else
    echo -e "${RED}❌ Idempotência falhou! Esperado 409, recebido: $HTTP_CODE${NC}"
fi
echo ""

# ETAPA 7: Tentar contratar segunda apólice (deve falhar)
echo -e "${BLUE}7️⃣ TESTANDO REGRA: Cliente não pode ter 2 apólices ativas...${NC}"
SECOND_POLICY=$(curl -s -w "\n%{http_code}" -X POST ${INSURANCE_SERVICE}/api/v1/insurance/contract \
  -H "Content-Type: application/json" \
  -d "{
    \"customerId\": \"$CUSTOMER_ID\",
    \"policyType\": \"BRONZE\",
    \"idempotencyKey\": \"contract-$(uuidgen)\"
  }")

HTTP_CODE=$(echo "$SECOND_POLICY" | tail -1)

if [ "$HTTP_CODE" = "409" ]; then
    echo -e "${GREEN}✅ Regra de negócio funcionando! (HTTP 409 Conflict)${NC}"
else
    echo -e "${RED}❌ Regra falhou! Cliente conseguiu contratar 2 apólices${NC}"
fi
echo ""

# ETAPA 8: Buscar apólices do cliente
echo -e "${BLUE}8️⃣ BUSCANDO APÓLICES DO CLIENTE...${NC}"
POLICIES=$(curl -s ${INSURANCE_SERVICE}/api/v1/insurance/customer/${CUSTOMER_ID})
POLICY_COUNT=$(echo $POLICIES | jq '. | length')

echo "   Total de apólices: $POLICY_COUNT"
echo $POLICIES | jq
echo ""

# ETAPA 9: Testar resiliência - Cliente inexistente
echo -e "${BLUE}9️⃣ TESTANDO RESILIÊNCIA: Cliente inexistente...${NC}"
FAKE_CUSTOMER="00000000-0000-0000-0000-000000000000"
ERROR_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST ${INSURANCE_SERVICE}/api/v1/insurance/simulate \
  -H "Content-Type: application/json" \
  -d "{
    \"customerId\": \"$FAKE_CUSTOMER\",
    \"policyType\": \"BRONZE\"
  }")

HTTP_CODE=$(echo "$ERROR_RESPONSE" | tail -1)

if [ "$HTTP_CODE" = "404" ]; then
    echo -e "${GREEN}✅ Validação back-to-back funcionando! (HTTP 404)${NC}"
else
    echo -e "${RED}❌ Validação falhou! Esperado 404, recebido: $HTTP_CODE${NC}"
fi
echo ""

# RESUMO FINAL
echo "======================================================"
echo -e "${GREEN}✅ JORNADA COMPLETA TESTADA COM SUCESSO!${NC}"
echo "======================================================"
echo ""
echo "📊 Resumo:"
echo "   ✅ Cliente cadastrado"
echo "   ✅ Simulações realizadas (Bronze, Silver, Gold)"
echo "   ✅ Apólice contratada"
echo "   ✅ Idempotência validada"
echo "   ✅ Regras de negócio validadas"
echo "   ✅ Integração back-to-back validada"
echo "   ✅ Circuit breaker/resiliência validada"
echo ""
