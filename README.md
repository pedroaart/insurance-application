São dois serviços independentes que conversam entre si: um cuida dos clientes e outro cuida das apólices de seguro.

O sistema usa Arquitetura Hexagonal. Cada serviço tem três camadas principais.

Customer Service:  serviço gerencia o cadastro de clientes. Implementa CRUD completo: criar, buscar, atualizar e deletar clientes. 

Insurance Service: gerencia simulações e contratações de apólices. Existem três tipos: Bronze, Silver e Gold.

Subir tudo do zero: 
```
make clean
make full-deploy
```

Pode usar `make help` para listar os comandos.
