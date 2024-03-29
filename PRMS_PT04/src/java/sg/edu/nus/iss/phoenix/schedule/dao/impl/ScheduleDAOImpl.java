package sg.edu.nus.iss.phoenix.schedule.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import sg.edu.nus.iss.phoenix.authenticate.dao.UserDao;
import sg.edu.nus.iss.phoenix.authenticate.dao.impl.UserDaoImpl;
import sg.edu.nus.iss.phoenix.authenticate.entity.User;
import sg.edu.nus.iss.phoenix.core.exceptions.NotFoundException;
import sg.edu.nus.iss.phoenix.presenter.entity.Presenter;
import sg.edu.nus.iss.phoenix.presenter.service.PresenterService;
import sg.edu.nus.iss.phoenix.producer.entity.Producer;
import sg.edu.nus.iss.phoenix.producer.service.ProducerService;
import sg.edu.nus.iss.phoenix.radioprogram.entity.RadioProgram;
import sg.edu.nus.iss.phoenix.schedule.dao.ScheduleDAO;
import sg.edu.nus.iss.phoenix.schedule.entity.ProgramSlot;
import sg.edu.nus.iss.phoenix.schedule.entity.PSSearchObject;
import sg.edu.nus.iss.phoenix.utils.SDFUtils;

/**
 *
 * @author Jha Archana
 */
public class ScheduleDAOImpl implements ScheduleDAO {
    private static final Logger logger = Logger.getLogger(ScheduleDAOImpl.class.getName());
   
        DataSource ds ;
	//Connection connection;
        
        public ScheduleDAOImpl()
        {
             try {
            ds = (DataSource) InitialContext.doLookup("jdbc/PrmsDatasource");
           
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Cannot connect to the DS");
            }
        }

        public ScheduleDAOImpl(DataSource ds) {
           this.ds = ds;
        }
        
	/* (non-Javadoc)
	 * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.RadioProgramDAO#createValueObject()
	 */
	@Override
	public ProgramSlot createValueObject() {
		return new ProgramSlot();
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.RadioProgramDAO#getObject(java.lang.String)
	 */
		/* (non-Javadoc)
	 * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.RadioProgramDAO#getObject(java.lang.String)
	 */
        @Override
	public List<ProgramSlot> getListByDate(Date dateOfProgram) throws NotFoundException,
			SQLException {
                List<ProgramSlot> searchResults = null;
		String sql = "SELECT * FROM APP.\"program-slot\" WHERE (\"dateOfProgram\" = ? ) ";
                try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
                    stmt.setDate(1, new java.sql.Date(dateOfProgram.getTime()));
                    searchResults = listQuery(stmt);
                    System.out.println("record size"+searchResults.size());
                }
		return searchResults;
	}
        
        @Override
        public ProgramSlot findObject(int id) throws NotFoundException, SQLException {

            ProgramSlot valueObject = createValueObject();
            valueObject.setId(id);
            loadById(valueObject);
            return valueObject;
        }

	/* (non-Javadoc)
	 * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.RadioProgramDAO#load(sg.edu.nus.iss.phoenix.radioprogram.entity.RadioProgram)
	 */
	@Override
	public void loadById(ProgramSlot valueObject) throws NotFoundException,
			SQLException {
         String sql = "SELECT * FROM APP.\"program-slot\" WHERE (\"id\" = ? ) ";
            try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
                stmt.setInt(1, valueObject.getId());
                singleQuery(stmt, valueObject);
            } 
	}
        
        /* (non-Javadoc)
	 * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.RadioProgramDAO#load(sg.edu.nus.iss.phoenix.radioprogram.entity.RadioProgram)
	 */
	@Override
	public void load(ProgramSlot valueObject) throws NotFoundException,
			SQLException {
         String sql = "SELECT * FROM APP.\"program-slot\" WHERE (\"dateOfProgram\" = ? ) ";
            try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
                stmt.setDate(2, new java.sql.Date(valueObject.getDateOfProgram().getTime()));
                singleQuery(stmt, valueObject);
            } 
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.RadioProgramDAO#loadAll()
	 */
	@Override
	public List<ProgramSlot> loadAll() throws SQLException {
		List<ProgramSlot> searchResults = null;
		//To do
		String sql = "SELECT * FROM APP.\"program-slot\" ORDER BY \"dateOfProgram\" DESC ";
                try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
                    searchResults = listQuery(stmt);
                    System.out.println("record size"+searchResults.size());
                }
		return searchResults;
	}
            
	/* (non-Javadoc)
	 * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.RadioProgramDAO#create(sg.edu.nus.iss.phoenix.radioprogram.entity.RadioProgram)
	 */
	@Override
	public synchronized void create(ProgramSlot valueObject)
			throws SQLException {
                        System.out.println("duration=="+valueObject.getDuration());
                        System.out.println("date=="+valueObject.getDateOfProgram());
                        System.out.println("StartTime=="+valueObject.getStartTime());
		String sql = "INSERT INTO APP.\"program-slot\" (\"duration\", \"dateOfProgram\",\"startTime\", \"program-name\", \"presenter-id\", \"producer-id\") VALUES (?,?,?,?,?,?) ";
		 try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
			stmt.setString(1,SDFUtils.SCHEDULE_SDF_TIME.format(valueObject.getDuration()));
                        stmt.setDate(2, new java.sql.Date(valueObject.getDateOfProgram().getTime()));
                        stmt.setString(3, SDFUtils.SCHEDULE_SDF_TIME.format(valueObject.getStartTime()));
                        stmt.setString(4, valueObject.getRadioProgram().getName());
                        stmt.setString(5, valueObject.getPresenter().getId());
                        stmt.setString(6, valueObject.getProducer().getId());
			int rowcount = databaseUpdate(stmt);
			if (rowcount != 1) {
				// System.out.println("PrimaryKey Error when updating DB!");
				throw new SQLException("PrimaryKey Error when updating DB!");
			}

		}

	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.RadioProgramDAO#save(sg.edu.nus.iss.phoenix.radioprogram.entity.RadioProgram)
	 */
	@Override
	public void save(ProgramSlot valueObject) throws NotFoundException,
			SQLException {
		
		String sql = "UPDATE APP.\"program-slot\" SET \"duration\" = ?, \"dateOfProgram\" = ?, \"startTime\" = ?, \"program-name\" = ?, \"presenter-id\" = ?, \"producer-id\" = ? WHERE (\"id\" = ? ) ";
		 try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
			stmt.setString(1,SDFUtils.SCHEDULE_SDF_TIME.format(valueObject.getDuration()));
                        stmt.setDate(2, new java.sql.Date(valueObject.getDateOfProgram().getTime()));
                        stmt.setString(3, SDFUtils.SCHEDULE_SDF_TIME.format(valueObject.getStartTime()));
                        stmt.setString(4, valueObject.getRadioProgram().getName());
                        stmt.setString(5, valueObject.getPresenter().getId());
                        stmt.setString(6, valueObject.getProducer().getId());
                        stmt.setInt(7,valueObject.getId());
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

	/* (non-Javadoc)
	 * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.RadioProgramDAO#delete(sg.edu.nus.iss.phoenix.radioprogram.entity.RadioProgram)
         * delte selected program
          */
	@Override
	public void delete(ProgramSlot valueObject) throws NotFoundException,
			SQLException {
          String sql = "delete FROM APP.\"program-slot\" WHERE (\"id\" = ? ) ";
           /* try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
                stmt.setInt(1, valueObject.getId());
                 int rowcount = databaseUpdate(stmt);
                //singleQuery(stmt, valueObject);
            } */
            try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, valueObject.getId());

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
        } catch (SQLException se) {
            se.printStackTrace();
        }
        }

	/* (non-Javadoc)
	 * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.RadioProgramDAO#deleteAll(java.sql.Connection)
	 */
	@Override
	public void deleteAll(Connection conn) throws SQLException {
                //to do
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.RadioProgramDAO#countAll()
	 */
	@Override
	public int countAll() throws SQLException {
		int allRows = 0;
                //to do
		return allRows;
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.RadioProgramDAO#searchMatching(sg.edu.nus.iss.phoenix.radioprogram.entity.RadioProgram)
	 */
	@Override
	public List<ProgramSlot> searchMatching(PSSearchObject valueObject) throws SQLException {

                List<ProgramSlot> searchResults = new ArrayList<ProgramSlot>();
		//openConnection();
		boolean first = true;
		StringBuffer sql = new StringBuffer(
				"SELECT * FROM APP.\"program-slot\" WHERE 1=1 ");

		if (valueObject.getStartTime()!= null && !"".equals(valueObject.getStartTime().trim())) {
			if (first) {
				first = false;
			}
			sql.append("AND \"startTime\" = '").append(valueObject.getStartTime())
					.append("' ");
		}

                
		if (valueObject.getDateOfProgram()!= null && !"".equals(valueObject.getDateOfProgram().trim())) {
			if (first) {
				first = false;
			}
			sql.append("AND \"dateOfProgram\" = '").append(valueObject.getDateOfProgram())
					.append("' ");
		}

		if (valueObject.getRadioProgramName()!= null) {
			if (first) {
				first = false;
			}
			sql.append("AND \"program-name\" LIKE '%")
					.append(valueObject.getRadioProgramName()).append("%' ");
		}

		sql.append("ORDER BY \"dateOfProgram\" DESC ");

		// Prevent accidential full table results.
		// Use loadAll if all rows must be returned.
		if (first)
			searchResults = new ArrayList<ProgramSlot>();
                else{
                     try( Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString());) {
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
	 * @param conn
	 *            This method requires working database connection.
	 * @param stmt
	 *            This parameter contains the SQL statement to be excuted.
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
	 * @param conn
	 *            This method requires working database connection.
	 * @param stmt
	 *            This parameter contains the SQL statement to be excuted.
	 * @param valueObject
	 *            Class-instance where resulting data will be stored.
	 */
	protected void singleQuery(PreparedStatement stmt, ProgramSlot valueObject)
			throws NotFoundException, SQLException {

		ResultSet result = null;

                try {
                    result = stmt.executeQuery();

                    if (result.next()) {
                                valueObject.setId(result.getInt("id"));
				valueObject.setDuration(result.getString("duration"));
				valueObject.setDateOfProgram(result.getDate("dateOfProgram"));
				valueObject.setStartTime(result.getString("startTime"));
                                RadioProgram rp = new RadioProgram(result.getString("program-name"));
                                valueObject.setRadioProgram(rp);
                                Presenter presenter = new Presenter(result.getString("presenter-id"));
                                valueObject.setPresenter(presenter);
                                Producer producer = new Producer(result.getString("producer-id"));
                                valueObject.setProducer(producer);

                    } else {
                        throw new NotFoundException("User Object Not Found!");
                    }
                } finally {
                    if (result != null) {
                        result.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                }
	}
        

	/**
	 * databaseQuery-method. This method is a helper method for internal use. It
	 * will execute all database queries that will return multiple rows. The
	 * resultset will be converted to the List of valueObjects. If no rows were
	 * found, an empty List will be returned.
	 * 
	 * @param conn
	 *            This method requires working database connection.
	 * @param stmt
	 *            This parameter contains the SQL statement to be excuted.
	 */
	protected List<ProgramSlot> listQuery(PreparedStatement stmt) throws SQLException {

		ArrayList<ProgramSlot> searchResults = new ArrayList<ProgramSlot>();
		//To do
                ResultSet result = null;
		//openConnection();
		try {
			result = stmt.executeQuery();

			while (result.next()) {
				ProgramSlot temp = createValueObject();
                                temp.setId(result.getInt("id"));
				temp.setDuration(result.getString("duration"));
				temp.setDateOfProgram(result.getDate("dateOfProgram"));
				temp.setStartTime(result.getString("startTime"));
                                RadioProgram rp = new RadioProgram(result.getString("program-name"));
                                temp.setRadioProgram(rp);
                                Presenter presenter = new Presenter(result.getString("presenter-id"));
                                temp.setPresenter(presenter);
                                Producer producer = new Producer(result.getString("producer-id"));
                                temp.setProducer(producer);
				searchResults.add(temp);
			}

		} finally {
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
			//closeConnection();
		}

		return (List<ProgramSlot>) searchResults;
	}
        
        /*
	private void openConnection() {
		try {
			Class.forName(DBConstants.COM_MYSQL_JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			this.connection = DriverManager.getConnection(DBConstants.dbUrl,
					DBConstants.dbUserName, DBConstants.dbPassword);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void closeConnection() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
