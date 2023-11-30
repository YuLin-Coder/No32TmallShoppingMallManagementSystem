package tmall.dao;

import org.hibernate.SessionFactory;

/**
 * DAO层，获取 SessionFactory
 */

public interface DAO {
    public SessionFactory getSessionFactory();
}
