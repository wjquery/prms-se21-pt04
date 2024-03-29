package sg.edu.nus.iss.phoenix.authenticate.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import sg.edu.nus.iss.phoenix.authenticate.dao.RoleDao;
import sg.edu.nus.iss.phoenix.authenticate.entity.Role;
import sg.edu.nus.iss.phoenix.core.dao.DBConstants;
import sg.edu.nus.iss.phoenix.core.exceptions.NotFoundException;

/**
 * Role Data Access Object (DAO). This class contains all database handling that
 * is needed to permanently store and retrieve Role object instances.
 */
public class RoleDaoImpl implements RoleDao {

    private static final Logger logger = Logger.getLogger(RoleDaoImpl.class.getName());
    private DataSource ds;
    //Connection connection;

    public RoleDaoImpl() {
        super();
        // TODO Auto-generated constructor stub
        //connection = openConnection();
        try {
            ds = (DataSource) InitialContext.doLookup("jdbc/PrmsDatasource");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Cannot connect to the DS");
        }
    }
    
    public RoleDaoImpl(DataSource ds)
    {
        this.ds = ds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * sg.edu.nus.iss.phoenix.authenticate.dao.impl.RoleDao#createValueObject()
     */
    @Override
    public Role createValueObject() {
        return new Role();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * sg.edu.nus.iss.phoenix.authenticate.dao.impl.RoleDao#getObject(java.sql
     * .Connection, java.lang.String)
     */
    @Override
    public Role getObject(String role) throws NotFoundException, SQLException {

        Role valueObject = createValueObject();
        valueObject.setRole(role);
        load(valueObject);
        return valueObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * sg.edu.nus.iss.phoenix.authenticate.dao.impl.RoleDao#load(java.sql.Connection
     * , sg.edu.nus.iss.phoenix.authenticate.entity.Role)
     */
    @Override
    public void load(Role valueObject) throws NotFoundException, SQLException {

        if (valueObject.getRole() == null) {
            // System.out.println("Can not select without Primary-Key!");
            throw new NotFoundException("Can not select without Primary-Key!");
        }

        String sql = "SELECT * FROM APP.\"role\" WHERE (\"role\" = ? ) ";
        try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, valueObject.getRole());

            singleQuery(stmt, valueObject);

        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * sg.edu.nus.iss.phoenix.authenticate.dao.impl.RoleDao#loadAll(java.sql
     * .Connection)
     */
    @Override
    public List<Role> loadAll() throws SQLException {
        List<Role> searchResults = null;
        String sql = "SELECT * FROM APP.\"role\" ORDER BY \"role\" ASC ";
        try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            searchResults = listQuery(stmt);
            System.out.println("record size" + searchResults.size());
        }
        return searchResults;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * sg.edu.nus.iss.phoenix.authenticate.dao.impl.RoleDao#create(java.sql.
     * Connection, sg.edu.nus.iss.phoenix.authenticate.entity.Role)
     */
    @Override
    public synchronized void create(Role valueObject) throws SQLException {

        String sql = "INSERT INTO APP.\"role\" ( \"role\", \"accessPrivilege\") VALUES (?, ?) ";

        try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {

            stmt.setString(1, valueObject.getRole());
            stmt.setString(2, valueObject.getAccessPrivilege());

            int rowcount = databaseUpdate(stmt);
            if (rowcount != 1) {
                // System.out.println("PrimaryKey Error when updating DB!");
                throw new SQLException("PrimaryKey Error when updating DB!");
            }

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * sg.edu.nus.iss.phoenix.authenticate.dao.impl.RoleDao#save(java.sql.Connection
     * , sg.edu.nus.iss.phoenix.authenticate.entity.Role)
     */
    @Override
    public void save(Role valueObject) throws NotFoundException, SQLException {

        String sql = "UPDATE APP.\"role\" SET \"accessPrivilege\" = ? WHERE (\"role\" = ? ) ";
        try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, valueObject.getAccessPrivilege());

            stmt.setString(2, valueObject.getRole());

            int rowcount = databaseUpdate(stmt);
            if (rowcount == 0) {
                // System.out.println("Object could not be saved! (PrimaryKey not found)");
                throw new NotFoundException(
                        "Object could not be saved! (PrimaryKey not found)");
            }
            if (rowcount > 1) {
                // System.out.println("PrimaryKey Error when updating DB! (Many objects were affected!)");
                throw new SQLException(
                        "PrimaryKey Error when updating DB! (Many objects were affected!)");
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * sg.edu.nus.iss.phoenix.authenticate.dao.impl.RoleDao#delete(java.sql.
     * Connection, sg.edu.nus.iss.phoenix.authenticate.entity.Role)
     */
    @Override
    public void delete(Role valueObject) throws NotFoundException, SQLException {

        if (valueObject.getRole() == null) {
            // System.out.println("Can not delete without Primary-Key!");
            throw new NotFoundException("Can not delete without Primary-Key!");
        }

        String sql = "DELETE FROM APP.\"role\" WHERE (\"role\" = ? ) ";
        try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, valueObject.getRole());

            int rowcount = databaseUpdate(stmt);
            if (rowcount == 0) {
                // System.out.println("Object could not be deleted (PrimaryKey not found)");
                throw new NotFoundException(
                        "Object could not be deleted! (PrimaryKey not found)");
            }
            if (rowcount > 1) {
                // System.out.println("PrimaryKey Error when updating DB! (Many objects were deleted!)");
                throw new SQLException(
                        "PrimaryKey Error when updating DB! (Many objects were deleted!)");
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * sg.edu.nus.iss.phoenix.authenticate.dao.impl.RoleDao#deleteAll(java.sql
     * .Connection)
     */
    @Override
    public void deleteAll() throws SQLException {

        String sql = "DELETE FROM APP.\"role\"";
        try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            int rowcount = databaseUpdate(stmt);
            System.out.println("deleted rows" + rowcount);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * sg.edu.nus.iss.phoenix.authenticate.dao.impl.RoleDao#countAll(java.sql
     * .Connection)
     */
    @Override
    public int countAll() throws SQLException {

        String sql = "SELECT count(*) FROM APP.\"role\"";

        ResultSet result = null;
        int allRows = 0;
        try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            result = stmt.executeQuery();

            if (result.next()) {
                allRows = result.getInt(1);
            }
        }
        return allRows;
    }

    @Override
    public Role searchMatching(String role) throws SQLException {
        try {
            return (getObject(role));
        } catch (NotFoundException ex) {
            logger.log(Level.WARNING, "Cannot find role {0}", role);
        }
        return (null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * sg.edu.nus.iss.phoenix.authenticate.dao.impl.RoleDao#searchMatching(java
     * .sql.Connection, sg.edu.nus.iss.phoenix.authenticate.entity.Role)
     */
    @Override
    public List<Role> searchMatching(Role valueObject) throws SQLException {

        List<Role> searchResults = null;

        boolean first = true;
        StringBuffer sql = new StringBuffer("SELECT * FROM APP.\"role\" WHERE 1=1 ");

        if (valueObject.getRole() != null) {
            if (first) {
                first = false;
            }
            sql.append("AND \"role\" LIKE '").append(valueObject.getRole())
                    .append("%' ");
        }

        if (valueObject.getAccessPrivilege() != null) {
            if (first) {
                first = false;
            }
            sql.append("AND \"accessPrivilege\" LIKE '%")
                    .append(valueObject.getAccessPrivilege()).append("%' ");
        }

        sql.append("ORDER BY \"role\" ASC ");

        // Prevent accidential full table results.
        // Use loadAll if all rows must be returned.
        if (first) {
            searchResults = new ArrayList<Role>();
        } else {
            try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString());) {
                searchResults = listQuery(stmt);
            }
        }
        return searchResults;
    }

    /**
     * databaseUpdate-method. This method is a helper method for internal use.
     * It will execute all database handling that will change the information in
     * tables. SELECT queries will not be executed here however. The return
     * value indicates how many rows were affected. This method will also make
     * sure that if cache is used, it will reset when data changes.
     *
     * @param conn This method requires working database connection.
     * @param stmt This parameter contains the SQL statement to be excuted.
     */
    protected int databaseUpdate(PreparedStatement stmt) throws SQLException {

        int result = stmt.executeUpdate();

        return result;
    }

    /**
     * databaseQuery-method. This method is a helper method for internal use. It
     * will execute all database queries that will return only one row. The
     * resultset will be converted to valueObject. If no rows were found,
     * NotFoundException will be thrown.
     *
     * @param conn This method requires working database connection.
     * @param stmt This parameter contains the SQL statement to be excuted.
     * @param valueObject Class-instance where resulting data will be stored.
     */
    protected void singleQuery(PreparedStatement stmt, Role valueObject)
            throws NotFoundException, SQLException {

        ResultSet result = null;

        try {
            result = stmt.executeQuery();

            if (result.next()) {

                valueObject.setRole(result.getString("role"));
                valueObject.setAccessPrivilege(result
                        .getString("accessPrivilege"));

            } else {
                // System.out.println("Role Object Not Found!");
                throw new NotFoundException("Role Object Not Found!");
            }
        } finally {
            if (result != null) {
                result.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            //closeConnection();
        }
    }

    /**
     * databaseQuery-method. This method is a helper method for internal use. It
     * will execute all database queries that will return multiple rows. The
     * resultset will be converted to the List of valueObjects. If no rows were
     * found, an empty List will be returned.
     *
     * @param conn This method requires working database connection.
     * @param stmt This parameter contains the SQL statement to be excuted.
     */
    protected List<Role> listQuery(PreparedStatement stmt) throws SQLException {

        ArrayList<Role> searchResults = new ArrayList<Role>();
        ResultSet result = null;
        //connection = openConnection();
        try {
            result = stmt.executeQuery();

            while (result.next()) {
                Role temp = createValueObject();

                temp.setRole(result.getString("role"));
                temp.setAccessPrivilege(result.getString("accessPrivilege"));

                searchResults.add(temp);
            }

        } finally {
            if (result != null) {
                result.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            //closeConnection();
        }

        return (List<Role>) searchResults;
    }

    /*
     private Connection openConnection() {
     Connection conn = null;

     try {
     Class.forName(DBConstants.COM_MYSQL_JDBC_DRIVER);
     } catch (ClassNotFoundException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
     }

     try {
     conn = DriverManager.getConnection(DBConstants.dbUrl,
     DBConstants.dbUserName, DBConstants.dbPassword);
     } catch (SQLException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
     }
     return conn;
     }

     private void closeConnection() {
     try {
     this.connection.close();
     } catch (SQLException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
     }
     }
     * */
}
