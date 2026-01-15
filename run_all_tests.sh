#!/bin/bash

echo "🧪 EXECUTANDO TODOS OS TESTES"
echo "=============================="
echo ""

# 1. Testes Unitários Customer Service
echo "1️⃣ Testes Unitários - Customer Service..."
cd customer-service
./mvnw test
cd ..
echo ""

# 2. Testes Unitários Insurance Service
echo "2️⃣ Testes Unitários - Insurance Service..."
cd insurance-service
./mvnw test
cd ..
echo ""

# 3. Subir ambiente
echo "3️⃣ Subindo ambiente completo..."
make full-deploy
echo ""

# 4. Aguardar serviços ficarem prontos
echo "4️⃣ Aguardando serviços ficarem prontos..."
sleep 30
echo ""

# 5. Teste de Jornada Completa
echo "5️⃣ Executando teste de jornada completa..."
./test_complete_journey.sh
echo ""

# 6. Teste de Resiliência
echo "6️⃣ Executando teste de resiliência..."
./test_resilience.sh
echo ""

echo "✅ TODOS OS TESTES COMPLETOS!"
