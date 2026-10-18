# Auto Escola 3ESS — API REST de Agendamento de Instruções

**Checkpoint 5 – SOA e WebServices**
**Professor:** Carlos Eduardo Machado de Oliveira

## Integrante

| Nome | RM |
|------|----|
| Rodrigo Leme | RM550266 |

---

## Tecnologias

- Java 25 (configurado em `pom.xml` via `java.version`; requisito da atividade: Java 17+)
- Spring Boot 4 (Web MVC, Data JPA, Validation, Security + JWT)
- MySQL + Flyway
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito + MockMvc

## O que já existia

- CRUD de Instrutores, Alunos, Usuários (login/JWT) e Instruções.
- Serviço de agendamento de instruções com as regras de negócio (validadores em `application/core/specification`).

## O que foi adicionado neste checkpoint

### 1. Consumo de API externa (ViaCEP)

- `adapter/out/client/ViaCepClient.java` → consome o WebService público **ViaCEP** usando o `RestClient` do Spring.
- `application/port/out/ConsultaCepPort.java` → porta de saída (a aplicação depende da interface, não do cliente HTTP).
- `adapter/in/controller/EnderecoController.java` → expõe o endpoint:

```
GET /enderecos/cep/{cep}      (ex.: /enderecos/cep/01001000)
```

- CEP inexistente → `404`; CEP com formato inválido → `400`.
- URL configurável em `application.properties` (`api.viacep.url`).

### 2. Documentação automática com Swagger

- Dependência `springdoc-openapi-starter-webmvc-ui` + `config/documentation/SwaggerConfig.java` (título, descrição e esquema de segurança `bearer-key` para JWT).
- Controllers anotados com `@Tag` e `@Operation`.
- Acesso (com a aplicação rodando):
  - Swagger UI: http://localhost:8080/swagger-ui.html
  - OpenAPI JSON: http://localhost:8080/v3/api-docs
  - OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml
- Para testar endpoints protegidos: faça `POST /login`, copie o token e clique em **Authorize**.

### 3. Configuração de CORS

- `config/cors/CorsConfig.java` → bean `CorsConfigurationSource` com origens, métodos e headers permitidos.
- `SecurityConfig` usa `.cors(Customizer.withDefaults())`, que aplica esse bean na cadeia de segurança.
- As origens permitidas ficam em `application.properties`:

```properties
api.cors.allowed-origins=https://www.enderecodomeufront.com.br,http://localhost:3000,...
```

### 4. Testes automatizados (por entidade)

| Entidade | Arquivo(s) de teste |
|----------|---------------------|
| Instrutor | `controller/InstrutorControllerTest`, `service/InstrutorServiceTest`, `domain/instrutor/InstrutorRepositoryTest` |
| Aluno | `controller/AlunoControllerTest`, `service/AlunoServiceTest` |
| Usuário | `domain/UsuarioTest` (perfil/roles e token JWT), `controller/LoginControllerTest` |
| Instrução | `controller/InstrucaoControllerTest`, `service/AgendaDeInstrucoesTest`, `service/ValidadorHorarioFuncionamentoTest` |
| ViaCEP (API externa) | `controller/EnderecoControllerTest`, `service/ViaCepClientTest` |
| CORS | `config/CorsConfigTest` |

- **Testes unitários** (`service/*`, `domain/UsuarioTest`): usam Mockito, **não precisam de banco**.
- **Testes de controller/CORS** (`@SpringBootTest` + `MockMvc`): os services são simulados com `@MockitoBean`, mas o contexto do Spring sobe completo, então o **MySQL precisa estar rodando**.
- **Teste de repositório** (`@DataJpaTest`): usa o perfil `test` (banco `autoescola3ess_test`).

## Ajustes feitos para a aplicação subir e os testes rodarem

- `Instrucao` passou a referenciar `InstrutorEntity` (a classe de domínio `Instrutor` não é `@Entity`, o que impedia o Hibernate de iniciar).
- `InstrutorRepositoryImpl` trata o caso de nenhum instrutor disponível (retorna `null` em vez de lançar `NullPointerException`).
- `AlunoController`: os métodos foram ligados a um `AlunoService` (antes retornavam `null`).
- `ValidadorLimiteDiarioAluno` passou a lançar a exceção quando o aluno já tem instrução no dia.
- `GlobalExceptionHandler` trata `AlunoNotFoundException` (404), `ValidacaoException` (400) e `CepNotFoundException` (404).

## Como executar

1. Crie os bancos no MySQL:
   ```sql
   CREATE DATABASE autoescola3ess;
   CREATE DATABASE autoescola3ess_test;
   ```
2. Ajuste usuário/senha em `src/main/resources/application.properties`, se necessário.
3. Rode a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Rode os testes:
   ```bash
   ./mvnw test
   ```
