select id_causale,data_inizio,data_fine,descrizione from
prenotazioni join causale on causale.id = prenotazioni.id_causale
where data_inizio like "2020-02-10%";
/*Perché un'auto non è disponibile in questa data?*/

select id_causale,data_inizio,data_fine,descrizione from
prenotazioni join causale on causale.id = prenotazioni.id_causale
where id_causale = 1 or id_causale = 2;
/*Quali auto sono in manutenzione?*/

select id_causale,id_auto,data_inizio,data_fine,descrizione from
prenotazioni join causale on causale.id = prenotazioni.id_causale
where id_causale = 1 and data_inizio between now() and date_add(now(), interval 15 day)
or id_causale = 2 and data_inizio between now() and date_add(now(), interval 15 day);
/*Manutenzioni nei prossimi X giorni */

select marca,modello,targa,concat(floor(hour(timediff(data_fine,data_inizio))/24), ' giorni ',
mod(hour(timediff(data_fine,data_inizio)),24), ' ore ', minute (timediff(data_fine,data_inizio)), ' minuti') as durata_manutenzione
from auto join prenotazioni on auto.id = prenotazioni.id_auto
where (id_causale = 1 and id_auto = 1) or (id_causale = 2 and id_auto = 1);
/*quanto dura una determinata manutenzione*/

select nome,cognome,tipologia from
dipendenti join possesso_patenti on dipendenti.id = possesso_patenti.id_dipendente
join patenti on patenti.id = possesso_patenti.id_patente
where cognome like "v%"
order by cognome;
/*Patente del dipendente X*/

select nome,cognome from
dipendenti join sede_dip on dipendenti.id = sede_dip.id_dipendente
join sede on sede.id = sede_dip.id_sede
where sede.id = 1;
/*quali dipendenti lavorano in una determinata sede*/

select nome,cognome,regione,citta from
dipendenti join sede_dip on dipendenti.id = sede_dip.id_dipendente
join sede on sede.id = sede_dip.id_sede
where cognome like 'b%';
/*in quale sede lavora un certo dipendente*/

select marca,modello,targa,data_assicurazione
from auto
having date_add(data_assicurazione, interval 1 year) <= date_add(curdate(), interval 10 month);
/*quali assicurazioni sono in scadenza in tot tempo*/

select marca,modello,targa
from auto
where targa like 'C%';
/*a che auto corrisponde una targa*/

select marca,modello,targa,data_inizio,data_fine from
auto join prenotazioni on auto.id = prenotazioni.id_auto
where data_inizio like '2019-07-03%';
/*filtri vari su prenotazioni*/

select nome,cognome,mansione from
dipendenti join livelli on livelli.id = dipendenti.id_livello
where livelli.id = 1;
/*dipendenti che corrispondono ad un livello*/

select targa, citta, p1.data_parch, p1.id_auto, modello, marca from parcheggio as p1 
left outer join parcheggio as p2 on p1.id_auto=p2.id_auto and p1.data_parch < p2.data_parch 
join auto on p1.id_auto = auto.id 
join sede on sede.id = p1.id_sede 
where p2.id is null;
/*auto in una determinata sede*/

select * from prenotazioni where data_inizio like "2020-02-20%";
/*lista prenotazioni in una determinata data*/

select marca,modello,targa,data_fine as disponibile_da
from auto join prenotazioni on auto.id = prenotazioni.id_auto
where data_fine > curdate() and id_auto = 1;
/*quando sarà disponibile un'auto che ora non lo è?*/

select marca,modello,targa,sum(spesa) as spesa_totale
from auto join spesa_manutenzione on auto.id = spesa_manutenzione.id_auto
where id_auto = 1 and data_spesa between "2019-01-01" and "2020-01-01";
/*quanto ho speso in manutenzione per un'auto in un determinato periodo?*/

select marca,modello,targa 
from auto join patenti on auto.id_patente = patenti.id
join possesso_patenti on patenti.id = possesso_patenti.id_patente
join dipendenti on dipendenti.id = possesso_patenti.id_dipendente
where auto.id_patente = possesso_patenti.id_patente
and (kW/((tara+75)/1000)) <= 55
and kW <= 70
and dipendenti.id=1;
/*auto che posso guidare con la mia patente SAPENDO che sono neoP*/

select marca,modello,targa 
from auto join patenti on auto.id_patente = patenti.id
join possesso_patenti on patenti.id = possesso_patenti.id_patente
join dipendenti on dipendenti.id = possesso_patenti.id_dipendente
where auto.id_patente = possesso_patenti.id_patente
and dipendenti.id=1;
/*auto che posso guidare con la mia patente se non sono neoP*/

select marca,modello,targa,regione,citta
from auto join parcheggio on auto.id = parcheggio.id_auto
join sede on parcheggio.id_sede = sede.id
where parcheggio.id_auto = 1 and data_parch = "2020-01-01";
/*in quale sede è un'auto in una determinata data*/

select marca,modello,targa,descrizione,data_inizio,data_fine
from auto join prenotazioni on prenotazioni.id_auto = auto.id
join causale on causale.id = prenotazioni.id_causale
where data_fine between "2019-01-01" and "2020-01-01"
and id_auto = 6;
/*com'è stata usata un'auto in un determinato periodo*/



