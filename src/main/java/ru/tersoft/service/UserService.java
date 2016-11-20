package ru.tersoft.service;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tersoft.entity.Account;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {
    private final SessionFactory sessionFactory;

    @Autowired
    public UserService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Account findUserByMail(String mail) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Account where mail = '" + mail + "'");
        return (Account) query.list().get(0);
    }
}
