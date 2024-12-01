import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import database.DatabaseConfig;
import utils.CacheLoader;

public class WorkerNode {
    public static void main(String[] args) {
        DataSource dataSource = DatabaseConfig.getDataSource();
        Connection dbConnection;
        CacheLoader cacheLoader = new CacheLoader();
        try {
            cacheLoader.loadCache();
            dbConnection = dataSource.getConnection();
            System.out.println("Connected to the database");
        } catch (Exception e) {
            dbConnection = null;
            e.printStackTrace();
        }

        if (dbConnection != null) {
            printWithoutCache(dbConnection);
        }
    }

    public static void printWithCache(Connection dbConnection, CacheLoader cacheLoader) {
        String query = "SELECT id, documento, nombre, apellido, mesa_id\n" + //
                "FROM public.ciudadano\n" +
                "LIMIT 1000;";
        try {
            long start = System.currentTimeMillis();
            PreparedStatement statement = dbConnection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String documento = resultSet.getString("documento");
                String nombre = resultSet.getString("nombre");
                String apellido = resultSet.getString("apellido");
                int mesaId = resultSet.getInt("mesa_id");
                String mesaInfo = cacheLoader.getCacheValue(mesaId);

                System.out.printf("Documento: %s, Nombre: %s, Apellido: %s, Mesa Información: %s%n",
                        documento, nombre, apellido, mesaInfo);

            }
            long end = System.currentTimeMillis();
            System.out.println("Time elapsed: " + (end - start) + "ms");
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void printWithoutCache(Connection dbConnection) {
        String query = "SELECT\n" + //
                "    c.documento AS ciudadano_documento,\n" + //
                "    c.nombre AS ciudadano_nombre,\n" + //
                "    c.apellido AS ciudadano_apellido,\n" + //
                "    pv.nombre as puesto_votacion_nombre,\n" + //
                "    pv.direccion as puesto_votacion_direccion,\n" + //
                "    pv.consecutive as puesto_votacion_consecutivo,\n" + //
                "    m.nombre as municipio_nombre,\n" + //
                "    d.nombre as departamento_nombre \n" + //
                "FROM\n" + //
                "    ciudadano c\n" + //
                "JOIN mesa_votacion mv ON c.mesa_id = mv.id\n" + //
                "JOIN puesto_votacion pv ON mv.puesto_id = pv.id\n" + //
                "JOIN municipio m ON pv.municipio_id = m.id\n" + //
                "JOIN departamento d ON m.departamento_id = d.id\n" + //
                "LIMIT 1000;";
        try {
            long start = System.currentTimeMillis();
            PreparedStatement statement = dbConnection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String documento = resultSet.getString("ciudadano_documento");
                String nombre = resultSet.getString("ciudadano_nombre");
                String apellido = resultSet.getString("ciudadano_apellido");
                String mesaInfo = String.format("Municipio: %s, Departamento: %s, Puesto: %s, Dirección: %s",
                        resultSet.getString("municipio_nombre"), resultSet.getString("departamento_nombre"),
                        resultSet.getString("puesto_votacion_nombre"),
                        resultSet.getString("puesto_votacion_direccion"));

                System.out.printf("Documento: %s, Nombre: %s, Apellido: %s, Mesa Información: %s%n",
                        documento, nombre, apellido, mesaInfo);

            }
            long end = System.currentTimeMillis();
            System.out.println("Time elapsed: " + (end - start) + "ms");
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
