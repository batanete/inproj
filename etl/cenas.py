from time import time
from urllib import request
import os
import json
import bd
import MySQLdb
from datetime import datetime

"""
dirs=[
    '/media/bata/pocketSDD/json/2015q1/',
    '/media/bata/pocketSDD/json/2015q2/',
    '/media/bata/pocketSDD/json/2015q3/',
    '/media/bata/pocketSDD/json/2015q4/',
    '/media/bata/pocketSDD/json/2014q1/',
    '/media/bata/pocketSDD/json/2014q2/',
    '/media/bata/pocketSDD/json/2014q3/',
    '/media/bata/pocketSDD/json/2014q4/',
    '/media/bata/pocketSDD/json/2013q1/',
    '/media/bata/pocketSDD/json/2013q2/',
    '/media/bata/pocketSDD/json/2013q3/'
    '/media/bata/pocketSDD/json/2013q4/'
]
"""
dirs=[
    'json/test/'
    
]

#return's patient weight group
#1=0-34kg,2=35-49,3=50-69,4=70-89,5=90+
def get_weight_group(weight):
    if weight<35:
        return 1
    elif weight<50:
        return 2
    elif weight<70:
        return 3
    elif weight<90:
        return 4
    else:
        return 5

def get_age_group(units,code):
    """
    returns the patient's age group.
    we consider 4 different age groups:child(0-12),teenager(13-17),adult(18-65),senior(66+)
    codes:
    800 = Decade
    801 = Year
    802 = Month
    803 = Week
    804 = Day
    805 = Hour
    """
    if code==800:
        age=units*10
    elif code==801:
        age=units
    elif code==802:
        age=units//12
    elif code==803:
        age=units//52
    elif code==804:
        age=units//365
    elif code==805:
        age=units//8760
    else:
        #print("ERROR! INVALID AGE FORMAT")
        return -1
    
    if age<=12:
        return 1
    elif age<=17:
        return 2
    elif age<=64:
        return 3
    else:
      
        return 4

def get_dosage(quantity,unit):
    """
    returns the dosage the patient took in mg.
    codes:1=kg, 2=g, 3=mg, 4=ug
    
    """
    if unit==1:
        return quantity*1000000.0
    elif unit==2:
        return quantity*1000.0
    elif unit==3:
        return float(quantity)
    elif unit==4:
        return float(quantity)/1000.0
    else:
        #print("ERROR! INVALID DOSAGE FORMAT:"+str(unit))
        return -1
        

count=0
def update_db(eventdict):
    global count
    count+=1
    
    try:
        #date
        
        bd.insert('Time',(eventdict['date'].year,eventdict['date'].month))
        
        #symptoms
        for symptom in eventdict['symptoms']:
            bd.insert('Symptom',(symptom[0],symptom[1]))
        
        #patient
        patient=eventdict['patient']
        bd.insert('Patient',(patient['age_group'],patient['weight_group'],patient['sex']))
        
        #drug
        drug=eventdict['drug']
        manufacturer=drug['manufacturer']
        drugname=drug['name']
        bd.insert('Drug',(drugname,manufacturer))
        
        #ingredients
        for ingredient in drug['ingredients']:
            bd.insert('Ingredient',(ingredient,))
            #drug_has_ingredient(relationship table)
            bd.insert('Drug_has_Ingredient',(drugname,manufacturer,ingredient))
        
        #fact
        values={
            'dosage':eventdict['dosage'],
            'death':eventdict['death'],
            'drug':drug,
            'patient':patient,    
            'date':eventdict['date'],
            'symptoms':eventdict['symptoms']
        }
        bd.insert('Fact',values)
        
        #TODOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO================0
    
    #rollback database when event can't be entered completely
    except Exception as e:
        print(e)
        bd.conn.rollback()
        raise
    
    


#extracts data from the given file
def extract(filename):
    
    with open(filename) as data_file:    
        data = json.load(data_file)
        
        for event in data['results']:
            
            
            patient=event['patient']
            drug=patient['drug'][0]
            
            try:
                #event resulted in death of patient?
                if "seriousnessdeath" in event.keys():
                    death=True
                else:
                    death=False
                
                #event's date
                datestr=event['receiptdate']
                date=datetime.strptime(datestr,'%Y%m%d')
                
                #ingredients list
                ings=drug['openfda']['substance_name']
                
                #dosage the patient took before the event occurred.
                #units code: 1=kg, 2=g, 3=mg, 4=ug
                #we multiply the number of doses taken with the ammount of medicine per dose.
                dosage_numb=int(drug['drugstructuredosagenumb'])
                dosage_unit=int(drug['drugstructuredosageunit'])
                
                dosage=get_dosage(dosage_numb,dosage_unit)
                
                if dosage==-1:
                    continue
                
                total_dosage=dosage*int(drug['drugseparatedosagenumb'])
                
                #drug's name
                drugname=drug['openfda']['brand_name'][0].lower()
                
                #drug's manufacturer
                manufacturer=drug['openfda']['manufacturer_name'][0].lower()
                
                
                #patient's age, as well as the format used for it
                age_units=int(patient['patientonsetage'])
                age_code=int(patient['patientonsetageunit'])
                
                age_group=get_age_group(age_units,age_code)
                
                if age_group==-1:
                    continue
                
                #patient's sex. 1=male,2=female
                sex=int(patient['patientsex'])
                if sex==0:
                    continue
                    
                #patient's weight group
                weight=int(patient['patientweight'])
                weight_group=get_weight_group(weight)
                
                #skip events where sex of the patient is unknown
                if age_group==-1:
                    continue
                    
                    
                #symptoms that ocorred.
                symptoms=[]
                #check for severity(4,5=severe)
                for symptom in patient['reaction']:
                    
                    if 'reactionoutcome' in symptom.keys():
                        outcome=int(symptom['reactionoutcome'])
                        if outcome >= 4 and outcome < 6:
                            symptoms.append([symptom['reactionmeddrapt'],True])
                        else:
                            symptoms.append([symptom['reactionmeddrapt'],False])
                    else:
                        symptoms.append([symptom['reactionmeddrapt'],False])
                
                ingredients=[]
                for ing in ings:
                    if ing.lower() not in ingredients:
                        ingredients.append(ing.lower())
                
                #dictionary with all the relevant info for this event
                eventdict={
                    'death':death,
                    'date':date,
                    'dosage': total_dosage,
                    'patient':{
                        'age_group':age_group,
                        'sex':sex,
                        'weight_group':weight_group
                    },
                    'drug':{
                        'name': drugname,
                        'manufacturer': manufacturer,
                        'ingredients': ingredients
                    },
                    

                    'symptoms':symptoms

                }
                
                update_db(eventdict)
                
            
            except (KeyError,ValueError,TypeError):
                #print(e)
                continue
            
            
            


if __name__=='__main__':
    start=time()
    last=start
    bd.connect()
    #bd.reset()
    for d in dirs:
        for name in os.listdir(d):
            extract(d+name)
            curr=time()
            #print('finished processing file '+name+' on dir '+d+';time:'+str(curr-last))
            last=curr
        #print('finished processing dir '+d)
    print('total events processed:'+str(count)+'.total time:'+str(time()-start))
    #commit after inserting all events successfully.
    bd.conn.commit()
