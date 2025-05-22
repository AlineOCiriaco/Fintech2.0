package br.com.fiap.controller;

import br.com.fiap.dao.DespesaDao;
import br.com.fiap.exception.EntidadeNaoEncontrada;
import br.com.fiap.model.Despesa;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet(urlPatterns = {"/despesa"})
public class DespesaServlet extends HttpServlet {

    private DespesaDao despesaDao;

    @Override
    public void init() throws ServletException {
        try {

            System.out.println("Registrando driver Oracle...");
            Class.forName("oracle.jdbc.driver.OracleDriver");

            despesaDao = new DespesaDao();
            System.out.println("DAO inicializado com sucesso!");
        } catch (ClassNotFoundException e) {
            throw new ServletException("Driver JDBC não encontrado. Verifique o ojdbc11.jar no classpath", e);
        } catch (SQLException e) {
            throw new ServletException("Erro ao conectar ao banco: " + e.getMessage(), e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action != null) {
                switch (action) {
                    case "inserir":
                        cadastrar(request, response);
                        break;
                    case "remover":
                        remover(request, response);
                        break;
                    case "pesquisar":
                        pesquisar(request, response);
                        break;
                    default:
                        listar(request, response);
                }
            } else {
                listar(request, response);
            }
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doPost(request, response);
    }

    private void cadastrar(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, SQLException, ParseException {

        if (request.getParameter("nome") == null || request.getParameter("valor") == null) {
            throw new ServletException("Parâmetros obrigatórios não informados");
        }

        Despesa despesa = new Despesa();
        despesa.setDescricao(request.getParameter("nome"));
        despesa.setValor(new BigDecimal(request.getParameter("valor")));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (request.getParameter("data") != null && !request.getParameter("data").isEmpty()) {
            despesa.setDataPagamento(sdf.parse(request.getParameter("data")));
        } else {
            despesa.setDataPagamento(new Date(System.currentTimeMillis())); // fallback
        }

        if (request.getParameter("vencimento") != null && !request.getParameter("vencimento").isEmpty()) {
            despesa.setVencimento(sdf.parse(request.getParameter("vencimento")));
        } else {
            throw new ServletException("Data de vencimento não informada");
        }

        despesa.setCategoriaDespesa(request.getParameter("categoria"));
        despesa.setFormaPagamento(request.getParameter("formaPagamento"));
        despesa.setStatusDespesa(request.getParameter("status"));

        if (request.getParameter("recorrente") != null && !request.getParameter("recorrente").isEmpty()) {
            despesa.setRecorrente(request.getParameter("recorrente").charAt(0));
        } else {
            despesa.setRecorrente('N');  // valor padrão
        }

        despesaDao.inserir(despesa);

        response.sendRedirect("despesa");
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        List<Despesa> lista = despesaDao.listar();
        double total = despesaDao.calcularTotal();

        request.setAttribute("listaDespesas", lista);
        request.setAttribute("totalDespesas", total);
        request.getRequestDispatcher("despesa.jsp").forward(request, response);
    }

    private void remover(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, SQLException {
        String descricao = request.getParameter("nome");
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new ServletException("Descrição não informada");
        }
        despesaDao.removerPorDescricao(descricao);
        response.sendRedirect("despesa");
    }

    private void pesquisar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, EntidadeNaoEncontrada {
        String termo = request.getParameter("termo");
        if (termo == null || termo.trim().isEmpty()) {
            throw new ServletException("Termo de pesquisa não informado");
        }

        List<Despesa> resultado = despesaDao.pesquisarPorDescricao(termo);
        double total = despesaDao.calcularTotal();

        request.setAttribute("listaDespesas", resultado);
        request.setAttribute("totalDespesas", total);
        request.getRequestDispatcher("/despesa.jsp").forward(request, response);
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException, IOException {
        e.printStackTrace();
        request.setAttribute("erro", "Erro: " + e.getMessage());
        try {
            listar(request, response);
        } catch (Exception ex) {
            throw new ServletException("Erro ao listar despesas após erro", ex);
        }
    }

    @Override
    public void destroy() {
        try {
            if (despesaDao != null) {
                despesaDao.fecharConexao();
                System.out.println("Conexão com o banco fechada");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}