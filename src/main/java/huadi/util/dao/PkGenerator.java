package huadi.util.dao;

import java.sql.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * PrimaryKeyGenerator. Based on MySQL.
 *
 * @author HUADI
 * @see PkGenerator#main(String[]) for example
 */
public class PkGenerator {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new Error("No JDBC driver for MySQL.");
        }
    }

    private static final String HOST = "MySQL host";
    private static final int PORT = 3306;
    private static final String DB = "db name";

    private String connStr = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB;
    private String user = "user";
    private String pwd = "password";

    /*
        CREATE TABLE `pk_sequence` (
          `id` int(11) NOT NULL AUTO_INCREMENT,
          `k` varchar(16) NOT NULL,
          `v` bigint(20) NOT NULL,
          `step` int(10) unsigned NOT NULL,
          `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
          PRIMARY KEY (`id`),
          UNIQUE KEY `IDX_k_UNIQUE` (`k`)
        )
     */
    private String sequenceTableName = "pk_sequence";
    private String keyColumn = "k";
    private String valueColumn = "v";
    private String stepColumn = "step";

    private String keyName = "user_id";


    private PkPool pk = new PkPool(0, -1); // 初始化一个不能用的pool, 第一次使用会触发更新. 省得每次在get方法中做null判断.

    public Long get() {
        long primaryKey;
        if ((primaryKey = pk.next()) == PkPool.INVALID_VALUE) {
            synchronized (this) {
                while ((primaryKey = pk.next()) == PkPool.INVALID_VALUE) {
                    pk = load();
                }
            }
        }
        return primaryKey;
    }

    private PkPool load() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            conn = DriverManager.getConnection(connStr, user, pwd);

            int retry = 0;
            while (true) {
                stmt = conn.prepareStatement(
                        "SELECT * FROM " + sequenceTableName + " WHERE " + keyColumn + "='" + keyName + "'");
                rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw new RuntimeException(
                            "No result in table \"" + sequenceTableName + "\" with key name \"" + keyName + "\".");
                }
                Long oldValue = rs.getLong(valueColumn);
                Long stepValue = rs.getLong(stepColumn);
                if (stepValue == 0L) {
                    throw new RuntimeException(
                            "Setting step to 0 will cause an endless loop while getting new PK pool.");
                }
                Long newValue = oldValue + stepValue;
                if (newValue < Long.MIN_VALUE + stepValue) {
                    throw new RuntimeException("Primary key overflow.");
                }

                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Exception on close resource." + e);
                }
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Exception on close resource." + e);
                }

                stmt = conn.prepareStatement(
                        "UPDATE " + sequenceTableName + " SET " + valueColumn + "=" + newValue +
                                " WHERE " + keyColumn + "='" + keyName + "' AND " + valueColumn + "=" + oldValue);
                if (stmt.executeUpdate() != 0) {
                    // 这里使用oldValue作为pk池, 这样所有小于db中valueColumn域的pk都可以认为是使用过的.
                    return new PkPool(oldValue + 1, oldValue + stepValue);
                }

                if (++retry > 3) {
                    System.err.println(
                            "PK generate failed " + retry + " times. KeyName: \"" + keyName + "\", old: " + oldValue
                                    + ", new: " + newValue + ", step: " + stepValue + ".");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Exception on generating primary key.", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {}
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignored) {}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    /*
     * 对于get方法来讲, pk和max必须保证原子更新, 所以这里用一个class封装.
     */
    private static class PkPool {
        static final long INVALID_VALUE = Long.MIN_VALUE;

        AtomicLong pk;
        long max;

        PkPool(long pk, long max) {
            this.pk = new AtomicLong(pk);
            this.max = max;
        }

        long next() {
            long v = pk.getAndIncrement();
            return v > max ? INVALID_VALUE : v;
        }
    }


    public String getConnStr() {
        return connStr;
    }

    /**
     * @param connStr jdbc:mysql://yommou.com:3306/jwlwl 请注意需要有db名.
     */
    public void setConnStr(String connStr) {
        this.connStr = connStr;
    }

    public String getUser() {
        return user;
    }

    /**
     * @param user 连接用户名
     */
    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    /**
     * @param pwd 连接密码
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSequenceTableName() {
        return sequenceTableName;
    }

    /**
     * @param sequenceTableName 生成sequence使用的表名
     */
    public void setSequenceTableName(String sequenceTableName) {
        this.sequenceTableName = sequenceTableName;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    /**
     * @param keyColumn sequence表key的column名
     */
    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public String getValueColumn() {
        return valueColumn;
    }

    /**
     * @param valueColumn sequence表value的column名
     */
    public void setValueColumn(String valueColumn) {
        this.valueColumn = valueColumn;
    }

    public String getStepColumn() {
        return stepColumn;
    }

    /**
     * @param stepColumn sequence表step的column名
     */
    public void setStepColumn(String stepColumn) {
        this.stepColumn = stepColumn;
    }

    public String getKeyName() {
        return keyName;
    }

    /**
     * @param keyName sequence名, 对应sequence表key字段的值
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }


    /**
     * Example
     */
    public static void main(String[] args) {
        PkGenerator pkGenerator = new PkGenerator();
        pkGenerator.setKeyName("user_id");
        for (int i = 0; i < 20; i++) {
            System.out.println(pkGenerator.get());
        }
    }
}
