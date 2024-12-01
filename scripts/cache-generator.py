import psycopg2

# Configuración de conexión a la base de datos
DB_CONFIG = {
    "dbname": "votaciones",
    "user": "postgres",
    "password": "postgres",
    "host": "xhgrid2",
    "port": 5432
}

# Archivo de salida
CACHE_FILE = "cache.txt"

# Query para hacer el join de las tablas
QUERY = """
SELECT
    mv.id AS mesa_votacion_id,
    pv.nombre as puesto_votacion_nombre,
    pv.direccion as puesto_votacion_direccion,
    pv.consecutive as puesto_votacion_consecutivo,
    m.nombre as municipio_nombre,
    d.nombre as departamento_nombre 
FROM
    mesa_votacion mv
JOIN puesto_votacion pv ON mv.puesto_id = pv.id
JOIN municipio m ON pv.municipio_id = m.id
JOIN departamento d ON m.departamento_id = d.id;
"""

def generate_cache_file():
    try:
        # Conectar a la base de datos
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        # Ejecutar el query
        cursor.execute(QUERY)
        
        # Recuperar los resultados
        rows = cursor.fetchall()

        # Generar el archivo de cache
        with open(CACHE_FILE, "w") as file:
            for row in rows:
                key = row[0]  # id de mesa_votacion
                value = f"{row[1]}|{row[2]}|{row[3]}|{row[4]}|{row[5]}"  # Concatenar las columnas
                file.write(f"{key}:{value}\n")
        
        print(f"Archivo de cache generado exitosamente en {CACHE_FILE}")
    
    except Exception as e:
        print(f"Error al generar el archivo de cache: {e}")
    
    finally:
        # Cerrar la conexión a la base de datos
        if cursor:
            cursor.close()
        if conn:
            conn.close()

if __name__ == "__main__":
    generate_cache_file()
