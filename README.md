
# Gerenciador de Senhas Seguro

Este é um **Gerenciador de Senhas** com as seguintes funcionalidades:

✅ Cadastro de senhas criptografadas (AES/GCM/NoPadding)  
✅ Criptografia forte das senhas armazenadas  
✅ Autenticação de dois fatores (2FA) com **Google Authenticator** (QR Code e validação do código)  
✅ Geração de senhas seguras aleatórias  
✅ Validação de vazamento de senhas (exemplo de uso de APIs externas)  
✅ Sem dependência de banco externo (armazenamento local no código)  

---

## Tecnologias

- Java 17+
- Maven
- Apache Commons Codec (Base32)
- ZXing (para geração de QR Code)

---

## Como executar

### Pré-requisitos

- Java 17+ instalado
- Maven instalado

### Execução

```bash
mvn clean compile exec:java -Dexec.mainClass="com.securepassmanager.Main"
```

### Como funciona a autenticação 2FA

1. Ao executar o programa, um QR Code será exibido em uma janela.
2. Escaneie o QR Code com o app **Google Authenticator** ou similar.
3. O app irá gerar um código TOTP de 6 dígitos a cada 30 segundos.
4. O programa pedirá que você digite este código.
5. Se o código for válido, você terá acesso ao gerenciador de senhas.

---

## Melhorias futuras

- Modo offline completo com armazenamento local seguro
- Sincronização opcional com a nuvem
- Interface gráfica (GUI)
- Integração com APIs reais de verificação de vazamentos
- Suporte multiusuário

---

## Segurança

- Algoritmo de criptografia: `AES/GCM/NoPadding` (recomendado)  
- Algoritmo de hash: `SHA-512`  
- Autenticação 2FA com algoritmo `HmacSHA512` totalmente compatível com o Google Authenticator

---

Desenvolvido como exemplo de boas práticas em gerenciadores de senha.  
**Não use em produção sem auditoria completa de segurança!**

### Nota de versão

- A geração da chave secreta (`secretKey`) foi atualizada para garantir um valor válido em **Base32**, totalmente compatível com o Google Authenticator.

### Melhoria no fluxo de adição de senha

- Se a senha tiver sido vazada, o sistema pergunta se o usuário deseja armazená-la mesmo assim.

### Novas funcionalidades

- **Geração automática de senha forte**:
    - Ao adicionar uma nova senha, digite `gerar` para que o sistema gere uma senha forte automaticamente.
    - Você pode escolher se deseja usar a senha gerada ou não.

- **Listar senhas descriptografadas**: (Em implementação) 

    - Agora ao listar senhas, o sistema exibe o nome do serviço, o usuário e a senha em texto claro (as senhas continuam armazenadas criptografadas).

### Novas funcionalidades

- **Validação de senha aprimorada**:
    - Instruções para senha forte agora aparecem antes de digitar.
    - Senha fraca → exige nova senha.
    - Senha vazada → pergunta se deseja prosseguir.

- **Armazenamento de data de criação/atualização**: (Em implementação)
    - Cada senha agora armazena a data de criação/última atualização.
    - Ao listar as senhas, esta data é exibida.

- **Backup seguro criptografado**: (Em implementação)
    - Nova opção no menu: "Fazer backup seguro".
    - Gera um arquivo `backup_secure.enc` com todas as credenciais criptografadas.
