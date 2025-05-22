package br.com.fiap.dao;

import br.com.fiap.exception.EntidadeNaoEncontrada;
import br.com.fiap.factory.ConnectionFactory;
import br.com.fiap.model.Despesa;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DespesaDao {
    private final Connection conexao;

    public DespesaDao() throws SQLException, ClassNotFoundException {
        conexao = ConnectionFactory.getConnection();
    }

    public void inserir(Despesa despesa) throws SQLException {
        String sql = "INSERT INTO DESPESA (ID_DESPESA, VALOR, DATA_PAGAMENTO, VENCIMENTO, DESCRICAO, " +
                "CATEGORIA_DESPESA, STATUS_DESPESA, RECORRENTE, USUARIO_ID_USUARIO, CONTA_ID_CONTA) " +
                "VALUES (seq_despesa.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setBigDecimal(1, despesa.getValor());
            stm.setDate(2, new Date(despesa.getDataPagamento().getTime()));
            stm.setDate(3, new Date(despesa.getVencimento().getTime()));
            stm.setString(4, despesa.getDescricao());
            stm.setString(5, despesa.getCategoriaDespesa());
            String status = despesa.getStatusDespesa().toUpperCase().trim();
            stm.setString(6, status);
            stm.setString(7, String.valueOf(despesa.getRecorrente()));
            stm.setInt(8, despesa.getUsuarioId());
            stm.setInt(9, despesa.getContaId());

            stm.executeUpdate();
        }
    }

    public List<Despesa> pesquisarPorDescricao(String termo) throws SQLException {
        List<Despesa> despesas = new ArrayList<>();

        String sql = "SELECT * FROM despesa WHERE LOWER(TRIM(descricao)) LIKE LOWER(TRIM(?))";

        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, "%" + termo.trim() + "%");

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Despesa despesa = new Despesa();
            despesa.setId(rs.getInt("id"));
            despesa.setDescricao(rs.getString("descricao"));
            despesa.setValor(BigDecimal.valueOf(rs.getDouble("valor")));
            despesa.setData(rs.getDate("data").toLocalDate());

            despesas.add(despesa);
        }

        rs.close();
        stmt.close();

        return despesas;
    }

    public List<Despesa> listar() throws SQLException {
        String sql = "SELECT * FROM DESPESA ORDER BY DATA_PAGAMENTO DESC";
        try (PreparedStatement stm = conexao.prepareStatement(sql)) {
            ResultSet result = stm.executeQuery();
            List<Despesa> lista = new ArrayList<>();
            while (result.next()) {
                lista.add(parseDespesa(result));
            }
            return lista;
        }
    }

    public void removerPorId(int id) throws SQLException {
        String sql = "DELETE FROM DESPESA WHERE ID_DESPESA = ?";
        try (PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setInt(1, id);
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhuma despesa encontrada com ID" + id + "para remover");
            }
        }
    }

    public void atualizar(Despesa despesa) throws SQLException, EntidadeNaoEncontrada {
        PreparedStatement stm = conexao.prepareStatement(
                "UPDATE despesa SET descricao = ?, valor = ?, data_pagamento = ?, vencimento = ?, categoria_despesa = ?, forma_pagamento = ?, status_despesa = ?, recorrente = ?, usuario_id_usuario = ?, conta_id_conta = ? WHERE id_despesa = ?");

        stm.setString(1, despesa.getDescricao());
        stm.setBigDecimal(2, despesa.getValor());
        stm.setDate(3, new java.sql.Date(despesa.getDataPagamento().getTime()));
        stm.setDate(4, new java.sql.Date(despesa.getVencimento().getTime()));
        stm.setString(5, despesa.getCategoriaDespesa());
        stm.setString(6, despesa.getFormaPagamento());
        stm.setString(7, despesa.getStatusDespesa());
        stm.setString(8, String.valueOf(despesa.getRecorrente()));
        stm.setInt(9, despesa.getUsuarioId());
        stm.setInt(10, despesa.getContaId());
        stm.setInt(11, despesa.getIdDespesa());

        if (stm.executeUpdate() == 0) {
            throw new EntidadeNaoEncontrada("Despesa não encontrada para atualização.");
        }
    }

    public double calcularTotal() throws SQLException {
        String sql = "SELECT SUM(VALOR) AS TOTAL FROM DESPESA";
        try (PreparedStatement stm = conexao.prepareStatement(sql)) {
            ResultSet result = stm.executeQuery();
            return result.next() ? result.getDouble("TOTAL") : 0;
        }
    }

    private Despesa parseDespesa(ResultSet result) throws SQLException {
        Despesa despesa = new Despesa();
        despesa.setIdDespesa(result.getInt("ID_DESPESA"));
        despesa.setValor(result.getBigDecimal("VALOR"));
        despesa.setDataPagamento(result.getDate("DATA_PAGAMENTO"));
        despesa.setVencimento(result.getDate("VENCIMENTO"));
        despesa.setDescricao(result.getString("DESCRICAO"));
        despesa.setCategoriaDespesa(result.getString("CATEGORIA_DESPESA"));
        despesa.setFormaPagamento(result.getString("FORMA_PAGAMENTO"));
        despesa.setStatusDespesa(result.getString("STATUS_DESPESA"));

        String recorrenteStr = result.getString("RECORRENTE");
        despesa.setRecorrente(recorrenteStr != null && !recorrenteStr.isEmpty() ? recorrenteStr.charAt(0) : 'N'); // Ajuste seguro

        despesa.setUsuarioId(result.getInt("USUARIO_ID_USUARIO"));
        despesa.setContaId(result.getInt("CONTA_ID_CONTA"));
        return despesa;
    }

    public Map<String, Double> getTotalDespesasPorCategoria() throws SQLException {
        Map<String, Double> mapa = new HashMap<>();
        String sql = "SELECT CATEGORIA_DESPESA, SUM(VALOR) AS TOTAL FROM DESPESA GROUP BY CATEGORIA_DESPESA";

        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                mapa.put(rs.getString("CATEGORIA_DESPESA"), rs.getDouble("TOTAL"));
            }
        }
        return mapa;
    }

    public Despesa pesquisarPorId(int id) throws SQLException, EntidadeNaoEncontrada {
        String sql = "SELECT * FROM despesa WHERE id_despesa = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Despesa despesa = new Despesa();
                despesa.setIdDespesa(rs.getInt("id_despesa"));
                despesa.setDescricao(rs.getString("descricao"));
                despesa.setValor(rs.getBigDecimal("valor"));
                despesa.setDataPagamento(rs.getDate("data_pagamento"));
                despesa.setVencimento(rs.getDate("vencimento"));
                despesa.setCategoriaDespesa(rs.getString("categoria"));
                despesa.setFormaPagamento(rs.getString("forma_pagamento"));
                despesa.setStatusDespesa(rs.getString("status"));
                despesa.setRecorrente(rs.getString("recorrente").charAt(0));
                return despesa;
            } else {
                throw new EntidadeNaoEncontrada("Despesa não encontrada com ID: " + id);
            }
        }
    }

    public void fecharConexao() throws SQLException {
        if (conexao != null && !conexao.isClosed()) {
            conexao.close();
        }
    }
}