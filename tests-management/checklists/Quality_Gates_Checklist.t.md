# Quality Gates Checklist

TMS ID: S1-CHECKLIST

## Reports (Relatórios de Análise Estática e Cobertura)

* [ ] C1 Reports: Gerar Relatório de Cobertura de Código (HTML)
  TMS ID: C1-REPORT-COV
* [ ] C2 Reports: Verificar Análise Estática de Código (e.g., SonarQube ou Qodana)
  TMS ID: C2-REPORT-STATIC
* [ ] C3 Reports: Verificar o Relatório de Débito Técnico
  TMS ID: C3-REPORT-DEBT

## Automated Tests (Testes Automáticos)
TMS ID: S2-AUTOMATED-TESTS

* [ ] T1 Unit Tests: Executar o suite completo de Testes Unitários JUnit 6.
  TMS ID: C4-UNIT-EXEC
* [ ] T2 Unit Tests: Validar o nível de Cobertura de Ramo (Branch Coverage) alcançado.
  TMS ID: C5-UNIT-COV
* [ ] T3 CI/CD: Garantir que o Workflow de Build e Testes no GitHub Actions passou com sucesso.
  TMS ID: C6-CI-FLOW] T3 CI/CD: Garantir que o Workflow de Build e Testes no GitHub Actions passou com sucesso.