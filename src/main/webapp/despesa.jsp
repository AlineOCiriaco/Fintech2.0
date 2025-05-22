<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri ="jakarta.tags.core"%>
<%@ page import="br.com.fiap.model.Despesa"%>
<%@ page import="br.com.fiap.dao.DespesaDao"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.List"%>
<%@ page import="br.com.fiap.exception.EntidadeNaoEncontrada"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Fintech - Controle de Despesas</title>
    <link rel="stylesheet" href="resources/css/bootstrap.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter&display=swap" rel="stylesheet">

    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f4f6f9;
        }

        .fintech-card {
            border-radius: 16px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            background: white;
            transition: transform 0.2s ease;
        }

        .fintech-card:hover {
            transform: translateY(-4px);
        }

        .btn-primary {
            background-color: #1E88E5;
            border: none;
        }

        .btn-secondary {
            background-color: #43A047;
            border: none;
        }

        .btn-warning {
            background-color: #FFB300;
            border: none;
        }

        .btn-danger {
            background-color: #E53935;
            border: none;
        }

        h2 {
            font-weight: bold;
            color: #333;
        }
    </style>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

</head>
<body>

<%@include file="header.jsp"%>

<div class="container mt-5">

    <h2 class="mb-3 text-center">Bem-vindo ao sistema de gerenciamento de despesas!</h2>

    <!-- resumo -->
    <div class="row mb-4">
        <div class="col-md-4">
            <div class="p-3 fintech-card text-center">
                <h6>Total de Despesas</h6>
                <h3 class="text-danger">R$ <c:out value="${totalDespesas}" /></h3>
            </div>
        </div>
    </div>

    <!-- formulário para adicionar despesa -->
    <div class="fintech-card p-4 mb-4">
        <form action="despesa?action=inserir" method="post" class="row g-3">
            <div class="col-md-4">
                <label>Nova Despesa</label>
                <input type="text" name="nome" class="form-control" placeholder="Descrição" required>
            </div>
            <div class="col-md-3">
                <label>Valor da Despesa</label>
                <input type="number" step="0.01" name="valor" class="form-control" placeholder="Valor" required>
            </div>

            <div class="col-md-3">
                <label>Data do Pagamento</label>
                <input type="date" name="data" class="form-control" placeholder="Data do Pagamento" required>
            </div>
            <div class="col-md-2">
                <label>Data do Vencimento</label>
                <input type="date" name="vencimento" class="form-control" placeholder="Data do Vencimento">
            </div>

            <div class="col-md-4">
                <label>Categoria</label>
                <select name="categoria" class="form-select" required>
                    <option value="">Selecione a Categoria</option>
                    <option value="Alimentação">Alimentação</option>
                    <option value="Transporte">Transporte</option>
                    <option value="Lazer">Lazer</option>
                    <option value="Educação">Educação</option>
                    <option value="Investimento">Investimento</option>
                    <option value="Saúde">Saúde</option>
                    <option value="Moradia">Moradia</option>
                    <option value="Outros">Outros</option>
                </select>
            </div>

            <div class="col-md-4">
                <label>Forma de Pagamento</label>
                <select name="formaPagamento" class="form-select" required>
                    <option value="">Forma de Pagamento</option>
                    <option value="Cartão">Cartão</option>
                    <option value="Dinheiro">Dinheiro</option>
                    <option value="PIX">PIX</option>
                </select>
            </div>

            <div class="col-md-2">
                <label>Status</label>
                <select name="status" class="form-select" required>
                    <option value="Pendente">Pendente</option>
                    <option value="Pago">Pago</option>
                </select>
            </div>

            <div class="col-md-2">
                <label>Recorrente?</label>
                <select name="recorrente" class="form-select" required>
                    <option value="">Recorrente?</option>
                    <option value="S">SIM</option>
                    <option value="N">NAO</option>
                </select>
            </div>

            <div class="col-12">
                <button type="submit" class="btn btn-primary">Cadastrar</button>
            </div>
        </form>
    </div>

    <!-- formulário para pesquisar despesa -->
    <div class="fintech-card p-4 mb-4">
        <form action="despesa?action=pesquisar" method="post" class="row g-3">
            <div class="col-md-6">
                <input type="text" name="termo" class="form-control" placeholder="Pesquisar" required>
            </div>
            <div class="col-md-2">
                <button type="submit" class="btn btn-secondary">Pesquisar</button>
            </div>
        </form>
    </div>

    <!-- exibir tabela com despesas -->
    <div class="fintech-card p-4">
        <h5>Despesas Cadastradas</h5>
        <table class="table table-hover">
            <thead class="table-light">
            <tr>
                <th>Descrição</th>
                <th>Valor</th>
                <th>Data de Pagamento</th>
                <th>Vencimento</th>
                <th>Categoria</th>
                <th>Forma</th>
                <th>Status</th>
                <th>Recorrente</th>
                <th>Ações</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="despesa" items="${listaDespesas}">
                <c:choose>
                    <c:when test="${despesa.idDespesa == param.idEditar}">
                        <!-- Linha em modo edição -->
                        <tr>
                            <form action="despesa?action=atualizar" method="post">
                                <input type="hidden" name="idDespesa" value="${despesa.idDespesa}" />
                                <td><input type="text" name="nome" value="${despesa.descricao}" class="form-control" required></td>
                                <td><input type="number" step="0.01" name="valor" value="${despesa.valor}" class="form-control" required></td>
                                <td><input type="date" name="data" value="${despesa.dataPagamentoFormatada}" class="form-control" required></td>
                                <td><input type="date" name="vencimento" value="${despesa.vencimentoFormatado}" class="form-control"></td>
                                <td>
                                    <select name="categoria" class="form-select" required>
                                        <option value="Alimentação" ${despesa.categoria == 'Alimentação' ? 'selected' : ''}>Alimentação</option>
                                        <option value="Transporte" ${despesa.categoria == 'Transporte' ? 'selected' : ''}>Transporte</option>
                                        <option value="Lazer" ${despesa.categoria == 'Lazer' ? 'selected' : ''}>Lazer</option>
                                        <option value="Educação" ${despesa.categoria == 'Educação' ? 'selected' : ''}>Educação</option>
                                        <option value="Investimento" ${despesa.categoria == 'Investimento' ? 'selected' : ''}>Investimento</option>
                                        <option value="Saúde" ${despesa.categoria == 'Saúde' ? 'selected' : ''}>Saúde</option>
                                        <option value="Moradia" ${despesa.categoria == 'Moradia' ? 'selected' : ''}>Moradia</option>
                                        <option value="Outros" ${despesa.categoria == 'Outros' ? 'selected' : ''}>Outros</option>
                                    </select>
                                </td>
                                <td>
                                    <select name="formaPagamento" class="form-select" required>
                                        <option value="Cartão" ${despesa.formaPagamento == 'Cartão' ? 'selected' : ''}>Cartão</option>
                                        <option value="Dinheiro" ${despesa.formaPagamento == 'Dinheiro' ? 'selected' : ''}>Dinheiro</option>
                                        <option value="PIX" ${despesa.formaPagamento == 'PIX' ? 'selected' : ''}>PIX</option>
                                    </select>
                                </td>
                                <td>
                                    <select name="status" class="form-select" required>
                                        <option value="Pendente" ${despesa.status == 'Pendente' ? 'selected' : ''}>Pendente</option>
                                        <option value="Pago" ${despesa.status == 'Pago' ? 'selected' : ''}>Pago</option>
                                    </select>
                                </td>
                                <td>
                                    <select name="recorrente" class="form-select" required>
                                        <option value="S" ${despesa.recorrente == 'S' ? 'selected' : ''}>SIM</option>
                                        <option value="N" ${despesa.recorrente == 'N' ? 'selected' : ''}>NÃO</option>
                                    </select>
                                </td>
                                <td>
                                    <button type="submit" class="btn btn-primary btn-sm">Salvar</button>
                                    <a href="despesa?action=listar" class="btn btn-secondary btn-sm">Cancelar</a>
                                </td>
                            </form>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <!-- Linha normal, somente leitura -->
                        <tr>
                            <td>${despesa.descricao}</td>
                            <td>R$ ${despesa.valor}</td>
                            <td>${despesa.dataPagamentoFormatada}</td>
                            <td>${despesa.vencimentoFormatado}</td>
                            <td>${despesa.categoria}</td>
                            <td>${despesa.formaPagamento}</td>
                            <td>${despesa.status}</td>
                            <td>${despesa.recorrenteFormatado}</td>
                            <td>
                                <div class="btn-group" role="group">
                                    <a href="despesa?action=listar&idEditar=${despesa.idDespesa}" class="btn btn-warning btn-sm">
                                        <i class="bi bi-pencil"></i> Editar
                                    </a>
                                    <a href="despesa?action=remover&id=${despesa.idDespesa}" class="btn btn-danger btn-sm"
                                       onclick="return confirm('Tem certeza que deseja remover esta despesa?');">
                                        <i class="bi bi-trash"></i> Remover
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            </tbody>

        </table>
    </div>

</div>
<%@include file="footer.jsp"%>
</body>
</html>