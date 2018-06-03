package top.zgpv.tools;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class JDBCUtilsTest {
    public static void main(String[] args) throws SQLException {
        String sql = "INSERT INTO gjp_sort (sname, parent, sdesc) VALUES (111, 222, 333)";
        JDBCUtils.update(sql);


        String sql3 = "UPDATE gjp_sort SET sname='update' WHERE sid=1";
        JDBCUtils.update(sql3);

        String sql4 = "DELETE FROM gjp_sort WHERE sid=2";
        JDBCUtils.update(sql4);

        String sql2 = "SELECT * FROM gjp_sort";
        List<Map<String, Object>> maps = JDBCUtils.find(sql2);
        System.out.println(maps);

    }
}
