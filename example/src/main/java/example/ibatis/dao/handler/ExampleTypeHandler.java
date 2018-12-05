package example.ibatis.dao.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: andyxu
 * Date: 2018/12/5
 * Time: 10:23
 */
public class ExampleTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, transferColonToUnderline(parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return transferUnderlineToColon(rs.getString(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return transferUnderlineToColon(rs.getString(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return transferUnderlineToColon(cs.getString(columnIndex));
    }

    private String transferUnderlineToColon(String data) {
        if (null != data && data.contains("_")) {
            return data.replaceAll("_", ":");
        }
        return data;
    }
    private String transferColonToUnderline(String data) {
        if (null != data && data.contains(":")) {
            return data.replaceAll(":", "_");
        }
        return data;
    }
}
