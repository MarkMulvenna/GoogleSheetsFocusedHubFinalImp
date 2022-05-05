import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseFunctions {
    private final String URL = "jdbc:mysql://91.208.99.2:1232/focusedh_db";
    private final String user = "focusedh_dbusr";
    private final String password = "dK£T%iqCKv";

    public DatabaseFunctions() {
    }

    public Connection openConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection myConnection = DriverManager.getConnection("jdbc:mysql://91.208.99.2:1232/focusedh_db", "focusedh_dbusr", "dK£T%iqCKv");
            System.out.println("Function Run: Connection Established.");
            return myConnection;
        } catch (SQLException | ClassNotFoundException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public ArrayList<Places> gatherRequiredDataFromPlacesTable() throws SQLException
    {
        ArrayList<Places> placeObjs = new ArrayList<Places>();
        String SQL = "SELECT * FROM `Places` WHERE passedToSheets = 0 ORDER BY DateAdded";
        Statement statement = this.openConnection().prepareStatement(SQL);
        ResultSet rs = statement.executeQuery(SQL);

        while (rs.next())
        {
            Places place = new Places();
            place.setPlacesID(rs.getString("PlacesID"));
            place.setBusinessName(rs.getString("BusinessName"));
            place.setVicinity(rs.getString("Vicinity"));
            place.setLocation(rs.getString("Location"));
            place.setTypes(rs.getString("Types"));
            place.setOverallRating(rs.getInt("OverallRating"));
            place.setTotalUserRating(rs.getInt("TotalUserRatings"));
            place.setDateAdded(rs.getString("DateAdded"));
            placeObjs.add(place);

        }
        return placeObjs;
    }

    public ArrayList<MailDropObj> gatheredRequiredDataFromMailDropTable() throws SQLException {
        ArrayList<MailDropObj> mailDropObjs = new ArrayList();
        String SQL = "SELECT * FROM MailDrop WHERE passedToSheets = 0";
        Statement statement = this.openConnection().prepareStatement(SQL);
        ResultSet rs = statement.executeQuery(SQL);

        while(rs.next()) {
            MailDropObj mailDropObj = new MailDropObj();
            mailDropObj.setMailDropID(rs.getString("MDid"));
            mailDropObj.setBusinessName(rs.getString("Business_Name"));
            mailDropObj.setAddress(rs.getString("Address"));
            mailDropObj.setNotes(rs.getString("Notes"));
            mailDropObjs.add(mailDropObj);
        }

        return mailDropObjs;
    }

    public void setIsPassedToGoogleSheetsTrue(String dataTable) throws SQLException {
        String SQL = "UPDATE "+ dataTable + " SET passedToSheets = 1";
        Statement statement = this.openConnection().prepareStatement(SQL);
        statement.executeUpdate(SQL);
        System.out.println("Query Executed Successfully, existing records now reflect Google Sheets parity, total rows affected = " + statement.getUpdateCount());
    }

    public static void main(String[] args) throws SQLException {
        ArrayList<Places> placeObjs = (new DatabaseFunctions()).gatherRequiredDataFromPlacesTable();

        for(int i = 0; i < placeObjs.size(); ++i) {
            System.out.println(placeObjs.get(i).getBusinessName());
        }

    }
}