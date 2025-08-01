# Chat TCP Multi-Usuário - Documentação Completa

## Visão Geral
Sistema de chat em linha de comando com comunicação TCP entre múltiplos clientes e servidor participativo, desenvolvido como evolução do código da Prática 1.

### Execução
1. Inicie o servidor:
   ```bash
   start src/main/java/labsockets/start.bat

## Arquitetura do Sistema

### Componentes Principais
- **Servidor (MultiUserChatServer.java)**
    - Gerencia conexões de múltiplos clientes
    - Envia mensagens para todos participantes
    - Mantém lista de usuários conectados

- **Cliente (MultiUserChatClient.java)**
    - Interface com usuário via terminal
    - Threads separadas para envio/recebimento
    - Autenticação com nome de usuário

- **ClientHandler.java**
    - Gerenciador individual para cada cliente
    - Tratamento de mensagens específicas
    - Controle de ciclo de vida da conexão

## Funcionalidades Implementadas

### Comunicação
- Broadcast de mensagens para todos os clientes
- Mensagens identificadas por remetente
- Notificações de entrada/saída de usuários
- Participação ativa do servidor no chat

### Controle
- Comando "sair" para desconexão
- Gerenciamento de threads com ExecutorService
- Sincronização de acesso à lista de clientes
- Tratamento adequado de exceções