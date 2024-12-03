package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utils.CacheLoader;
import utils.PrimeFactorizer;

public class QueryService {

    private final Connection dbConnection;
    private final CacheLoader cacheLoader;
    private final PrimeFactorizer primeFactorizer;

    public QueryService(Connection dbConnection, CacheLoader cacheLoader, PrimeFactorizer primeFactorizer) {
        this.dbConnection = dbConnection;
        this.cacheLoader = cacheLoader;
        this.primeFactorizer = primeFactorizer;
    }

    private List<String> queryWithCache(List<String> citizenIds) {
        String query = "SELECT id, documento, nombre, apellido, mesa_id FROM ciudadano WHERE documento IN (%s);";
        List<String> response = new ArrayList<>();

        String placeholders = citizenIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));
        query = String.format(query, placeholders);

        try (PreparedStatement statement = dbConnection.prepareStatement(query)) {
            for (int i = 0; i < citizenIds.size(); i++) {
                statement.setString(i + 1, citizenIds.get(i));
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String documento = resultSet.getString("documento");
                String nombre = resultSet.getString("nombre");
                String apellido = resultSet.getString("apellido");
                int mesaId = resultSet.getInt("mesa_id");
                String mesaInfo = cacheLoader.getCacheValue(mesaId);
                List<Long> primeFactors = primeFactorizer.getPrimeFactors(Long.parseLong(documento));
                Long primeFactorsSize = (long) primeFactors.size();
                int isPrime = primeFactorizer.getPrimeFactors(primeFactorsSize).size() == 0 ? 0 : 1;

                response.add(String.format(
                        "Documento: %s, Nombre: %s, Apellido: %s, Mesa Información: %s%n, Factores primos: %s%n",
                        documento, nombre, apellido, mesaInfo, isPrime));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return response;
    }

    private List<String> queryWithoutCache(List<String> citizenIds) {
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
        List<String> response = new ArrayList<>();

        String placeholders = citizenIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));
        query = String.format(query, placeholders);

        try (PreparedStatement statement = dbConnection.prepareStatement(query)) {
            for (int i = 0; i < citizenIds.size(); i++) {
                statement.setString(i + 1, citizenIds.get(i));
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String documento = resultSet.getString("documento");
                String nombre = resultSet.getString("nombre");
                String apellido = resultSet.getString("apellido");
                int mesaId = resultSet.getInt("mesa_id");
                String mesaInfo = cacheLoader.getCacheValue(mesaId);
                List<Long> primeFactors = primeFactorizer.getPrimeFactors(Long.parseLong(documento));
                Long primeFactorsSize = (long) primeFactors.size();
                int isPrime = primeFactorizer.getPrimeFactors(primeFactorsSize).size() == 0 ? 0 : 1;

                response.add(String.format(
                        "Documento: %s, Nombre: %s, Apellido: %s, Mesa Información: %s%n, Factores primos: %s%n",
                        documento, nombre, apellido, mesaInfo, isPrime));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return response;
    }

    public String querySingleDocument(String document) {
        List<String> citizenDocuments = List.of(document);
        List<String> response = queryWithCache(citizenDocuments);
        if (!response.isEmpty()) {
            return response.get(0);
        }
        return null;
    }

    public List<String> queryMultipleDocuments(List<String> documents) {
        List<String> response = queryWithCache(documents);
        return response;
    }
}
