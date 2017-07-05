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

    c.execute("""delete from fact_has_symptom""")
    c.execute("""delete from fact""")
    c.execute("""delete from drug_has_ingredient""")

    c.execute("""delete from drug""")
    c.execute("""delete from ingredient""")

    c.execute("""delete from symptom""")
    c.execute("""delete from time""")
    c.execute("""delete from patient""")

    #reset auto_increment
    c.execute("""alter table fact auto_increment=1""")
    c.execute("""alter table drug auto_increment=1""")
    c.execute("""alter table ingredient auto_increment=1""")
    c.execute("""alter table symptom auto_increment=1""")
    c.execute("""alter table time auto_increment=1""")
    c.execute("""alter table patient auto_increment=1""")

    c.close()

#returns a query for the fact table where each null value is searched with IS NULL
def create_null_fact_query(time_id,drug_id,sex,weight_group,age_group):
    res="SELECT Fact_id FROM fact WHERE "

    if time_id is None:
        res+="Time_id is NULL AND "
    else:
        res+="Time_id="+str(time_id)+" AND "

    if drug_id is None:
        res+="Drug_id is NULL AND "
    else:
        res+="Drug_id="+str(drug_id)+" AND "

    if sex is None:
        res+="Sex is NULL AND "
    else:
        res+="Sex="+str(sex)+" AND "

    if weight_group is None:
        res+="Weight_group is NULL AND "
    else:
        res+="Weight_group="+str(weight_group)+" AND "

    if age_group is None:
        res+="Age_group is NULL "
    else:
        res+="Age_group="+str(age_group)

    return res



#insert into database table, if record doesn't exist yet.returns true on success.
#it does NOT commit at the end, as we may wish to rollback after successive inserts.
#values is a tuple with each column value.
def insert(table,values):
    try:
        c=conn.cursor()
        if table=='Ingredient':
            c.execute("""INSERT INTO ingredient(Name) VALUES (%s)""",values)
        elif table=='Patient':
            c.execute("""INSERT INTO patient(Age_group,Weight_group,Sex) VALUES (%s,%s,%s)""",values)
        elif table=='Time':
            c.execute("""INSERT INTO time(Year,Month) VALUES (%s,%s)""",values)
        elif table=='Symptom':
            c.execute("""INSERT INTO symptom(Name,Is_severe) VALUES (%s,%s)""",values)

        elif table=='Drug':
            c.execute("""INSERT INTO drug(Name,Manufacturer) VALUES (%s,%s)""",values)
        elif table=='Drug_has_Ingredient':
            #get drug id
            c.execute("""SELECT Drug_id FROM drug WHERE Name=%s AND Manufacturer=%s""",(values[0],values[1]))
            drug_id=c.fetchone()[0]

            #get ingredient id
            c.execute("""SELECT Ingredient_id FROM ingredient WHERE Name=%s""",(values[2],))
            ing_id=c.fetchone()[0]

            c.execute("""INSERT INTO drug_has_ingredient(Drug_id,Ingredient_id) VALUES (%s,%s)""",(drug_id,ing_id))
        elif table=='Fact':
            drug=values['drug']
            #get drug id
            c.execute("""SELECT Drug_id FROM drug WHERE Name=%s AND Manufacturer=%s""",(drug['name'],drug['manufacturer']))

            try:
                drug_id=c.fetchone()[0]
            except:
                drug_id=None


            date=values['date']
            #get time id
            c.execute("""SELECT Time_id FROM time WHERE Year=%s AND Month=%s""",(date.year,date.month))
            time_id=c.fetchone()[0]

            #patient info
            sex=values['patient']['sex']
            age_group=values['patient']['age_group']
            weight_group=values['patient']['weight_group']

            #check if fact already exists
            c.execute("""SELECT Fact_id FROM fact WHERE Time_id=%s AND Drug_id=%s AND Sex=%s
                AND Weight_group=%s AND Age_group=%s
            """,(time_id,drug_id,sex,weight_group,age_group))
            fact_id=c.fetchone()

            death=values['death']
            dosage=values['dosage']

            #fact doesn't exist
            if fact_id is None:
                if death:
                    c.execute("""INSERT INTO fact(Time_id,Drug_id,Sex,Weight_group,Age_group,Total_dosage,Num_events,Num_deaths)
                        VALUES(%s,%s,%s,%s,%s,%s,1,1)
                    """,(time_id,drug_id,sex,weight_group,age_group,dosage))
                else:
                    c.execute("""INSERT INTO fact(Time_id,Drug_id,Sex,Weight_group,Age_group,Total_dosage,Num_events,Num_deaths)
                        VALUES(%s,%s,%s,%s,%s,%s,1,0)
                    """,(time_id,drug_id,sex,weight_group,age_group,dosage))

                #get fact id for later use

                q=create_null_fact_query(time_id,drug_id,sex,weight_group,age_group)

                c.execute(q)
                fact_id=c.fetchone()[0]

            else:
                #fact already exists.update it
                fact_id=fact_id[0]
                c.execute("""UPDATE fact SET Num_events=Num_events+1 WHERE Fact_id=%s""",(fact_id,))
                c.execute("""UPDATE fact SET Total_dosage=Total_dosage+%s WHERE Fact_id=%s""",(dosage,fact_id))

                if death:
                    c.execute("""UPDATE fact SET Num_deaths=Num_deaths+1 WHERE Fact_id=%s""",(fact_id,))

            #insert symptoms in relational table between symptom and fact
            for symptom in values['symptoms']:
                c.execute("""SELECT Symptom_id FROM symptom WHERE Name=%s AND Is_severe=%s""",(symptom[0],symptom[1]))
                symp_id=c.fetchone()[0]

                #create row if it doesn't exist
                try:
                    c.execute("""INSERT INTO fact_has_symptom(Fact_id,Symptom_id,Num_events) VALUES(%s,%s,0)""",(fact_id,symp_id))
                except:
                    pass

                c.execute("""UPDATE fact_has_symptom SET Num_events=Num_events+1 WHERE Fact_id=%s AND Symptom_id=%s""",(fact_id,symp_id))


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

def bd_to_csv():
    tables=['drug','fact','drug_has_ingredient','ingredient','drug_has_ingredient','patient','symptom','time','fact_has_symptom']
    c=conn.cursor()
    DELIM='\t'
    for table in tables:
        c.execute("SHOW columns FROM "+table)
        cols=[column[0] for column in c.fetchall()]
        print(cols)

        c.execute("""SELECT * FROM """+table)
        rows=c.fetchall()
        #print(rows)

        with open(table+'.csv','w') as f:
            s=''
            for col in cols[:len(cols)-1]:
                print(col)
                s+=str(col)+DELIM
            s+=str(cols[-1])+'\n'
            f.write(s)

            for row in rows:
                #print(row)
                s=''
                for col in row[:len(row)-1]:
                    if col is None:
                        s+='?'+DELIM
                    else:
                        s+=str(col).replace('\'','')+DELIM
                if row[-1] is None:
                    s+='?\n'
                else:
                    s+=str(row[-1]).replace('\'','')+'\n'
                f.write(s)
