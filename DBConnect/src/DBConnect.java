
//importiamo il driver che permette di accedere a MySQL, è quello che ha messo Antonio sul drive.
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class DBConnect {

	public static void main(String[] args) {

		// stabiliamo una connessione, ovvero gli diciamo a quale DB deve collegarsi
		// (Connection andrà importato)
		Connection conn = null;
		try {
			// l'URL non è altro che l'indirizzo dello specifico DB da usare. localhost:3306
			// è sempre uguale, mentre il nome del database, in questo caso "prova" e i
			// successivi USER e PASS sono ovviamente legati al PC. Nel caso della EMiT,
			// useremo ROOT e PASSWORD.
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/prova", "root", "InfySQL899");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ogni classe importata andrà circondata con un try&catch. Possiamo farlo
		// automaticamente una volta scritta la dicitura. E' importante notare che
		// inizialmente bisognerà dichiararle null, perché altrimenti risulteranno
		// variabili non dichiarate e quindi inutilizzabili nei passaggi successivi.

		// creiamo uno Statement, sempre una classe da importare, che sarebbe il
		// collegamento tra java e DB, tramite il quale eseguiremo le nostre query.
		Statement st = null;
		try {
			st = (Statement) conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// qui ho creato una variabile chiamata query a cui associo la query richiesta.
		// Non è indispensabile, la query può essere scritta anche in seguito come
		// argomento di executeQuery, ma così è molto più comodo a mio parere.
		String query = "select * from utenti";

		// il ResultSet è la connessione tra DB e java, quindi il canale attraverso il
		// quale il DB ci fornirà tutti i risultati richiesti.
		ResultSet rs = null;
		try {

			// qui eseguiamo la query effettiva, associando al RS il risultato della
			// chiamata dello Statement (ST). Invece che "query" si può scrivere
			// direttamente la richiesta, come dicevo prima.
			rs = st.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {

			// questo codice è per l'utilizzo, in questo caso la stampa, dei risultati
			// ottenuti. Il WHILE NEXT significa "cicla all'interno del set di risultati".
			while (rs.next()) {

				// questo int e il successivo ciclo for stanno a significare che il programma mi
				// deve restituire il numero di COLONNE (quindi gli attributi delle nostre
				// tabelle) e stampare, con getObject(i) il contenuto della singola colonna.
				// Nell'esempio ho fatto in modo che stampasse tutti gli attributi, ma volendo
				// si possono richiedere solo determinati attributi specificandoli al posto
				// della i. Sia chiaro, mettendo i filtri già nella query iniziale, in teoria
				// non serve e quindi il codice scritto qui dovrebbe andare sempre benissimo.
				int col = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= col; i++) {
					System.out.print(rs.getObject(i) + " ");
				}

				System.out.println("");

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// MIO CONSIGLIO PERSONALE - provate a creare un DB di prova e fate tutti i test
	// che volete, oppure modificate i dati del codice per prendere i record da
	// vivaio felice!

}
