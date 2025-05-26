package br.com.fiap.factory;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl";


    private static final String USUARIO = "RM560198";

    private static final String SENHA = "260391";


    // método para obter uma conexão com o banco de dados

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC não encontrado", e);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

}
