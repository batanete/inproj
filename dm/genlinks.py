from os import system

q=1
y=2013


while y<2016:
    n=int(input())
    
    x=1
    
    if y==2013 and q==3:
        x=7
    
    for i in range(x,n+1):
        link="https://download.open.fda.gov/drug/event/"
        link+=str(y)+'q'+str(q)+'/'
        
        if i<10:
            filename="drug-event-000%d"
        else:
            filename="drug-event-00%d"
        if n < 10:
            filename+="-of-000%d.json.zip"
        else:
            filename+="-of-00%d.json.zip"
        filename=filename%(i,n)
        system("wget '"+link+filename+"'")
        system("unzip "+filename+" -d json")
        
        system("python3 cenas2.py")
        
        system("rm "+filename)
        system("rm json/*")
    
    q+=1
    if q>4:
        q=1
        y+=1
