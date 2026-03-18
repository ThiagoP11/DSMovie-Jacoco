# 🧪 DSCommerce - Testes Unitários e Cobertura com JaCoCo

## 📌 Sobre o projeto

Projeto focado em testes unitários na camada de serviço e análise de cobertura de código utilizando JaCoCo.

---

## 📏 Regras de negócio

- Operações de produto devem:
  - Buscar por id
  - Buscar por nome com paginação
  - Atualizar produto existente
  - Deletar produto existente
- Regras de exceção:
  - Produto inexistente → `ResourceNotFoundException`
  - Produto com dependência → `DatabaseException`
- Usuário:
  - Buscar usuário por username
  - Obter usuário logado

---

## 🛠️ Tecnologias utilizadas

- Java
- Spring Boot
- JUnit 5
- Mockito
- JaCoCo
- Maven

---

## 🔐 Autenticação

- Utiliza autenticação baseada em usuário (UserDetails)
- Métodos testados:
  - `loadUserByUsername`
  - `getMe`

---

## 🧪 Estrutura de testes

### ProductService

- findById
  - Retorna produto quando id existe
  - Lança `ResourceNotFoundException` quando não existe

- findAll (searchByName)
  - Retorna página de produtos

- update
  - Atualiza produto existente
  - Lança `ResourceNotFoundException` para id inexistente

- delete
  - Deleta produto existente
  - Lança `ResourceNotFoundException` para id inexistente
  - Lança `DatabaseException` para id dependente

---

### UserService

- loadUserByUsername
  - Retorna usuário quando existe
  - Lança exceção quando não existe

- getMe
  - Retorna usuário logado
  - Trata cenários de usuário não autenticado

---

## 📊 Cobertura de código

Tipos de cobertura analisados:

- **Line Coverage (Statement)**
- **Branch Coverage**
- **Function Coverage**

Ferramenta utilizada:

- **JaCoCo**

---

## ▶️ Como executar

```bash
# Clonar o repositório
git clone https://github.com/seu-usuario/dscommerce.git
cd dscommerce

# Rodar o projeto
mvn spring-boot:run

# Executar testes
mvn test

# Gerar relatório de cobertura
mvn jacoco:report
```
