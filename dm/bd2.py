import MySQLdb

from sqlalchemy.ext.automap import automap_base
from sqlalchemy.orm import Session
from sqlalchemy import create_engine
from sqlalchemy import inspect
from sqlalchemy import or_

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




def map_database():
    global Drug,Fact,Drug_has_ingredient,Fact_has_symptom,Ingredient,Patient,Symptom,Time,session,Fact_has_symptom
    Base = automap_base()

    # engine, suppose it has two tables 'user' and 'address' set up
    engine = create_engine("mysql+mysqldb://root:consolaPS3@localhost:3306/etl")

    # reflect the tables
    Base.prepare(engine, reflect=True)

    # mapped classes are now created with names by default
    # matching that of the table name.
    Drug = Base.classes.drug
    Fact = Base.classes.fact

    Ingredient = Base.classes.ingredient
    Patient = Base.classes.patient
    Symptom = Base.classes.symptom
    Fact_has_symptom = Base.classes.fact_has_symptom
    Time = Base.classes.time

    session = Session(engine)

    # rudimentary relationships are produced
    lyrica=session.query(Drug).filter_by(Name='lyrica').first()

    print('manuf:',lyrica.Manufacturer)

    # collection-based relationships are by default named
    # "<classname>_collection"
    #print (u1.address_collection)


def bd_to_simple_csv(q):

    map_database()
    f=open('out'+str(q)+'.csv','w')



    #drugs considered severe
    if q == 1:
        f.write('Severe,Total,Issevere\n')
        MIN_PERCENT=0.05

        drugs=session.query(Drug).filter(Drug.Name!=None,Drug.Manufacturer!=None).all()
        print(len(drugs))
        #key=drug name
        events={}

        for drug in drugs:

            #value:ind 0=severe events,1=total
            if drug.Name not in events.keys():
                events[drug.Name]=[0,0]
            facts=drug.fact_collection

            for fact in facts:
                #print(fact.Drug_id,';',drug.Drug_id)
                symptoms=[fhs.symptom for fhs in fact.fact_has_symptom_collection]
                severe=False
                for symptom in symptoms:
                    if symptom.Is_severe:
                        severe=True
                        break
                if severe:
                    events[drug.Name][0]+=fact.Num_events
                events[drug.Name][1]+=fact.Num_events
            n_severe=events[drug.Name][0]
            n_total=events[drug.Name][1]
            percent=n_severe/n_total

            s=str(n_severe)+','+str(n_total)+','
            if percent>=MIN_PERCENT:
                s+='true\n'
            else:
                s+='false\n'

            f.write(s)

    elif q == 2:
        f.write('Men,Women,Total,Tendency\n')
        drugs=session.query(Drug).filter(Drug.Name!=None,Drug.Manufacturer!=None).all()
        #key=drug name
        events={}
        MIN=30
        MIN_PERCENT_DIFF=0.1

        for drug in drugs:
            #value:ind 0=severe events on men,1=on women,2=total
            if drug.Name not in events.keys():
                events[drug.Name]=[0,0,0]
            facts=drug.fact_collection

            for fact in facts:

                #print(fact.Drug_id,';',drug.Drug_id)
                symptoms=[fhs.symptom for fhs in fact.fact_has_symptom_collection]
                severe=False
                for symptom in symptoms:
                    if symptom.Is_severe:
                        severe=True
                        break
                sex=fact.Sex
                if sex==1:
                    events[drug.Name][0]+=fact.Num_events
                else:
                    events[drug.Name][1]+=fact.Num_events
                events[drug.Name][2]+=fact.Num_events

            men=events[drug.Name][0]
            women=events[drug.Name][1]
            total=events[drug.Name][2]
            diff=men/total-women/total

            if total<MIN or abs(diff)<MIN_PERCENT_DIFF:
                res='N/A'
            elif diff>0:
                res='men'
            else:
                res='women'

            s=str(events[drug.Name][0])+','+str(events[drug.Name][1])+','+str(events[drug.Name][2])+','+res+'\n'

            f.write(s)
    elif q == 3:
        f.write('Year,Month,Nauseadrugs,Frequency\n')
        times=session.query(Time).all()

        #key=(Year,Month);value:absolute frequency,frequency of suspicious drugs
        frequency={}

        for time in times:
            year=time.Year
            month=time.Month
            frequency[(year,month)]=[0,0]

        NAME='Nausea'
        queries=['supple','biotic','aspirin','ibuprofen','naproxen','chemo',]

        drugs=session.query(Drug).filter(or_(*[Drug.Name.like('%'+name+'%') for name in queries])).all()

        print(drugs)

        for drug in drugs:
            facts=drug.fact_collection

            for fact in facts:
                time=fact.time

                #print(time.Year,time.Month)

                frequency[(time.Year,time.Month)][1]+=fact.Num_events

        sym=session.query(Symptom).filter_by(Name=NAME).first()

        num=0
        for ocorr in sym.fact_has_symptom_collection:
            num=ocorr.Num_events

            fact=ocorr.fact

            year=fact.time.Year
            month=fact.time.Month

            frequency[(year,month)][0]+=num

        keys=sorted(frequency.keys(),key=lambda val: str(val[0])+str(val[1]//10)+str(val[1]%10))

        for key in keys:
            f.write(str(key[0])+','+str(key[1])+','+str(frequency[key][1])+','+str(frequency[key][0])+'\n')
    f.close()

for i in range(3,4):
    bd_to_simple_csv(i)
