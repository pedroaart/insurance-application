#!/bin/bash

echo "🧪 Teste completo de cenários"
echo ""

# 1. CPF inválido (400)
echo "1️⃣  CPF inválido (esperado: 400)"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"cpf":"11111111111","name":"Test","birthDate":"1990-01-01","phone":"11999999999","address":{"street":"R","number":"1","neighborhood":"C","city":"SP","state":"SP","zipCode":"01234567"}}')
echo "Resultado: $HTTP_CODE"
echo ""

# 2. Menor de idade (400)
echo "2️⃣  Menor de idade (esperado: 400)"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"cpf":"05713675343","name":"Criança","birthDate":"2010-01-01","phone":"11999999999","address":{"street":"R","number":"1","neighborhood":"C","city":"SP","state":"SP","zipCode":"01234567"}}')
echo "Resultado: $HTTP_CODE"
echo ""

# 3. Cliente válido (201)
echo "3️⃣  Cliente válido (esperado: 201)"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"cpf":"05713675343","name":"João Silva","birthDate":"1990-05-15","phone":"11987654321","address":{"street":"Rua Teste","number":"123","neighborhood":"Centro","city":"São Paulo","state":"SP","zipCode":"01234567"}}')
echo "Resultado: $HTTP_CODE"
echo ""

# 4. CPF duplicado (409)
echo "4️⃣  CPF duplicado (esperado: 409)"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"cpf":"05713675343","name":"Outro Nome","birthDate":"1990-05-15","phone":"11987654321","address":{"street":"Rua Teste","number":"123","neighborhood":"Centro","city":"São Paulo","state":"SP","zipCode":"01234567"}}')
echo "Resultado: $HTTP_CODE"
echo ""

# 5. Cliente não encontrado (404)
echo "5️⃣  Cliente não encontrado (esperado: 404)"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/v1/customers/00000000-0000-0000-0000-000000000000)
echo "Resultado: $HTTP_CODE"
echo ""

# 6. Buscar cliente existente (200)
echo "6️⃣  Buscar cliente existente (esperado: 200)"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/api/v1/customers/cpf/05713675343")
echo "Resultado: $HTTP_CODE"
echo ""

echo "✅ Testes completos!"
