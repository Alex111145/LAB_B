


/**
 * Contiene il Main del progetto Book_Recommender.
 * 
 * @author Alessio     	Gervasini 		Mat. 756181
 * @author Francesco 	Orsini Pio		Mat. 756954
 * @author Luca      	Borin        	Mat. 756563

 
 */
package Book_Recommender;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Classe principale per l'applicazione di Book Recommender.
 */
public class Main {
        // Definizione dei codici ANSI per i colori
    public static final String RESET = "\033[0m";  // Resetta il colore
    public static final String ROSSO = "\033[0;31m";
    public static final String VERDE = "\033[0;32m";
    public static final String GIALLO = "\033[0;33m";
    public static final String CIANO = "\033[0;36m";
    public static final String VIOLA = "\033[0;35m"; 
    public static final String V = "✓ "; // Carattere  per il segno di spunta
    public static final String X = "x ";
        
    public static final String abs = "data" + File.separator;
    public static final String librerie_path = abs + "Librerie.dati.csv";
    public static final String FILE_PATH = abs + "Data.csv";
    public static final String reg = abs + "UtentiRegistrati.csv";
    public static final String VALUTAZIONI_FILE_PATH = abs + "ValutazioniLibri.csv";
    public static final String CONSIGLI_FILE_PATH = abs + "ConsigliLibri.dati.csv";
    public static final String LIBRI_FILE_PATH = abs + "Libri.csv";
    private static final long CHECK_INTERVAL = 10000; // Intervallo di controllo in millisecondi (10 secondi)
    private static final long UPDATE_INTERVAL = 30000; // Intervallo di aggiornamento in millisecondi (30 secondi)
    private static Timer timer; // Timer per l'aggiornamento
    private static Timer timer2; // Timer per il controllo dei file

    public static void main(String[] args) {
        menu();
    }

    /**
     * Pianifica le attività di aggiornamento automatico in base alla presenza di file specifici.
     */
    public static void autoaggiornamento() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    File valutazioniFile = new File(VALUTAZIONI_FILE_PATH);
                    File consigliFile = new File(CONSIGLI_FILE_PATH);
                    if (valutazioniFile.exists() || consigliFile.exists()) {
                        timer.cancel();
    
                        timer2 = new Timer();
                        timer2.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    Libricsv.generaFileLibri();
                                } catch (Exception e) {
                                    // Log l'errore o gestiscilo in altro modo
                                }
                            }
                        }, 0, UPDATE_INTERVAL); // Esegui subito e ripeti ogni 30 secondi
                    }
                } catch (Exception e) {
                    // Log l'errore o gestiscilo in altro modo
                }
            }
        }, 0, CHECK_INTERVAL); // Controlla ogni 10 secondi se il file esiste
    }
    /**
     * Mostra il menu principale dell'applicazione.
     */
    public static void menu() {
        Scanner scanner = new Scanner(System.in);

        // Menu principale
        while (true) {
            try {
                System.out.println(GIALLO+"\n\n* Benvenuto in Book Recommender!\n"+RESET);
                System.out.println("1. Cerca Libro (per utenti non registati)");
                System.out.println("2. Registrazione");
                System.out.println("3. Login");
                System.out.println(ROSSO+"4. Chiusura Programma"+RESET);
                System.out.print("\nScegli un'opzione: ");

                String sceltaInput = scanner.nextLine();
                if (sceltaInput.isEmpty()) {
                    System.out.println("\nInserisci un'opzione valida.");
                    continue;
                }
                
                // Controllo se l'input contiene solo numeri
                if (!sceltaInput.matches("\\d+")) {
                    System.out.println("\n"+ROSSO+ X +"Impossibile inserire una parola. Riprova."+RESET);
                    continue;
                }

                int scelta = Integer.parseInt(sceltaInput);
                
                if (scelta < 1 || scelta > 4) {
                    System.out.println("\n"+ROSSO+X+"Numero non accettato. Riprova."+RESET);
                    continue;
                }

                if (scelta == 1) {
                    ricercalibronologin();
                } else if (scelta == 2) {
                    // Registrazione utente
                    RegistrazioneUtente.registrazione();
                    menuUtenteRegistrato(sceltaInput);
                } else if (scelta == 3) {
                    System.out.print("\nUserID: ");
                    String userid = scanner.nextLine().trim();

                    if (userid.isEmpty()) {
                        System.out.println("\n"+ROSSO+X+"Errore: L'UserID non può essere vuoto."+RESET);
                        continue;
                    }

                    if (RegistrazioneUtente.login(userid)) {
                        System.out.println("\n"+VERDE+V+"Autenticazione riuscita!\n"+RESET);
                        menuUtenteRegistrato(userid);
                    } else {
                        System.out.println("\n"+ROSSO+X+"Autenticazione fallita"+RESET);
                    }
                } else if (scelta == 4) {
                    // Esci
                    System.out.println("\n\n"+ROSSO+"Chiusura Programma... \n"+RESET);
                    System.exit(0);
                    break;
                }
            } catch (NoSuchElementException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.out.println("\n\n"+ROSSO+"Chiusura Programma ...\n"+RESET);
                System.exit(0);
        
            }
        }
        scanner.close();
    }

    /**
     * Mostra il menu per un utente registrato.
     * 
     * @param userid L'ID dell'utente registrato.
     */
    public static void menuUtenteRegistrato(String userid) {
        autoaggiornamento();
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println(CIANO +"\n\n* Menu Utente Registrato:\n"+RESET);
            System.out.println("1. Crea libreria");
            System.out.println("2. Aggiungi libro alla libreria");
            System.out.println("3. Valuta libro");
            System.out.println("4. Consiglia libro");
            System.out.println(ROSSO+"5. Logout"+RESET);
            System.out.print("\nScegli un'opzione: ");

            String sceltaInput = scanner.nextLine();
            if (sceltaInput.isEmpty() || !isNumeric(sceltaInput)) {
                System.out.println(ROSSO + X+"Inserisci un'opzione valida."+RESET);
                continue;
            }

            int sceltaUtente = Integer.parseInt(sceltaInput);

            if (sceltaUtente == 1) {
               // Creazione libreria
System.out.print("\nInserisci il nome della libreria:\n\n ");
String nomeLibreria = scanner.nextLine();
if (nomeLibreria.isEmpty()) {
    System.out.println("Il nome della libreria non può essere vuoto: (digita 'salva' per salvare la libreria)\n");
    continue;
}
Libreria libreria = new Libreria(nomeLibreria);

// Aggiunta libri alla libreria
System.out.print("\n" + VERDE + V + "Inserisci i titoli dei libri da aggiungere a '" + nomeLibreria + "' (digita 'salva' per salvare i libri):\n\n" + RESET);

boolean almenoUnLibroAggiunto = false;

while (true) {
    String libro = scanner.nextLine().toLowerCase();
    if (libro.equalsIgnoreCase("salva")) {
        if (!almenoUnLibroAggiunto) {
            System.out.println("\n" + ROSSO + X + "Devi aggiungere almeno un libro prima di poter salvare la libreria! Inserisci un titolo:\n" + RESET);
            continue; // Torna all'inizio del ciclo per richiedere un input valido
        }
        break;
    }
    if (libro.isEmpty()) {
        System.out.println(ROSSO + X + "Il titolo del libro non può essere vuoto: (digita 'salva' per salvare la libreria)" + RESET);
        continue;
    }

    if (libroEsisteInDataCsv(libro)) {
        if (libreria.aggiungiLibro(libro)) {
            almenoUnLibroAggiunto = true; // Segna che almeno un libro è stato aggiunto
            System.out.println("\n" + VERDE + V + "Libro aggiunto con successo alla libreria '" + nomeLibreria + "'. Inserisci altri titoli per aggiungerli (digita 'salva' per salvare la libreria)! \n" + RESET);
        } else {
            System.out.println("\n" + ROSSO + X + libro + " già presente nella libreria, inserisci un altro titolo (digita 'salva' per salvare la libreria)\n" + RESET);
        }
    } else {
        System.out.println("\n" + ROSSO + X + "Non esiste nessun libro con quel titolo nella biblioteca, riprova! (digita 'salva' per salvare i libri)\n" + RESET);
    }
}

// Registrazione libreria
Libreria.registraLibreria(userid, libreria);
System.out.println("\n" + VERDE + V + "Libreria registrata con successo!" + RESET);





            } else if (sceltaUtente == 2) {

                File file = new File(librerie_path);
                if (!file.exists()) {
                    System.out.println("\n"+ROSSO+X+"Non hai librerie registrate, creane una nuova prima di selezionarne una"+RESET);
                  menuUtenteRegistrato(userid);// Termina l'esecuzione se il file non esiste
                }

                System.out.println("\nSeleziona la libreria dove vuoi aggiungere il libro:\n");

                String nomeLibreria = Libreria.selezionaLibreria(userid);
                if (nomeLibreria != null) {
                    System.out.print("\n"+VERDE+V+"Inserisci il titolo del libro da aggiungere a '" + nomeLibreria + "' (digita 'salva' per salvare i libri):\n\n"+RESET);

                    while (true) {
                        String libro = scanner.nextLine().toLowerCase();

                        if (libro.equalsIgnoreCase("salva")) {
                            System.out.print(VERDE+V+"\nTornando al menu principale...\n");
                            break;
                        }
                        if (Libreria.libroGiaPresenteNellaLibreria(userid, nomeLibreria, libro)) {
                            System.out.println("\n"+ROSSO + X+ "Il libro "+libro+ " è già presente nella libreria '" + nomeLibreria + "', cambia libro  (digita 'salva' per salvare i libri)" + RESET);
                            continue;
                        }
                        if (libro.isEmpty()) {
                            System.out.println(ROSSO+X +"Il titolo del libro non può essere vuoto: (digita 'salva' per salvare la libreria)"+RESET);
                            continue;
                        }

                        if (libroEsisteInDataCsv(libro) && !Libreria.libroGiaPresenteNellaLibreria(userid, nomeLibreria, libro)) {
                            
                            Libreria.aggiungiLibroALibreria(userid, nomeLibreria, libro);
                            System.out.println("\n"+VERDE +V + "Libro aggiunto con successo!\n "+RESET);
                            menuUtenteRegistrato(userid);
                        } else {
                            System.out.println("\n"+ROSSO +X +"Il libro non esiste in biblioteca o è già presente nella libreria, riprova! (digita 'salva' per salvare i libri)\n"+RESET);
                        }
                    }
                }

            } else if (sceltaUtente == 3) {
              
                String librerieStr = Libreria.visualizzaLibrerieConLibri(userid);
                if (librerieStr == null || librerieStr.isEmpty()) {
                    System.out.println("\n"+ROSSO+X +"Non hai librerie registrate. Creane una nuova prima di valutarne un libro."+RESET);
                    continue;
                }

                int libreriaIndex;
                String nomeLibreria = null;

                while (true) {
                    System.out.print("\nSeleziona il numero della libreria: ");
                    String sceltaLibreria = scanner.nextLine();
                    try {
                        libreriaIndex = Integer.parseInt(sceltaLibreria);
                        nomeLibreria = Libreria.getLibreriaByIndex(userid, libreriaIndex);
                        if (nomeLibreria == null) {
                            System.out.print("\n"+ROSSO+X +"Selezione non valida. Riprova."+RESET+"\n");
                        } else {
                            break; // uscire dal ciclo quando una libreria valida è stata selezionata
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\n"+ROSSO+X+"Selezione non valida, riprova."+RESET);
                       
                    }
                }

                System.out.print("\n"+VERDE+V+"Hai selezionato la libreria: " + nomeLibreria+RESET+"\n");
    

                while (true) {
                    System.out.print("\nSeleziona il numero del libro nella libreria " + nomeLibreria + " : ");
                    String sceltaLibro = scanner.nextLine().toLowerCase();
                    int libroIndex;
                    try {
                        libroIndex = Integer.parseInt(sceltaLibro);
                        String titoloLibro = Libreria.getLibroByIndex(userid, nomeLibreria, libroIndex);
                        if (titoloLibro==null) {
      
                        } else if (!titoloLibro.isEmpty()) {
                            ValutazioneLibro.inserisciValutazioneLibro(titoloLibro, userid);

                            System.out.println("\nTorno al menu utenti registrati .... \n");

                            // break; // uscire dal ciclo quando una valutazione è stata completata
                        } else {
                            System.out.println("Il titolo del libro non può essere vuoto: (digita 'salva' per salvare la libreria)");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\n"+ROSSO+X+"Selezione non valida. Riprova."+RESET);
               
                    }

                }

            } else if (sceltaUtente == 4) {
                // Consiglia libro
                String librerieStr = Libreria.visualizzaLibrerieConLibri(userid);
                if (librerieStr == null || librerieStr.isEmpty()) {
                    System.out.println("\n"+ROSSO+X+"Non hai librerie registrate. Creane una nuova prima di consigliare un libro."+RESET);
                    continue;
                }

                int libreriaIndex;
                String nomeLibreria = null;

                while (true) {
                    System.out.print("\nSeleziona il numero della libreria: ");
                    String sceltaLibreria = scanner.nextLine();
                    try {
                        libreriaIndex = Integer.parseInt(sceltaLibreria);
                        nomeLibreria = Libreria.getLibreriaByIndex(userid, libreriaIndex);
                        if (nomeLibreria == null) {
                            System.out.print("\n"+ROSSO+X+"Libreria non trovata. Riprova."+RESET+'\n');
                        } else {
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.print("\n"+ROSSO+X+"Selezione non valida. Riprova \n"+RESET);
                    }
                }

                System.out.print("\n"+VERDE+ V+"Hai selezionato la libreria: " + nomeLibreria +RESET+"\n");
              

                while (true) {
                    System.out.print("\nSeleziona il numero del libro nella libreria " + nomeLibreria + ": ");
                    String sceltaLibro = scanner.nextLine().toLowerCase();
                    int libroIndex;
                    try {
                        libroIndex = Integer.parseInt(sceltaLibro);
                        String titoloLibro = Libreria.getLibroByIndex(userid, nomeLibreria, libroIndex);
                        if (titoloLibro==null) {
                            System.out.print("\n"+ROSSO +X+"Libro non trovato. Riprova."+ RESET+ "\n");
                           
                        }

                         else {
                            List<String> libriConsigliati = new ArrayList<>();
                            while (true) {
                                System.out.println("\nInserisci i titoli dei libri consigliati per '"+titoloLibro+"' (max 3, digita 'salva' per terminare):\n");

                                int count = 0;
                                while (count < 3) {
                                    String libroConsigliato = scanner.nextLine();

                                    if (libroConsigliato.equalsIgnoreCase("salva")) {
                                        System.out.println("\n"+VERDE+V+"Libri consigliati con successo!\n");
                                        ConsiglioLibro.writeToFile(userid, titoloLibro, libriConsigliati);
                                        menuUtenteRegistrato(userid);
                                        return;
                                    }
                                    if (libroConsigliato.isEmpty()) {
                                        System.out.println("\n"+ROSSO+X+"Il titolo del libro non può essere vuoto! (digita 'salva' per salvare i libri consigliati)\n"+RESET);
                                        continue;
                                    }

                                    if (libriConsigliati.contains(libroConsigliato)) {
                                        System.out.println("\n"+ROSSO+X+"Libro già consigliato, riprova! (digita 'salva' per salvare i libri consigliati)\n"+RESET);
                                        continue;
                                    }
                                     // Controllo per evitare di consigliare lo stesso libro selezionato
                                    if (libroConsigliato.equalsIgnoreCase(titoloLibro)) {
                                     System.out.println("\n" + ROSSO + X + "Non puoi consigliare lo stesso libro selezionato, riprova con un altro titolo!\n" + RESET);
                                     continue;
                                     }

                                    if (libroEsisteInDataCsv(libroConsigliato)) {
                                        libriConsigliati.add(libroConsigliato);
                                        System.out.println("\n"+VERDE+V+"Libro presente in biblioteca, dammi il prossimo titolo! (digita 'salva' per salvare i libri consigliati)\n"+RESET);
                                        count++;
                                     
                                    } else {
                                        System.out.println("\n"+ROSSO+X+"Il libro non è presente in biblioteca, riprova! (digita 'salva' per salvare i libri consigliati)\n"+RESET);
                                    }
                                }

                                System.out.println("\n"+VERDE+V+" Hai raggiunto il massimo dei libri consigliabili, torno al menù utente registrato... \n"+RESET);
                        
                    
                                ConsiglioLibro.writeToFile(userid, titoloLibro, libriConsigliati);

                                menuUtenteRegistrato(userid);

                                return;
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.print("\n"+ROSSO+X+"Selezione non valida, riprova.\n"+RESET);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (sceltaUtente == 5) {
                // Logout
                System.out.println(ROSSO+"\nLogout effettuato con successo!\n"+RESET);
               menu();

                if (timer != null) {
                    timer.cancel(); // Ferma il timer1
                }
                if (timer2 != null) {
                    timer2.cancel(); // Ferma il timer2
                    menu();
                }

                break;
               
            } else {
                System.out.println("\n"+ROSSO+X +"Opzione non valida. Riprova."+RESET);
            }
        }
    }

    /**
     * Mostra il menu di ricerca per gli utenti non registrati.
     */
    public static void ricercalibronologin() {
        if (!new File(LIBRI_FILE_PATH).exists()) {
            Libricsv.generaFileLibri();
        }
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println(VIOLA+"\n\n* Menu di ricerca libri:\n"+RESET);
            System.out.println("1. Cerca per titolo");
            System.out.println("2. Cerca per autore");
            System.out.println("3. Cerca per autore e anno");
            System.out.println(ROSSO+"4. Esci"+RESET);
            System.out.print("\nInserisci la tua scelta: ");

       

            int scelta = scanner.nextInt();
            scanner.nextLine(); // Consuma la newline

            switch (scelta) {
                case 1:
                    while (true) {
                        System.out.print("\nInserisci il titolo da cercare (digita 'back' per tornare indietro): ");
                        String titolo = scanner.nextLine();
                        if (titolo.equalsIgnoreCase("back")) {
                            ricercalibronologin();
                            break;
                        } else {
                            Libreria.cercaLibroPerTitolo(titolo);
                            ricercalibronologin();
                        }
                    }
                    break;
                case 2:
                    while (true) {
                        System.out.print("\nInserisci l'autore da cercare (digita 'back' per tornare indietro): ");
                        String autore = scanner.nextLine();
                        if (autore.equalsIgnoreCase("back")) {
                            ricercalibronologin();
                            break;
                        } else {
                            Libreria.cercaLibroPerAutore(autore);
                            ricercalibronologin();
                        }
                    }
                    break;
                case 3:
                    Libreria.cercaLibroPerAutoreeanno();
                    break;
                case 4:
                    System.out.println("\n"+ROSSO+"Tornando al menù principale..."+RESET);
                    menu();
                    break;
                default:
                    System.out.println("\n"+ROSSO+X+"Scelta non valida. Riprova."+RESET);
            }
        }
    }

    /**
     * Legge un file CSV e restituisce una lista di array di stringhe rappresentanti i record.
     * 
     * @param filePath Il percorso del file CSV.
     * @return Una lista di array di stringhe rappresentanti i record.
     */
    private static List<String[]> leggiFileCsv(String filePath) {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Divisione dei campi tenendo conto delle virgole all'interno delle virgolette
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].replace("\"", "").trim(); // Rimuove le virgolette e gli spazi vuoti all'inizio e alla fine
                }
                records.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * Controlla se un libro esiste nel file CSV dei dati basato sul titolo.
     * 
     * @param titolo Il titolo del libro da controllare.
     * @return true se il libro esiste, false altrimenti.
     */
    public static boolean libroEsisteInDataCsv(String titolo) {
        List<String[]> libri = leggiFileCsv(FILE_PATH);
        for (String[] datiLibro : libri) {
            if (datiLibro.length > 0 && datiLibro[0].equalsIgnoreCase(titolo)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Controlla se una stringa è numerica.
     * 
     * @param str La stringa da controllare.
     * @return true se la stringa è numerica, false altrimenti.
     */
    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }
}