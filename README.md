São dois serviços independentes que conversam entre si: um cuida dos clientes e outro cuida das apólices de seguro.

O sistema usa Arquitetura Hexagonal. Cada serviço tem três camadas principais.

Customer Service:  serviço gerencia o cadastro de clientes. Implementa CRUD completo: criar, buscar, atualizar e deletar clientes. 

Insurance Service: gerencia simulações e contratações de apólices. Existem três tipos: Bronze (R$ 150/mês, cobertura de R$ 50 mil), Silver (R$ 300/mês, cobertura de R$ 100 mil) e Gold (R$ 500/mês, cobertura de R$ 200 mil).

Subir tudo do zero: make full-deploy

Pode usar make help para listar os comandos.
