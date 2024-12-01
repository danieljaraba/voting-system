package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import utils.CacheLoader;
import utils.PrimeFactorizer;

public class DatabaseQueryManager {

    private final Connection dbConnection;
    private final CacheLoader cacheLoader;
    private final PrimeFactorizer primeFactorizer;

    public DatabaseQueryManager(Connection dbConnection, CacheLoader cacheLoader, PrimeFactorizer primeFactorizer) {
        this.dbConnection = dbConnection;
        this.cacheLoader = cacheLoader;
        this.primeFactorizer = primeFactorizer;
    }

    public void queryWithCache(List<Integer> citizenIds) {
        String query = "SELECT id, documento, nombre, apellido, mesa_id FROM ciudadano WHERE documento IN (%s);";

        String placeholders = citizenIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));
        query = String.format(query, placeholders);

        try (PreparedStatement statement = dbConnection.prepareStatement(query)) {
            for (int i = 0; i < citizenIds.size(); i++) {
                statement.setString(i + 1, String.valueOf(citizenIds.get(i))); // Convertir IDs a cadenas
            }

            long start = System.currentTimeMillis();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String documento = resultSet.getString("documento");
                String nombre = resultSet.getString("nombre");
                String apellido = resultSet.getString("apellido");
                int mesaId = resultSet.getInt("mesa_id");
                String mesaInfo = cacheLoader.getCacheValue(mesaId);
                List<Long> primeFactors = primeFactorizer.getPrimeFactors(Long.parseLong(documento));

                System.out.printf(
                        "Documento: %s, Nombre: %s, Apellido: %s, Mesa Información: %s%n, Factores primos: %s%n",
                        documento, nombre, apellido, mesaInfo, primeFactors);
            }
            long end = System.currentTimeMillis();
            System.out.println("Time elapsed (with cache): " + (end - start) + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void queryWithoutCache(List<Integer> citizenIds) {
        String query = "SELECT " +
                "c.id AS ciudadano_id, " +
                "c.documento AS ciudadano_documento, " +
                "c.nombre AS ciudadano_nombre, " +
                "c.apellido AS ciudadano_apellido, " +
                "pv.nombre AS puesto_votacion_nombre, " +
                "pv.direccion AS puesto_votacion_direccion, " +
                "pv.consecutive AS puesto_votacion_consecutivo, " +
                "m.nombre AS municipio_nombre, " +
                "d.nombre AS departamento_nombre " +
                "FROM ciudadano c " +
                "JOIN mesa_votacion mv ON c.mesa_id = mv.id " +
                "JOIN puesto_votacion pv ON mv.puesto_id = pv.id " +
                "JOIN municipio m ON pv.municipio_id = m.id " +
                "JOIN departamento d ON m.departamento_id = d.id " +
                "WHERE c.documento IN (%s);";

        String placeholders = citizenIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));
        query = String.format(query, placeholders);

        try (PreparedStatement statement = dbConnection.prepareStatement(query)) {
            for (int i = 0; i < citizenIds.size(); i++) {
                statement.setString(i + 1, String.valueOf(citizenIds.get(i))); // Convertir IDs a cadenas
            }

            long start = System.currentTimeMillis();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String documento = resultSet.getString("ciudadano_documento");
                String nombre = resultSet.getString("ciudadano_nombre");
                String apellido = resultSet.getString("ciudadano_apellido");
                String mesaInfo = String.format("Municipio: %s, Departamento: %s, Puesto: %s, Dirección: %s",
                        resultSet.getString("municipio_nombre"), resultSet.getString("departamento_nombre"),
                        resultSet.getString("puesto_votacion_nombre"),
                        resultSet.getString("puesto_votacion_direccion"));
                List<Long> primeFactors = primeFactorizer.getPrimeFactors(Long.parseLong(documento));

                System.out.printf(
                        "Documento: %s, Nombre: %s, Apellido: %s, Mesa Información: %s%n, Factores primos: %s%n",
                        documento, nombre, apellido, mesaInfo, primeFactors);
            }
            long end = System.currentTimeMillis();
            System.out.println("Time elapsed (without cache): " + (end - start) + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
