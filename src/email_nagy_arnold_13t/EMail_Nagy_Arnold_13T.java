package email_nagy_arnold_13t;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author mrazzeroth
 */
public class EMail_Nagy_Arnold_13T {

    /**
     * @param args the command line arguments
     */
    private static Connection conn = null;
    private static Statement stmt = null;
    private static Map<String, User> userek = new TreeMap<>();
    private static int STATE = 0;
    private static String whoami = "";
    
    public static void main(String[] args) throws SQLException {
        //connectDB();
        userek.put("dsa", new User("nincsagatyamban@gmail.com", "dsa"));
        functions(0);
    }
    
    private static void functions(int func) throws SQLException{
        Scanner scanner = new Scanner(System.in);
        if (STATE == 0){   //Login
            if(func == 0){
                System.out.println("Válaszd ki, hogy mit szeretnél tenni:\n1 - Bejelentkezés\n2 - Regisztráció\n3 - Kilépés");
                int a = 0;
                try{
                    a = scanner.nextInt();
                }
                catch(Exception e){
                    System.out.println("ÉRVÉNYTELEN érték");
                }
                switch(a){
                        case 1: functions(1); break;
                        case 2: functions(2); break;
                        case 3: System.exit(0); break;
                    }
            }
            else if(func== 1){
                System.out.println("Írd be az e-mail címed a bejelentkezéshez\n Ha még nem regisztráltál akkor írd az email-ed mögé, hogy -reg\n\nEmail:");
                String email = scanner.nextLine();
                if(email.split(" ").length == 2){
                    if ( email.split(" ")[1].startsWith("-")){
                        System.out.println("Adj meg egy megjelenítendő nevet:");
                        userek.put(email.split(" ")[0], new User(email.split(" ")[0], scanner.next()));

                    }
                }
                else            
                    System.out.println(login(email) ? "" : "Ez az email cím még nincs regisztrálva\n");
                functions(0);
            }
            else if(func == 2){
                System.out.println("Adj meg egy email címet a regisztrációhoz:\nEmail:");
                register();
                functions(1);
            }
        }
        else if(STATE == 1){
            //clear
            if(func == 0){
                System.out.println("Válaszd ki, hogy mit szeretnél tenni:\n1 - Üzenetek olvasása\n2 - Új üzenet küldése\n3 - Kijelentkezés");
                    int a = scanner.nextInt();
                    functions(a);
            }
            else if(func == 1){
                userek.get(whoami).getUzenetek();
                functions(0);
                
            }
            else if(func == 2){
                System.out.println("Add meg a címzett email címét:\n");
                String cimzett = scanner.next();
                if(!userek.containsKey(cimzett)){
                    System.out.println("Nem létező ímél");
                    functions(2);
                }
                System.out.println("Írj neki szép kis üzit:\n");
                String torzs = scanner.next();
                userek.get(whoami).ujKuldott(new Message(whoami, torzs, cimzett));
                functions(0);
            }
            else if(func == 3){
                STATE = 0;
                System.out.println("Sikeresen kijelentkeztél!!\n");
                functions(0);
            }
        }
    }
    
    private static void register(){
        Scanner scanner = new Scanner(System.in);
        String email = scanner.next();
        System.out.println("Adj meg egy becenevet:\n");
        String nick = scanner.next();
        userek.put(email, new User(email, nick));
    }
    
    
    private static boolean login(String _email){
        if(userek.containsKey(_email)){
            whoami = _email;
            STATE = 1;
            return true;
        }
      return false;
    }
    
    
}

class User{
    private String email;
    private String name;
    private Set<Message> bejovoUzenetek = new HashSet<>();
    private Set<Message> kimenoUzenetek = new HashSet<>();
    private Statement stmt;
    private Connection conn;
    
    public User( String email, String name) {
        this.name = name;
        this.email = email;

    }
    
    public void ujFogadott(Message m){
        bejovoUzenetek.add(m);
    }
    
    public void ujKuldott(Message m){
        kimenoUzenetek.add(m);

    }
    
    public void getUzenetek(){
        System.out.println("//======BEJÖVŐ============/");
        for(Message elem: bejovoUzenetek){
            System.out.println(elem);
        }
        System.out.println("\n//======KIMENŐ============/");
        for(Message elem: kimenoUzenetek){
            System.out.println(elem);
        }
    }
    
    @Override
    public String toString(){
        return "Email: " + email + " | Név: "+ name;
    }

    
}

class Message{
    private String felado;
    private String torzs;
    private String cimzett;

    public Message(String felado, String torzs, String cimzett) {
        this.felado = felado;
        this.torzs = torzs;
        this.cimzett = cimzett;
    }

    public String getFelado() {
        return felado;
    }

    public String getTorzs() {
        return torzs;
    }

    public String getCimzett() {
        return cimzett;
    }
    
    
    @Override
    public String toString(){
        return "Felado: " + felado + "\nCímzett: " + cimzett + "\n "+torzs;
    }
}

/*
Beadási határidő: 2018. 02. 19. óra vége
Beadás módja: Git repó
Konzolos alkalmazás.

Kezdeti adatokat be lehet kódolni, +5-ösért lehet adatbázisból (MySQL/SQLite) kiolvasni.
Adatot menteni nem kell, nem probléma, a bezárás után minden elveszik.

Tároljuk a felhasználókat és az üzeneteiket, mint pl. egy chat alkalmazás,
facebook üzenetküldés stb.

Minden felhasználónak (User):
 van egy email címe (String)
 és egy megjelenítendő neve (String).
 Emellett tároljuk a beérkező és a kimenő üzeneteket (egy-egy Set<Message>)
 
Minden üzenetnek (Message):
 van egy törzse (String)
 feladója (User)
 és címzettje (User)

A felhasználókat egy (email) -> (User) stílusú Map<String, User> -ben tároljuk,
akár egy private static változóban.

Az alkalmazásba kezdetben az email megadásával lehessen belépni.
Ekkor megjelenik az összes bejövő és kimenő üzenet.
Itt lehet új üzenetet írni, vagy kilépni a login képernyőre.

A login képernyőről lehet kilépni az alkalmazásból.
A főbb függvények részletesebb leírását a mellékelt folyamatábra mutatja.

Ezek mellett célszerűen megvalósítandó segédfüggvények:
 User.ujFogadott(Message m) - userhez hozzáad egy új fogadott üzenetet
 User.ujKuldott(Message m) - userhez hozzáad egy új küldött üzenetet
 User.toString()
 Message.toString() - az egyszerűbb kiíratáshoz


*/