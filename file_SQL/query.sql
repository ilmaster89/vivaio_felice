select id_causale,data_inizio,data_fine,descrizione from
prenotazioni join causale on causale.id = prenotazioni.id_causale
and data_fine >= now() or data_fine is null;
/*Quali auto sono prenotate in generale*/
/*eventuale dettaglio*/

/*select marca,modello,targa,concat(floor(hour(timediff(data_fine,data_inizio))/24), ' giorni ',
mod(hour(timediff(data_fine,data_inizio)),24), ' ore ', minute (timediff(data_fine,data_inizio)), ' minuti') as durata_manutenzione
from auto join prenotazioni on auto.id = prenotazioni.id_auto
where (id_causale = 1 and id_auto = 1) or (id_causale = 2 and id_auto = 1);
quanto dura una determinata manutenzione*/

select nome,cognome from
dipendenti join sede_dip on dipendenti.id = sede_dip.id_dipendente
join sede on sede.id = sede_dip.id_sede
where sede.id = 1;
/*quali dipendenti lavorano in una determinata sede*/
/*dettaglio dipendente e VABEH*/

/*tiriamo fuori le auto in sede e mettiamo il dettaglio auto*/

select marca,modello,targa,sum(spesa) as spesa_totale
from auto join spesa_manutenzione on auto.id = spesa_manutenzione.id_auto
where id_auto = 1 and data_spesa between "2019-01-01" and "2020-01-01";
/*quanto ho speso in manutenzione per un'auto in un determinato periodo?*/
/* magari mostrare sia le singole spese che la somma*/


