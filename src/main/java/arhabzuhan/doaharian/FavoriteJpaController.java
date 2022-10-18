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
import javax.persistence.Persistence;

/**
 *
 * @author Zuhan
 */
public class FavoriteJpaController implements Serializable {

    public FavoriteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("arhabzuhan_doaharian_jar_0.0.1-SNAPSHOTPU");

    public FavoriteJpaController() {
    }
    
    

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Favorite favorite) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Doa doa = favorite.getDoa();
            if (doa != null) {
                doa = em.getReference(doa.getClass(), doa.getIdDoa());
                favorite.setDoa(doa);
            }
            em.persist(favorite);
            if (doa != null) {
                Favorite oldFavoriteOfDoa = doa.getFavorite();
                if (oldFavoriteOfDoa != null) {
                    oldFavoriteOfDoa.setDoa(null);
                    oldFavoriteOfDoa = em.merge(oldFavoriteOfDoa);
                }
                doa.setFavorite(favorite);
                doa = em.merge(doa);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findFavorite(favorite.getIdDoa()) != null) {
                throw new PreexistingEntityException("Favorite " + favorite + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Favorite favorite) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Favorite persistentFavorite = em.find(Favorite.class, favorite.getIdDoa());
            Doa doaOld = persistentFavorite.getDoa();
            Doa doaNew = favorite.getDoa();
            List<String> illegalOrphanMessages = null;
            if (doaOld != null && !doaOld.equals(doaNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Doa " + doaOld + " since its favorite field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (doaNew != null) {
                doaNew = em.getReference(doaNew.getClass(), doaNew.getIdDoa());
                favorite.setDoa(doaNew);
            }
            favorite = em.merge(favorite);
            if (doaNew != null && !doaNew.equals(doaOld)) {
                Favorite oldFavoriteOfDoa = doaNew.getFavorite();
                if (oldFavoriteOfDoa != null) {
                    oldFavoriteOfDoa.setDoa(null);
                    oldFavoriteOfDoa = em.merge(oldFavoriteOfDoa);
                }
                doaNew.setFavorite(favorite);
                doaNew = em.merge(doaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = favorite.getIdDoa();
                if (findFavorite(id) == null) {
                    throw new NonexistentEntityException("The favorite with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Favorite favorite;
            try {
                favorite = em.getReference(Favorite.class, id);
                favorite.getIdDoa();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The favorite with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Doa doaOrphanCheck = favorite.getDoa();
            if (doaOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Favorite (" + favorite + ") cannot be destroyed since the Doa " + doaOrphanCheck + " in its doa field has a non-nullable favorite field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(favorite);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Favorite> findFavoriteEntities() {
        return findFavoriteEntities(true, -1, -1);
    }

    public List<Favorite> findFavoriteEntities(int maxResults, int firstResult) {
        return findFavoriteEntities(false, maxResults, firstResult);
    }

    private List<Favorite> findFavoriteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Favorite.class));
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

    public Favorite findFavorite(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Favorite.class, id);
        } finally {
            em.close();
        }
    }

    public int getFavoriteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Favorite> rt = cq.from(Favorite.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
