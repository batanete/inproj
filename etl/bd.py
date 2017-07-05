import MySQLdb

conn = None


#create database connection
def connect():
    global conn
    conn=MySQLdb.connect(host= "localhost",
                  user="root",
                  passwd="consolaPS3",
                  db="etl")


#clear all tables from the db
def reset():
    c=conn.cursor()
    
    
    #clear tables
    
    c.execute("""delete from Fact_has_Symptom""")
    c.execute("""delete from Fact""")
    c.execute("""delete from Drug_has_Ingredient""")
    
    c.execute("""delete from Drug""")
    c.execute("""delete from Ingredient""")
    
    c.execute("""delete from Symptom""")
    c.execute("""delete from Time""")
    c.execute("""delete from Patient""")
    
    #reset auto_increment
    c.execute("""alter table Fact auto_increment=1""")
    c.execute("""alter table Drug auto_increment=1""")
    c.execute("""alter table Ingredient auto_increment=1""")
    c.execute("""alter table Symptom auto_increment=1""")
    c.execute("""alter table Time auto_increment=1""")
    c.execute("""alter table Patient auto_increment=1""")
    
    c.close()    
        


#insert into database table, if record doesn't exist yet.returns true on success.
#it does NOT commit at the end, as we may wish to rollback after successive inserts.
#values is a tuple with each column value.
def insert(table,values):
    
    try:
        c=conn.cursor()
        if table=='Ingredient':
            c.execute("""INSERT INTO Ingredient(Name) VALUES (%s)""",values)
        elif table=='Patient':
            c.execute("""INSERT INTO Patient(Age_group,Weight_group,Sex) VALUES (%s,%s,%s)""",values)
        elif table=='Time':
            c.execute("""INSERT INTO Time(Year,Month) VALUES (%s,%s)""",values)
        elif table=='Symptom':
            c.execute("""INSERT INTO Symptom(Name,Is_severe) VALUES (%s,%s)""",values)
        
        elif table=='Drug':
            c.execute("""INSERT INTO Drug(Name,Manufacturer) VALUES (%s,%s)""",values)
        elif table=='Drug_has_Ingredient':
            #get drug id
            c.execute("""SELECT Drug_id FROM Drug WHERE Name=%s AND Manufacturer=%s""",(values[0],values[1]))
            drug_id=c.fetchone()[0]
            
            #get ingredient id
            c.execute("""SELECT Ingredient_id FROM Ingredient WHERE Name=%s""",(values[2],))
            ing_id=c.fetchone()[0]
            
            c.execute("""INSERT INTO Drug_has_Ingredient(Drug_id,Ingredient_id) VALUES (%s,%s)""",(drug_id,ing_id))
        elif table=='Fact':
            drug=values['drug']
            #get drug id
            c.execute("""SELECT Drug_id FROM Drug WHERE Name=%s AND Manufacturer=%s""",(drug['name'],drug['manufacturer']))
            drug_id=c.fetchone()[0]
            
            date=values['date']
            #get time id
            c.execute("""SELECT Time_id FROM Time WHERE Year=%s AND Month=%s""",(date.year,date.month))
            time_id=c.fetchone()[0]
            
            #patient info
            sex=values['patient']['sex']
            age_group=values['patient']['age_group']
            weight_group=values['patient']['weight_group']
            
            #check if fact already exists
            c.execute("""SELECT Fact_id FROM Fact WHERE Time_id=%s AND Drug_id=%s AND Sex=%s
                AND Weight_group=%s AND Age_group=%s
            """,(time_id,drug_id,sex,weight_group,age_group))
            fact_id=c.fetchone()
            
            death=values['death']
            dosage=values['dosage']
            
            #fact doesn't exist
            if fact_id is None:
                if death:
                    c.execute("""INSERT INTO Fact(Time_id,Drug_id,Sex,Weight_group,Age_group,Total_dosage,Num_events,Num_deaths) 
                        VALUES(%s,%s,%s,%s,%s,%s,1,1)
                    """,(time_id,drug_id,sex,weight_group,age_group,dosage))
                else:
                    c.execute("""INSERT INTO Fact(Time_id,Drug_id,Sex,Weight_group,Age_group,Total_dosage,Num_events,Num_deaths) 
                        VALUES(%s,%s,%s,%s,%s,%s,1,0)
                    """,(time_id,drug_id,sex,weight_group,age_group,dosage))
                
                #get fact id for later use
                c.execute("""SELECT Fact_id FROM Fact WHERE Time_id=%s AND Drug_id=%s AND Sex=%s
                    AND Weight_group=%s AND Age_group=%s
                """,(time_id,drug_id,sex,weight_group,age_group))
                fact_id=c.fetchone()[0]
                
            else:
                #fact already exists.update it
                fact_id=fact_id[0]
                c.execute("""UPDATE Fact SET Num_events=Num_events+1 WHERE Fact_id=%s""",(fact_id,))
                c.execute("""UPDATE Fact SET Total_dosage=Total_dosage+%s WHERE Fact_id=%s""",(dosage,fact_id))
                
                if death:
                    c.execute("""UPDATE Fact SET Num_deaths=Num_deaths+1 WHERE Fact_id=%s""",(fact_id,))
            
            #insert symptoms in relational table between symptom and fact
            for symptom in values['symptoms']:
                c.execute("""SELECT Symptom_id FROM Symptom WHERE Name=%s AND Is_severe=%s""",(symptom[0],symptom[1]))
                symp_id=c.fetchone()[0]
                
                #create row if it doesn't exist
                try:
                    c.execute("""INSERT INTO Fact_has_Symptom(Fact_id,Symptom_id,Num_events) VALUES(%s,%s,0)""",(fact_id,symp_id))
                except:
                    pass
                
                c.execute("""UPDATE Fact_has_Symptom SET Num_events=Num_events+1 WHERE Fact_id=%s AND Symptom_id=%s""",(fact_id,symp_id))
                
                
        else:
            return False
        return True
    #record already exists, no problem
    except MySQLdb.IntegrityError as ie:
        c.close()
        return True
    except:
        c.close()
        raise


