package br.com.fiap.controller;

import br.com.fiap.dao.ReceitasDAO;
import br.com.fiap.exception.EntidadeNaoEncontrada;
import br.com.fiap.model.Receitas;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/receitas")
public class ReceitasServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String valorStr = req.getParameter("valor");

        if (valorStr != null) {
            try {
                Receitas receita = new Receitas();
                receita.setValor(Double.parseDouble(valorStr));
                receita.setData_Recebimento(Date.valueOf(req.getParameter("data_Recebimento")));
                receita.setDescricao(req.getParameter("descricao"));
                receita.setCategoria_Receita(req.getParameter("categoria_Receita"));
                receita.setFormaPagamento(req.getParameter("forma_Pagamento"));
                receita.setStatus(req.getParameter("status"));
                receita.setConta_id_conta(Integer.parseInt(req.getParameter("conta_id_conta")));
                receita.setUsuario_id_usuario(Integer.parseInt(req.getParameter("usuario_id_usuario")));

                new ReceitasDAO().insert(receita);

                req.setAttribute("mensagemSucesso", "Receita cadastrada com sucesso!");
            } catch (Exception e) {
                req.setAttribute("mensagemErro", "Erro ao cadastrar receita: " + e.getMessage());
            }
        }

        doGet(req, resp); // redireciona para carregar lista novamente
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ReceitasDAO dao = null;
        dao = new ReceitasDAO();

        // Busca por ID
        String buscarId = req.getParameter("buscarId");
        if (buscarId != null) {
            try {
                int id = Integer.parseInt(buscarId);
                Receitas receitaBuscada = dao.pesquisar(id);
                req.setAttribute("receitaBuscada", receitaBuscada);
            } catch (EntidadeNaoEncontrada e) {
                req.setAttribute("mensagemAviso", "Receita não encontrada.");
            } catch (Exception e) {
                req.setAttribute("mensagemErro", "Erro ao buscar receita: " + e.getMessage());
            }
        }

        // Listagem geral
        try {
            List<Receitas> receitas = dao.listar();
            req.setAttribute("receitas", receitas);
        } catch (Exception e) {
            req.setAttribute("mensagemErro", "Erro ao listar receitas: " + e.getMessage());
        }


        req.getRequestDispatcher("/Receitas.jsp").forward(req, resp);
    }
}
