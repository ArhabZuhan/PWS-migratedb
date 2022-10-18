/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arhabzuhan.doaharian;

import arhabzuhan.doaharian.exceptions.IllegalOrphanException;
import arhabzuhan.doaharian.exceptions.NonexistentEntityException;
import arhabzuhan.doaharian.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Zuhan
 */
public class UserJpaController implements Serializable {

    public UserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        Doa doaOrphanCheck = user.getDoa();
        if (doaOrphanCheck != null) {
            User oldUserOfDoa = doaOrphanCheck.getUser();
            if (oldUserOfDoa != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Doa " + doaOrphanCheck + " already has an item of type User whose doa column cannot be null. Please make another selection for the doa field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Doa doa = user.getDoa();
            if (doa != null) {
                doa = em.getReference(doa.getClass(), doa.getIdDoa());
                user.setDoa(doa);
            }
            em.persist(user);
            if (doa != null) {
                doa.setUser(user);
                doa = em.merge(doa);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUser(user.getIdUser()) != null) {
                throw new PreexistingEntityException("User " + user + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getIdUser());
            Doa doaOld = persistentUser.getDoa();
            Doa doaNew = user.getDoa();
            List<String> illegalOrphanMessages = null;
            if (doaNew != null && !doaNew.equals(doaOld)) {
                User oldUserOfDoa = doaNew.getUser();
                if (oldUserOfDoa != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Doa " + doaNew + " already has an item of type User whose doa column cannot be null. Please make another selection for the doa field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (doaNew != null) {
                doaNew = em.getReference(doaNew.getClass(), doaNew.getIdDoa());
                user.setDoa(doaNew);
            }
            user = em.merge(user);
            if (doaOld != null && !doaOld.equals(doaNew)) {
                doaOld.setUser(null);
                doaOld = em.merge(doaOld);
            }
            if (doaNew != null && !doaNew.equals(doaOld)) {
                doaNew.setUser(user);
                doaNew = em.merge(doaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = user.getIdUser();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getIdUser();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            Doa doa = user.getDoa();
            if (doa != null) {
                doa.setUser(null);
                doa = em.merge(doa);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public User findUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
