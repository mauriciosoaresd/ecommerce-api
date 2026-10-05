# 🛒 E-Commerce REST API

![Java Version](https://img.shields.io/badge/Java-21-orange?style=flat&logo=openjdk)
![Spring Boot Version](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat&logo=springboot)
![Database](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat&logo=postgresql)

API RESTful robusta desenvolvida para gerenciamento de e-commerce, aplicando arquitetura em camadas, autenticação stateless via JWT, controle de acesso baseado em funções (RBAC) e pipeline de integração contínua (CI/CD).

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3
* **Segurança:** Spring Security 6 & Auth0 java-jwt (RBAC / Stateless)
* **Persistência de Dados:** Spring Data JPA / Hibernate
* **Banco de Dados:** PostgreSQL (Produção) / H2 (Ambiente de Testes)
* **Testes:** JUnit 5, Mockito, MockMvc
* **Automação & CI/CD:** GitHub Actions (Service Containers com PostgreSQL 15)
* **Documentação:** Springdoc OpenAPI / Swagger UI
* **Gerenciador de Dependências:** Maven

---

## 🔒 Segurança e Perfis de Acesso (RBAC)

A API utiliza autenticação via tokens JWT com tempo de expiração configurável. Os endpoints possuem permissões granulares:

| Método | Endpoint | Perfil Necessário | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/login` | Público | Autenticação do usuário e emissão do JWT |
| `POST` | `/auth/register` | Público | Cadastro de novos clientes |
| `GET` | `/products/**` | Público | Consulta de produtos e detalhes |
| `GET` | `/categories/**` | Público | Consulta de categorias |
| `POST`, `PUT`, `DELETE` | `/products/**` | `ROLE_ADMIN` | Gestão do catálogo de produtos |
| `POST`, `PUT`, `DELETE` | `/categories/**` | `ROLE_ADMIN` | Gestão das categorias |

---

## 🧪 Suíte de Testes e Qualidade

O projeto conta com ampla cobertura de testes integrados ao pipeline do **GitHub Actions**:

* **Testes Unitários:** Isolamento da camada de serviço (`ProductService`, `CategoryService`, `TokenService`) utilizando **Mockito** e `@ExtendWith(MockitoExtension.class)`.
* **Testes de Integração:** Validação dos controllers (`ProductControllerIT`, `CategoryControllerIT`, `AuthenticationControllerIT`) utilizando `@SpringBootTest` e `MockMvc` com rollback automático por transação.

Para executar os testes localmente:

```bash
mvn clean test