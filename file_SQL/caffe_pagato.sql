select targa, citta, p1.data_parch, p1.id_auto, modello, marca from parcheggio as p1 
left outer join parcheggio as p2 on p1.id_auto=p2.id_auto and p1.data_parch < p2.data_parch 
join auto on p1.id_auto = auto.id 
join sede on sede.id = p1.id_sede 
where p2.id is null