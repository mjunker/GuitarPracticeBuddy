package ch.guitarpracticebuddy.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 2/16/13
 * Time: 12:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class PersistenceUtil {

    public static EntityManager init() {
        String userHome = System.getProperty("user.home");
        String guitarBuddyHome = userHome + "/guitarbuddy";
        System.setProperty("objectdb.home", guitarBuddyHome);
        System.setProperty("guitarbuddy.home", guitarBuddyHome);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("$objectdb/db/guitarpracticebuddy.odb");
        final EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        return em;
    }
}
