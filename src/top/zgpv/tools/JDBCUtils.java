package top.zgpv.tools;

import org.apache.commons.dbcp.BasicDataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Jdbc utils.
 */
public class JDBCUtils {
    /**
     * The constant DRIVER_CLASS_NAME.
     */
    public static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    /**
     * The constant URL.
     */
    public static final String URL = "jdbc:mysql://39.zgpv.top:3306/gjp";
    /**
     * The constant USERNAME.
     */
    public static final String USERNAME = "root";
    /**
     * The constant PASSWORD.
     */
    public static final String PASSWORD = "123456";

    private static final int MAX_IDLE = 3;
    private static final long MAX_WAIT = 5000;
    private static final int MAX_ACTIVE = 5;
    private static final int INITIAL_SIZE = 3;

    private static BasicDataSource dataSource = new BasicDataSource();

    static {
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        dataSource.setMaxIdle(MAX_IDLE);
        dataSource.setMaxWait(MAX_WAIT);
        dataSource.setMaxActive(MAX_ACTIVE);
        dataSource.setInitialSize(INITIAL_SIZE);

        getConnection();
    }

    private static Connection connection;
    private static PreparedStatement pstmt;
    private static ResultSet resultSet;

    private static Map<String, Object> rowToMap(ResultSet resultSet, ResultSetMetaData metaData) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        int colLen = metaData.getColumnCount();
        for (int i = 0; i < colLen; i++) {
            String colsName = metaData.getColumnName(i + 1);
            Object colsValue = resultSet.getObject(colsName);
            if (colsValue == null) {
                colsValue = "";
            }
            map.put(colsName, colsValue);
        }
        return map;
    }

    /**
     * Gets connection.
     *
     * @return the connection
     */
    public static Connection getConnection() {
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Add or delete and update boolean.
     *
     * @param sql the sql
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public static boolean update(String sql) throws SQLException {
        pstmt = connection.prepareStatement(sql);
        int i = pstmt.executeUpdate();
        return i > 0 ? true : false;

    }

    /**
     * Find one map.
     *
     * @param sql the sql
     * @return the map
     * @throws SQLException the sql exception
     */
    public static Map<String, Object> findOne(String sql) throws SQLException {
        pstmt = connection.prepareStatement(sql);
        resultSet = pstmt.executeQuery();
        Map<String, Object> map = null;
        ResultSetMetaData metaData = resultSet.getMetaData();
        while (resultSet.next()) {
            map = rowToMap(resultSet, metaData);
        }
        return map;
    }

    /**
     * Find list.
     *
     * @param sql the sql
     * @return the list
     * @throws SQLException the sql exception
     */
    public static List<Map<String, Object>> find(String sql) throws SQLException {
        pstmt = connection.prepareStatement(sql);
        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        List<Map<String, Object>> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(rowToMap(resultSet, metaData));
        }
        return list;
    }

    /**
     * Fine one t.
     *
     * @param <T> the type parameter
     * @param sql the sql
     * @param cls the cls
     * @return the t
     * @throws Exception the exception
     */
    public static <T> T fineOne(String sql, Class<T> cls) throws Exception {
        pstmt = connection.prepareStatement(sql);
        resultSet = pstmt.executeQuery();

        Constructor declaredConstructor = cls.getDeclaredConstructor();

        Object obj = declaredConstructor.newInstance();
        Field[] declaredFields = cls.getDeclaredFields();

        while (resultSet.next()) {
            for (Field f : declaredFields) {
                String name = f.getName();
                Object value = resultSet.getObject(name);

                f.set(obj, value);
            }
        }

        return (T) obj;
    }

    /**
     * Find list.
     *
     * @param <T> the type parameter
     * @param sql the sql
     * @param cls the cls
     * @return the list
     * @throws Exception the exception
     */
    public static <T> List<T> find(String sql, Class<T> cls) throws Exception {
        pstmt = connection.prepareStatement(sql);
        resultSet = pstmt.executeQuery();

        Constructor declaredConstructor = cls.getDeclaredConstructor();

        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            Object obj = declaredConstructor.newInstance();
            Field[] declaredFields = cls.getDeclaredFields();
            for (Field f : declaredFields) {
                String name = f.getName();
                Object value = resultSet.getObject(name);

                f.set(obj, value);
            }
            list.add((T) obj);
        }
        return list;
    }
}
