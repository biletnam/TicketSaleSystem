package ru.tersoft.service;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.domain.Account;

@Service("AccountService")
@Transactional
public class AccountService {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Account> getAll() {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        // Create a Hibernate query (HQL)
        Query query = session.createQuery("FROM Account");
        // Retrieve all
        return  query.list();
    }

    public Account get( Integer id ) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        // Retrieve existing person first
        Account account = session.get(Account.class, id);
        return account;
    }

    public void add(Account account) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        // Save
        session.save(account);
    }

    public void delete(Integer id) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        // Retrieve existing person first
        Account account = session.get(Account.class, id);
        // Delete
        session.delete(account);
    }

    public void edit(Account account) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        // Retrieve existing person via id
        Account existingAccount = session.get(Account.class, account.getId());
        // Assign updated values to this person
        existingAccount.setFirstname(account.getFirstname());
        existingAccount.setLastname(account.getLastname());
        // Save updates
        session.save(existingAccount);
    }
}
