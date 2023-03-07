import dao.daoVolatile.CreditDao;
import dao.IDao;
import metier.CreditMetier;
import metier.IMetierCredit;
import modele.Credit;
import presentation.CreditControleur;
import presentation.ICreditControleur;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Scanner;

public class SimulateurDeCredit_App {
    static ICreditControleur creditControleur;
    static Scanner clavier = new Scanner(System.in);

    private static boolean estUnNombre(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void test1() {
        //instanciation des différents composants de l'application
        var dao = new CreditDao();
        var metier = new CreditMetier();
        var controleur = new CreditControleur();
        //injection des dépendances
        metier.setCreditDao(dao);
        controleur.setCreditMetier(metier);
        //tester
        String rep = "";
        do {
            System.out.print("=>[Test1] Calcule de Mensualité de crédit <=\n");
            try {
                String inpuut = "";
                while (true) {
                    System.out.print("=>Entrez l'id du crédit:");
                    inpuut = clavier.nextLine();
                    if (estUnNombre(inpuut)) break;
                    System.err.println("Entrée non valide!!!");
                }
                long id = Long.parseLong(inpuut);
                controleur.afficher_Mensualite(id);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            System.out.print("Voulez vous quittez(oui/non)?");
            rep = clavier.nextLine();
        } while (!rep.equalsIgnoreCase("oui"));
        System.out.println("Au revoir ^_^");
    }



    public static void test2() throws Exception {
        String daoClass;
        String serviceClass;
        String controllerClass;
        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream propertiesFile = classLoader.getResourceAsStream("config.properties");
        if (propertiesFile == null) throw new Exception("fichier config introuvable !!!");
        else {
            try {
                properties.load(propertiesFile);
                daoClass = properties.getProperty("DAO");
                serviceClass = properties.getProperty("SERVICE");
                controllerClass = properties.getProperty("CONTROLLER");
                propertiesFile.close();
            } catch (IOException e) {
                throw new Exception("Probleme de chargement des propriétés du fichier config");
            } finally {
                properties.clear();
            }
        }
        try {
            Class cDao = Class.forName(daoClass);
            Class cMetier = Class.forName(serviceClass);
            Class cControleur = Class.forName(controllerClass);
            var dao = (IDao<Credit, Long>) cDao.getDeclaredConstructor(IDao.class).newInstance(cDao);
            var metier = (IMetierCredit) cMetier.getDeclaredConstructor().newInstance();
            creditControleur = (ICreditControleur) cControleur.getDeclaredConstructor().newInstance();
            Method setDao = cMetier.getMethod("setCreditDao", CreditDao.class);
            setDao.invoke(metier, dao);
            Method setMetier = cControleur.getMethod("setCreditMetier", CreditMetier.class);
            setMetier.invoke(creditControleur, metier);
            creditControleur.afficher_Mensualite(1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args)throws Exception{
        test2();
    }
    public static void test3()throws Exception{

}

}
