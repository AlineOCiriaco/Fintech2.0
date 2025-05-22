package br.com.fiap.dao;

import br.com.fiap.exception.EntidadeNaoEncontrada;
import br.com.fiap.factory.ConnectionFactory;
import br.com.fiap.model.Despesa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<br.com.fiap.model.Despesa> pesquisarPorDescricao(String descricao) throws SQLException, EntidadeNaoEncontrada {
        String sql = "SELECT * FROM DESPESA WHERE DESCRICAO LIKE ? ORDER BY DATA_PAGAMENTO DESC";
        try (PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setString(1, "%" + descricao + "%");
            ResultSet result = stm.executeQuery();

            List<Despesa> lista = new ArrayList<>();
            while (result.next()) {
                lista.add(parseDespesa(result));
            }

            if (lista.isEmpty()) {
                throw new EntidadeNaoEncontrada("Nenhuma despesa encontrada");
            }
            return lista;
        }
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

    public void removerPorDescricao(String descricao) throws SQLException {
        String sql = "DELETE FROM DESPESA WHERE DESCRICAO = ?";
        try (PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setString(1, descricao);
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhuma despesa encontrada para remover");
            }
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

    public void fecharConexao() throws SQLException {
        if (conexao != null && !conexao.isClosed()) {
            conexao.close();
        }
    }
}