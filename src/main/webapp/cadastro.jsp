<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="header.jsp" %>
<html>
<head>
    <title>Cadastro</title>
    <!-- Bootstrap CSS já deve estar no header.jsp -->
</head>
<body>

<div class="container d-flex justify-content-center align-items-center" style="min-height: 80vh;">
    <div class="card p-4 shadow" style="width: 400px;">
        <h2 class="text-center mb-4">Cadastro de Conta</h2>

        <%
            String mensagem = (String) session.getAttribute("mensagem");
            Boolean sucesso = (Boolean) session.getAttribute("sucesso");
            session.removeAttribute("mensagem");
            session.removeAttribute("sucesso");
        %>

        <% if (mensagem != null) { %>
        <div class="alert <%= (sucesso != null && sucesso) ? "alert-success" : "alert-danger" %>">
            <strong><%= mensagem %></strong>
        </div>
        <% } %>

        <% if (sucesso == null || !sucesso) { %>
        <form action="cadastro" method="post">
            <div class="mb-3">
                <label for="nome" class="form-label">Nome:</label>
                <input type="text" class="form-control" id="nome" name="nome" required>
            </div>

            <div class="mb-3">
                <label for="sobrenome" class="form-label">Sobrenome:</label>
                <input type="text" class="form-control" id="sobrenome" name="sobrenome" required>
            </div>

            <div class="mb-3">
                <label for="dataNascimento" class="form-label">Data de Nascimento:</label>
                <input type="date" class="form-control" id="dataNascimento" name="dataNascimento" required>
            </div>

            <div class="mb-3">
                <label for="email" class="form-label">Email:</label>
                <input type="email" class="form-control" id="email" name="email" required>
            </div>

            <div class="mb-3">
                <label for="senha" class="form-label">Senha:</label>
                <input type="password" class="form-control" id="senha" name="senha" required>
            </div>

            <div class="mb-3">
                <label for="genero" class="form-label">Gênero:</label>
                <select class="form-select" id="genero" name="genero" required>
                    <option value="">Selecione</option>
                    <option value="Masculino">Masculino</option>
                    <option value="Feminino">Feminino</option>
                    <option value="Outro">Outro</option>
                </select>
            </div>

            <div class="d-grid">
                <button type="submit" class="btn btn-primary">Cadastrar</button>
            </div>
        </form>
        <% } %>
    </div>
</div>

<%@include file="footer.jsp" %>
</body>
</html>
