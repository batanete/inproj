import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseOperator {

	public DatabaseOperator() {
		
	}
	
	
	
	public ArrayList <Integer> runQuery1(String chosenSymptom, int totalMonths, int yearI, int yearF, int indexChosenMonthIni, int indexChosenMonthFin, int chosenSex, int chosenWeight, int chosenAge, String chosenDrug) {
   	 	ResultSet rs = null;
        Connection connection = null;
        Statement statement = null; 
        String stringResults = "";
        int i,year, limitI=indexChosenMonthIni+1, limitF=12;
        ArrayList <Integer> listResult = new ArrayList <Integer>();
        
        for (year=yearI;year<=yearF;year++) {
        	if (year==yearF) 
        		limitF=indexChosenMonthFin+1;
        	
        	for (i=limitI;i<=limitF;i++) {
        		
        		int month = i%12;
	        	String original_query = "select sum(fact_has_symptom.Num_events) as result from fact_has_symptom where Symptom_id in (select Symptom_id  from symptom where Name like '%"+chosenSymptom+"%') and Fact_id in (select Fact_id from fact where fact.Time_id in (select Time_id from time where Month = "+(month+1)+" and Year = "+year+") ";
		        String query = addFilters(original_query, chosenAge, chosenWeight, chosenSex, chosenDrug);
	        	try {
		            connection = JDBCMySQLConnection.getConnection();
		            statement = connection.createStatement();
		            rs = statement.executeQuery(query);
		            if (rs.next()) {
		                
		            	listResult.add(rs.getInt("result"));
		                
		            }
		            else {
		            	listResult.add(0);
			        	
		            }
		            
		        } catch (SQLException e) {
		            e.printStackTrace();
		        } finally {
		            if (connection != null) {
		                try {
		                    connection.close();
		                } catch (SQLException e) {
		                    e.printStackTrace();
		                }
		            }
		        }
		        
		        	
	        }
        	limitI=1; // come�a sempre em Janeiro
        }
        
        return listResult;
	}
	
	public ArrayList <Integer> runQuery2(String chosenSymptom, ArrayList <String> chosenDrugs, ArrayList <Integer> chosenAges) {
   	 	ResultSet rs = null;
        Connection connection = null;
        Statement statement = null; 
        String stringResults = "";
       
        int iDrug, jAge, i;
        ArrayList <Integer> listResult = new ArrayList <Integer>();
        
        for (jAge=0; jAge<chosenAges.size();jAge++) {
        	for (iDrug=0;iDrug<chosenDrugs.size();iDrug++) {
        	
	        	//String query = "select sum(num_events) as result from fact_has_symptom where Symptom_id in (select Symptom_id  from symptom where Name like '%"+chosenSymptom+"%') and Fact_id in (select Fact_id from fact where Drug_id = (select Drug_id from drug where Name like '"+chosenDrugs.get(iDrug)+"') and Age_group = "+chosenAges.get(jAge)+" )";
	        	
	        	String query="select sum(num_events) as result from fact_has_symptom where Fact_id in (select Fact_id from fact where Drug_id in (select Drug_id from drug where Name like '%"+chosenDrugs.get(iDrug)+"%') and Age_group = "+chosenAges.get(jAge)+" and Fact_id in (select Fact_id from fact_has_symptom where Symptom_id in (select Symptom_id from symptom where symptom.name like '%"+chosenSymptom+"%')))";
	        	
	        	
	        	
	        /*	String query = "select sum(num_events) as result from fact_has_symptom where Symptom_id in (select Symptom_id  from symptom where Name like '%"+chosenSymptom+"%') and Fact_id in (select Fact_id from fact where Drug_id = (select Drug_id from drug where Name like '"+chosenDrugs.get(iDrug)+"')" ;
	        	if(chosenAges.get(jAge)!=0)
	        		query+="and Age_group = "+chosenAges.get(jAge);
	        	
	        	query+=" )";*/
	        	
	        	try {
		            connection = JDBCMySQLConnection.getConnection();
		            statement = connection.createStatement();
		            rs = statement.executeQuery(query);
		            if (rs.next()) {
		                
		            	listResult.add(rs.getInt("result"));
		                
		            }
		            else {
		            	listResult.add(0);
			        	
		            }
		            
		        } catch (SQLException e) {
		            e.printStackTrace();
		        } finally {
		            if (connection != null) {
		                try {
		                    connection.close();
		                } catch (SQLException e) {
		                    e.printStackTrace();
		                }
		            }
		        }
        	}
        }
        
	    
        return listResult;
	}

	public ArrayList <String> runQuery3(int chosenSex, int chosenWeight, int chosenAge) {
   	 	ResultSet rs = null;
        Connection connection = null;
        Statement statement = null; 
        String stringResults = "";
        ArrayList <String> listResult = new ArrayList <String>();
        
        
	        	String query = "select ingredient.Name as IngredientName from ingredient where Ingredient_id in (select Ingredient_id from drug_has_ingredient where Drug_id in (select Drug_id from drug where Drug_id in (select Drug_id from fact where Fact_id in (select Fact_id from fact_has_symptom where Symptom_id in (select Symptom_id from symptom where Is_severe = 1))";
	        	
	        	//add filters
	        	if(chosenSex!=0)
	        		query+=" AND Sex="+chosenSex;
	        	
	        	if(chosenWeight!=0)
	        		query+=" AND Weight_group="+chosenWeight;
	        	
	        	if(chosenAge!=0)
	        		query+=" AND Age_group="+chosenAge;
	        	
	        	
	        	query+="))) order by IngredientName";
	        	
	        	try {
		            connection = JDBCMySQLConnection.getConnection();
		            statement = connection.createStatement();
		            rs = statement.executeQuery(query);
		            if (rs.next()) {
		            	listResult.add(rs.getString("IngredientName"));
			            while (rs.next()) {
			                
			            	listResult.add(rs.getString("IngredientName"));
			            	
			            }
		            }
		            else
		            	listResult.add("No results found.");
		            
		        } catch (SQLException e) {
		            e.printStackTrace();
		        } finally {
		            if (connection != null) {
		                try {
		                    connection.close();
		                } catch (SQLException e) {
		                    e.printStackTrace();
		                }
		            }
		        }
		

        return listResult;
	}
	
	
	// returns the new query
	public String addFilters(String query, int ageGroup, int weightGroup, int sexValue, String chosenDrug) {
		// severe
		// drug
		// age, weight, sex
		if (sexValue!=0)
			query = query + " and fact.Sex = "+sexValue;
		if (weightGroup != 0)
			query = query + " and fact.Weight_Group = "+weightGroup;
		if (ageGroup != 0)
			query = query + " and fact.Age_Group = "+ageGroup;
		
		if (chosenDrug != "-") {
			query = query + " and fact.Drug_Id = (select Drug_id from drug where drug.name = '"+chosenDrug+"') ";
		}
		// and fact.Drug_id = (select Drug_id from drug where Drug.name = 'nexavar')
		query = query+")";
		
		
		return query;
	}
	
	
	
	
	
	public ArrayList<String> getSymptoms() {
   	 	ResultSet rs = null;
        Connection connection = null;
        Statement statement = null; 
        String stringResults = "";
        ArrayList<String> itemsAL = new ArrayList<String>();
         

        String query = "select distinct Name from symptom order by Name";
        try {           
            connection = JDBCMySQLConnection.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            while (rs.next()) {

                itemsAL.add(rs.getString("Name"));
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return itemsAL;
    }
	
	public ArrayList<String> getDrugs() {
   	 	ResultSet rs = null;
        Connection connection = null;
        Statement statement = null; 
        String stringResults = "";
        ArrayList<String> itemsAL = new ArrayList<String>();
         

        String query = "select distinct Name from drug order by Name";
        try {           
            connection = JDBCMySQLConnection.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            while (rs.next()) {

                itemsAL.add(rs.getString("Name"));
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return itemsAL;
    }
	
	public int getNumberMonths(int monthI, int monthF, int yearI, int yearF) {
		int totalMonths=0;
		
		if (yearI != yearF) {
			totalMonths = (12-monthI+1) + monthF + (yearF-yearI-1) * 12;
		}
		else {
			totalMonths = monthF - monthI + 1;
		}
				
		return totalMonths;
	}



	public ArrayList<Integer> runQuery1Exact(String chosenSymptom, int totalMonths, int yearI, int yearF, int indexChosenMonthIni, int indexChosenMonthFin, int chosenSex, int chosenWeight, int chosenAge, String chosenDrug) {
		ResultSet rs = null;
        Connection connection = null;
        Statement statement = null; 
        String stringResults = "";
        int i,year, limitI=indexChosenMonthIni+1, limitF=12;
        ArrayList <Integer> listResult = new ArrayList <Integer>();
        
        for (year=yearI;year<=yearF;year++) {
        	if (year==yearF) 
        		limitF=indexChosenMonthFin+1;
        	
        	for (i=limitI;i<=limitF;i++) {
        		
        		int month = i%12;
	        	String original_query = "select sum(fact_has_symptom.Num_events) as result from fact_has_symptom where Symptom_id in (select Symptom_id  from symptom where Name like '"+chosenSymptom+"') and Fact_id in (select Fact_id from fact where fact.Time_id in (select Time_id from time where Month = "+(month+1)+" and Year = "+year+") ";
		        String query = addFilters(original_query, chosenAge, chosenWeight, chosenSex, chosenDrug);
	        	try {
		            connection = JDBCMySQLConnection.getConnection();
		            statement = connection.createStatement();
		            rs = statement.executeQuery(query);
		            if (rs.next()) {
		                
		            	listResult.add(rs.getInt("result"));
		                
		            }
		            else {
		            	listResult.add(0);
			        	
		            }
		            
		        } catch (SQLException e) {
		            e.printStackTrace();
		        } finally {
		            if (connection != null) {
		                try {
		                    connection.close();
		                } catch (SQLException e) {
		                    e.printStackTrace();
		                }
		            }
		        }
		        
		        	
	        }
        	limitI=1; // come�a sempre em Janeiro
        }
        
        return listResult;
	}
	
}
