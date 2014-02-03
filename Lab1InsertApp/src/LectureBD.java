import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class LectureBD {   
	private Connection conn;
	
   public class Role {
      public Role(int i, String n, String p) {
         id = i;
         nom = n;
         personnage = p;
      }
      protected int id;
      protected String nom;
      protected String personnage;
   }
   
   public LectureBD() {
      connectionBD();                     
   }
   
   
   public void lecturePersonnes(String nomFichier){      
      try {
         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         XmlPullParser parser = factory.newPullParser();

         InputStream is = new FileInputStream(nomFichier);
         parser.setInput(is, null);

         int eventType = parser.getEventType();

         String tag = null, 
                nom = null,
                anniversaire = null,
                lieu = null,
                photo = null,
                bio = null;
         
         int id = -1;
         
         while (eventType != XmlPullParser.END_DOCUMENT) 
         {
            if(eventType == XmlPullParser.START_TAG) 
            {
               tag = parser.getName();
               
               if (tag.equals("personne") && parser.getAttributeCount() == 1)
                  id = Integer.parseInt(parser.getAttributeValue(0));
            } 
            else if (eventType == XmlPullParser.END_TAG) 
            {                              
               tag = null;
               
               if (parser.getName().equals("personne") && id >= 0)
               {
                  insertionPersonne(id,nom,anniversaire,lieu,photo,bio);
                                    
                  id = -1;
                  nom = null;
                  anniversaire = null;
                  lieu = null;
                  photo = null;
                  bio = null;
               }
            }
            else if (eventType == XmlPullParser.TEXT && id >= 0) 
            {
               if (tag != null)
               {                                    
                  if (tag.equals("nom"))
                     nom = parser.getText();
                  else if (tag.equals("anniversaire"))
                     anniversaire = parser.getText();
                  else if (tag.equals("lieu"))
                     lieu = parser.getText();
                  else if (tag.equals("photo"))
                     photo = parser.getText();
                  else if (tag.equals("bio"))
                     bio = parser.getText();
               }              
            }
            
            eventType = parser.next();            
         }
      }
      catch (XmlPullParserException e) {
          System.out.println(e);   
       }
       catch (IOException e) {
         System.out.println("IOException while parsing " + nomFichier); 
       }
   }   
   
   public void lectureFilms(String nomFichier){
      try {
         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         XmlPullParser parser = factory.newPullParser();

         InputStream is = new FileInputStream(nomFichier);
         parser.setInput(is, null);

         int eventType = parser.getEventType();

         String tag = null, 
                titre = null,
                langue = null,
                poster = null,
                roleNom = null,
                rolePersonnage = null,
                realisateurNom = null,
                resume = null;
         
         ArrayList<String> pays = new ArrayList<String>();
         ArrayList<String> genres = new ArrayList<String>();
         ArrayList<String> scenaristes = new ArrayList<String>();
         ArrayList<Role> roles = new ArrayList<Role>();         
         ArrayList<String> annonces = new ArrayList<String>();
         
         int id = -1,
             annee = -1,
             duree = -1,
             roleId = -1,
             realisateurId = -1;
         
         while (eventType != XmlPullParser.END_DOCUMENT) 
         {
            if(eventType == XmlPullParser.START_TAG) 
            {
               tag = parser.getName();
               
               if (tag.equals("film") && parser.getAttributeCount() == 1)
                  id = Integer.parseInt(parser.getAttributeValue(0));
               else if (tag.equals("realisateur") && parser.getAttributeCount() == 1)
                  realisateurId = Integer.parseInt(parser.getAttributeValue(0));
               else if (tag.equals("acteur") && parser.getAttributeCount() == 1)
                  roleId = Integer.parseInt(parser.getAttributeValue(0));
            } 
            else if (eventType == XmlPullParser.END_TAG) 
            {                              
               tag = null;
               
               if (parser.getName().equals("film") && id >= 0)
               {
                  insertionFilm(id,titre,annee,pays,langue,
                             duree,resume,genres,realisateurNom,
                             realisateurId, scenaristes,
                             roles,poster,annonces);
                                    
                  id = -1;
                  annee = -1;
                  duree = -1;
                  titre = null;                                 
                  langue = null;                  
                  poster = null;
                  resume = null;
                  realisateurNom = null;
                  roleNom = null;
                  rolePersonnage = null;
                  realisateurId = -1;
                  roleId = -1;
                  
                  genres.clear();
                  scenaristes.clear();
                  roles.clear();
                  annonces.clear();  
                  pays.clear();
               }
               if (parser.getName().equals("role") && roleId >= 0) 
               {              
                  roles.add(new Role(roleId, roleNom, rolePersonnage));
                  roleId = -1;
                  roleNom = null;
                  rolePersonnage = null;
               }
            }
            else if (eventType == XmlPullParser.TEXT && id >= 0) 
            {
               if (tag != null)
               {                                    
                  if (tag.equals("titre"))
                     titre = parser.getText();
                  else if (tag.equals("annee"))
                     annee = Integer.parseInt(parser.getText());
                  else if (tag.equals("pays"))
                     pays.add(parser.getText());
                  else if (tag.equals("langue"))
                     langue = parser.getText();
                  else if (tag.equals("duree"))                 
                     duree = Integer.parseInt(parser.getText());
                  else if (tag.equals("resume"))                 
                     resume = parser.getText();
                  else if (tag.equals("genre"))
                     genres.add(parser.getText());
                  else if (tag.equals("realisateur"))
                     realisateurNom = parser.getText();
                  else if (tag.equals("scenariste"))
                     scenaristes.add(parser.getText());
                  else if (tag.equals("acteur"))
                     roleNom = parser.getText();
                  else if (tag.equals("personnage"))
                     rolePersonnage = parser.getText();
                  else if (tag.equals("poster"))
                     poster = parser.getText();
                  else if (tag.equals("annonce"))
                     annonces.add(parser.getText());                  
               }              
            }
            
            eventType = parser.next();            
         }
      }
      catch (XmlPullParserException e) {
          System.out.println(e);   
      }
      catch (IOException e) {
         System.out.println("IOException while parsing " + nomFichier); 
      }
   }
   
   public void lectureClients(String nomFichier){
      try {
         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         XmlPullParser parser = factory.newPullParser();

         InputStream is = new FileInputStream(nomFichier);
         parser.setInput(is, null);

         int eventType = parser.getEventType();               

         String tag = null, 
                nomFamille = null,
                prenom = null,
                courriel = null,
                tel = null,
                anniv = null,
                adresse = null,
                ville = null,
                province = null,
                codePostal = null,
                carte = null,
                noCarte = null,
                motDePasse = null,
                forfait = null;                                 
         
         int id = -1,
             expMois = -1,
             expAnnee = -1;
         
         while (eventType != XmlPullParser.END_DOCUMENT) 
         {
            if(eventType == XmlPullParser.START_TAG) 
            {
               tag = parser.getName();
               
               if (tag.equals("client") && parser.getAttributeCount() == 1)
                  id = Integer.parseInt(parser.getAttributeValue(0));
            } 
            else if (eventType == XmlPullParser.END_TAG) 
            {                              
               tag = null;
               
               if (parser.getName().equals("client") && id >= 0)
               {
                  insertionClient(id,nomFamille,prenom,courriel,tel,
                             anniv,adresse,ville,province,
                             codePostal,carte,noCarte, 
                             expMois,expAnnee,motDePasse,forfait);               
                                    
                  nomFamille = null;
                  prenom = null;
                  courriel = null;               
                  tel = null;
                  anniv = null;
                  adresse = null;
                  ville = null;
                  province = null;
                  codePostal = null;
                  carte = null;
                  noCarte = null;
                  motDePasse = null; 
                  forfait = null;
                  
                  id = -1;
                  expMois = -1;
                  expAnnee = -1;
               }
            }
            else if (eventType == XmlPullParser.TEXT && id >= 0) 
            {         
               if (tag != null)
               {                                    
                  if (tag.equals("nom-famille"))
                     nomFamille = parser.getText();
                  else if (tag.equals("prenom"))
                     prenom = parser.getText();
                  else if (tag.equals("courriel"))
                     courriel = parser.getText();
                  else if (tag.equals("tel"))
                     tel = parser.getText();
                  else if (tag.equals("anniversaire"))
                     anniv = parser.getText();
                  else if (tag.equals("adresse"))
                     adresse = parser.getText();
                  else if (tag.equals("ville"))
                     ville = parser.getText();
                  else if (tag.equals("province"))
                     province = parser.getText();
                  else if (tag.equals("code-postal"))
                     codePostal = parser.getText();
                  else if (tag.equals("carte"))
                     carte = parser.getText();
                  else if (tag.equals("no"))
                     noCarte = parser.getText();
                  else if (tag.equals("exp-mois"))                 
                     expMois = Integer.parseInt(parser.getText());
                  else if (tag.equals("exp-annee"))                 
                     expAnnee = Integer.parseInt(parser.getText());
                  else if (tag.equals("mot-de-passe"))                 
                     motDePasse = parser.getText();  
                  else if (tag.equals("forfait"))                 
                     forfait = parser.getText(); 
               }              
            }
            
            eventType = parser.next();            
         }
      }
      catch (XmlPullParserException e) {
          System.out.println(e);   
      }
      catch (IOException e) {
         System.out.println("IOException while parsing " + nomFichier); 
      }
   }   
   
   private void insertionPersonne(int id, String nom, String anniv, String lieu, String photo, String bio) 
   {      
	   System.out.println("insertion personnes");
	   // On insere la personne dans la BD
	   PreparedStatement preparedStatement = null;
	   
		String insertTableSQL = "INSERT INTO PERSONNE"
				+ "(personneId, nom, prenom, dateDeNaissance, lieuDeNaissance, photo, biographie) VALUES"
				+ "(?,?,?,?,?,?,?)";

		try {
			preparedStatement = conn.prepareStatement(insertTableSQL);
			
			String _prenom = nom.substring(0,nom.indexOf(" "));
			String _nom = nom.substring(nom.indexOf(" ") + 1, nom.length());
			
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date date = sdf1.parse(anniv);
			java.sql.Date sqlStartDate = new java.sql.Date(date.getTime()); 

			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, _nom);
			preparedStatement.setString(3, _prenom);
			preparedStatement.setDate(4,sqlStartDate);
			preparedStatement.setString(5, lieu);
			preparedStatement.setString(6, photo);
			preparedStatement.setString(7, bio);			

			// execute insert SQL stetement
			preparedStatement.executeUpdate();

			System.out.println("Record is inserted into DBUSER table!");

		} catch (Exception e) 
		{

			System.out.println(e.getMessage());

		} finally {

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

   }
   
   private void insertionFilm(int id, String titre, int annee,
                           ArrayList<String> pays, String langue, int duree, String resume,
                           ArrayList<String> genres, String realisateurNom, int realisateurId,
                           ArrayList<String> scenaristes,
                           ArrayList<Role> roles, String poster,
                           ArrayList<String> annonces) {         
      // On le film dans la BD
	         
   }
   
   private void insertionClient(int id, String nomFamille, String prenom,
                             String courriel, String tel, String anniv,
                             String adresse, String ville, String province,
                             String codePostal, String carte, String noCarte,
                             int expMois, int expAnnee, String motDePasse,
                             String forfait) 
   {
	   
	   int forfaitId = -1;
	   Statement stmt = null;
		 
	   System.out.println(adresse);
	   
	   String numeroCivique = adresse.substring(0, adresse.indexOf(" "));
	   String rue = adresse.substring(adresse.indexOf(" ") + 1, adresse.length());
	   
	   
      // On le client dans la BD
	   // On insere la personne dans la BD
	   PreparedStatement preparedStatement = null;
	   PreparedStatement preparedStatementAdresse = null;
	   PreparedStatement preparedStatementCarteCredit = null;
	   PreparedStatement preparedStatementForfait = null;
	   PreparedStatement preparedStatementClient = null;
	   
		String insertTableSQL = "INSERT INTO UTILISATEUR"
				+ "(nom, prenom, datedenaissance, motdepasse) VALUES"
				+ "(?,?,?,?)";
		
		String insertTableSQLAdresse = "INSERT INTO ADRESSE"
				+ "(numerocivique, rue, ville, province, codepostal) VALUES"
				+ "(?,?,?,?,?)";

		String insertTableSQLCarteCredit = "INSERT INTO CarteCredit"
				+ "(typecarte, numero) VALUES"
				+ "(?,?)";

		String insertTableSQLForfait = "INSERT INTO Forfait"
				+ "(nom) VALUES"
				+ "(?)";
		
		String insertTableSQLClient = "INSERT INTO CLIENT"
				+ "(clientid, adresseid, cartecreditid, forfaitid, utilisateurid, courriel, numerotelephone) VALUES"
				+ "(?,?,?,?,?,?,?)";
		
		try {
			
			preparedStatement = conn.prepareStatement(insertTableSQL, new String[]{"utilisateurId"});
			preparedStatementAdresse = conn.prepareStatement(insertTableSQLAdresse, new String[]{"adresseId"});
			preparedStatementCarteCredit = conn.prepareStatement(insertTableSQLCarteCredit, new String[]{"carteCreditId"});
			preparedStatementForfait = conn.prepareStatement(insertTableSQLForfait, new String[]{"forfaitId"});
			preparedStatementClient = conn.prepareStatement(insertTableSQLClient);
			
			
			preparedStatementAdresse.setString(1, numeroCivique);
			preparedStatementAdresse.setString(2, rue);
			preparedStatementAdresse.setString(3, ville);
			preparedStatementAdresse.setString(4, province);
			preparedStatementAdresse.setString(5,codePostal);			
			
			// execute insert SQL stetement
			preparedStatementAdresse.executeUpdate();
			
			System.out.println("Record is inserted into Adresse table!");
						
			
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date date = sdf1.parse(anniv);
			java.sql.Date sqlStartDate = new java.sql.Date(date.getTime()); 

			preparedStatement.setString(1, nomFamille);
			preparedStatement.setString(2, prenom);
			preparedStatement.setDate(3, sqlStartDate);
			preparedStatement.setString(4,motDePasse);			

			// execute insert SQL stetement
			preparedStatement.executeUpdate();

			System.out.println("Record is inserted into Utilisateur table!");
	
			// preparedStatementAdresse.getGeneratedKeys().first() INTO CLIENT.ADRESSEID
			
			preparedStatementCarteCredit.setString(1, carte);
			preparedStatementCarteCredit.setString(2, noCarte);		
			
			// execute insert SQL stetement
			preparedStatementCarteCredit.executeUpdate();
			
			System.out.println("Record is inserted into CarteCredit table!");

			
			stmt = conn.createStatement();
			String sql = "SELECT forfaitid FROM forfait WHERE nom='" + forfait
					+ "'";
			ResultSet rs = stmt.executeQuery(sql);
			ResultSet generatedKeys;

			while (rs.next()) 
			{
				// Retrieve by column name
				forfaitId = rs.getInt("forfaitid");
			}


			rs.close();
			
			if(forfaitId == -1)
			{
				preparedStatementForfait.setString(1, forfait);	
				
				// execute insert SQL stetement
				preparedStatementForfait.executeUpdate();

				generatedKeys = preparedStatementForfait.getGeneratedKeys();
				if (null != generatedKeys && generatedKeys.next()) 
				{
					forfaitId = generatedKeys.getInt(1);
				}
			}
			
			// Display values
			System.out.print("Forfait id: " + forfaitId);		


			System.out.println("Record is inserted into Forfait table!");
		
			
			System.out.println(id);
			int idUtilisateur = -1;
			generatedKeys = preparedStatement.getGeneratedKeys();
			if (null != generatedKeys && generatedKeys.next()) 
			{
			     idUtilisateur = generatedKeys.getInt(1);
			}
			System.out.println(idUtilisateur);			
			
			int idAdresse = -1;
			generatedKeys = preparedStatementAdresse.getGeneratedKeys();
			if (null != generatedKeys && generatedKeys.next()) 
			{
			     idAdresse = generatedKeys.getInt(1);
			}
			
			System.out.println(idAdresse);
			int idCarteCredit = -1;
			generatedKeys = preparedStatementCarteCredit.getGeneratedKeys();
			if (null != generatedKeys && generatedKeys.next()) 
			{
				idCarteCredit = generatedKeys.getInt(1);
			}

			preparedStatementClient.setInt(1, id);
			preparedStatementClient.setInt(2, idAdresse);
			preparedStatementClient.setInt(3, idCarteCredit);
			preparedStatementClient.setInt(4, forfaitId);
			preparedStatementClient.setInt(5, idUtilisateur);
			preparedStatementClient.setString(6, courriel);
			preparedStatementClient.setString(7, tel);	
			
			// execute insert SQL stetement
			preparedStatementClient.executeUpdate();
			
			System.out.println("Record is inserted into Client table!");

		} catch (Exception e) 
		{

			System.out.println(e.getMessage());

		} finally {

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (preparedStatementAdresse != null) {
				try {
					preparedStatementAdresse.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (preparedStatementClient != null) {
				try {
					preparedStatementClient.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("Client prepared statement ");
					e.printStackTrace();
				}
			}
			
			if (preparedStatementForfait != null) {
				try {
					preparedStatementForfait.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (preparedStatementCarteCredit != null) {
				try {
					preparedStatementCarteCredit.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
   }
   
   private void connectionBD(){
      // On se connecte a la BD
	   try {
		conn = DriverManager.getConnection( "jdbc:oracle:thin:@dijkstra.logti.etsmtl.ca:1521/log660","equipe18", "MBKzbx5S" );
		System.out.println("jdbcworking");
	   } catch (SQLException e) {
		// TODO Auto-generated catch block
		System.out.println("jdbc not working");
		e.printStackTrace();
	}
   }

   public static void main(String[] args) {
      LectureBD lecture = new LectureBD();
      
      lecture.lecturePersonnes(args[0]);
      //lecture.lectureFilms(args[1]);
      lecture.lectureClients(args[2]);
   }
}
