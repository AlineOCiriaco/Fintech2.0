package br.com.fiap.servlet;

import br.com.fiap.dao.CadastroDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("cadastro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nome");
        String sobrenome = request.getParameter("sobrenome");
        String dataNascimento = request.getParameter("dataNascimento");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String genero = request.getParameter("genero");

        String mensagem;
        boolean sucesso = false;

        if (nome.isEmpty() || sobrenome.isEmpty() || dataNascimento.isEmpty() ||
                email.isEmpty() || senha.isEmpty() || genero.isEmpty()) {

            mensagem = "Todos os campos são obrigatórios!";
        } else {
            try {
                CadastroDAO dao = new CadastroDAO();
                dao.cadastrar(nome, sobrenome, dataNascimento, email, senha, genero);
                mensagem = "Cadastro realizado com sucesso!";
                sucesso = true;
            } catch (Exception e) {
                mensagem = "Erro ao cadastrar: " + e.getMessage();
            }
        }

        HttpSession session = request.getSession();
        session.setAttribute("mensagem", mensagem);
        session.setAttribute("sucesso", sucesso);

        if (sucesso) {
            response.sendRedirect("outraPagina.jsp"); // ou página que queira após sucesso
        } else {
            response.sendRedirect("cadastro.jsp");
        }
    }
}
