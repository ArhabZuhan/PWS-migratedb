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
public class DoaJpaController implements Serializable {

    public DoaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Doa doa) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        Favorite favoriteOrphanCheck = doa.getFavorite();
        if (favoriteOrphanCheck != null) {
            Doa oldDoaOfFavorite = favoriteOrphanCheck.getDoa();
            if (oldDoaOfFavorite != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Favorite " + favoriteOrphanCheck + " already has an item of type Doa whose favorite column cannot be null. Please make another selection for the favorite field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Favorite favorite = doa.getFavorite();
            if (favorite != null) {
                favorite = em.getReference(favorite.getClass(), favorite.getIdDoa());
                doa.setFavorite(favorite);
            }
            User user = doa.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getIdUser());
                doa.setUser(user);
            }
            em.persist(doa);
            if (favorite != null) {
                favorite.setDoa(doa);
                favorite = em.merge(favorite);
            }
            if (user != null) {
                Doa oldDoaOfUser = user.getDoa();
                if (oldDoaOfUser != null) {
                    oldDoaOfUser.setUser(null);
                    oldDoaOfUser = em.merge(oldDoaOfUser);
                }
                user.setDoa(doa);
                user = em.merge(user);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDoa(doa.getIdDoa()) != null) {
                throw new PreexistingEntityException("Doa " + doa + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Doa doa) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Doa persistentDoa = em.find(Doa.class, doa.getIdDoa());
            Favorite favoriteOld = persistentDoa.getFavorite();
            Favorite favoriteNew = doa.getFavorite();
            User userOld = persistentDoa.getUser();
            User userNew = doa.getUser();
            List<String> illegalOrphanMessages = null;
            if (favoriteNew != null && !favoriteNew.equals(favoriteOld)) {
                Doa oldDoaOfFavorite = favoriteNew.getDoa();
                if (oldDoaOfFavorite != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Favorite " + favoriteNew + " already has an item of type Doa whose favorite column cannot be null. Please make another selection for the favorite field.");
                }
            }
            if (userOld != null && !userOld.equals(userNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain User " + userOld + " since its doa field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (favoriteNew != null) {
                favoriteNew = em.getReference(favoriteNew.getClass(), favoriteNew.getIdDoa());
                doa.setFavorite(favoriteNew);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getIdUser());
                doa.setUser(userNew);
            }
            doa = em.merge(doa);
            if (favoriteOld != null && !favoriteOld.equals(favoriteNew)) {
                favoriteOld.setDoa(null);
                favoriteOld = em.merge(favoriteOld);
            }
            if (favoriteNew != null && !favoriteNew.equals(favoriteOld)) {
                favoriteNew.setDoa(doa);
                favoriteNew = em.merge(favoriteNew);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                Doa oldDoaOfUser = userNew.getDoa();
                if (oldDoaOfUser != null) {
                    oldDoaOfUser.setUser(null);
                    oldDoaOfUser = em.merge(oldDoaOfUser);
                }
                userNew.setDoa(doa);
                userNew = em.merge(userNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = doa.getIdDoa();
                if (findDoa(id) == null) {
                    throw new NonexistentEntityException("The doa with id " + id + " no longer exists.");
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
            Doa doa;
            try {
                doa = em.getReference(Doa.class, id);
                doa.getIdDoa();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The doa with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            User userOrphanCheck = doa.getUser();
            if (userOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Doa (" + doa + ") cannot be destroyed since the User " + userOrphanCheck + " in its user field has a non-nullable doa field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Favorite favorite = doa.getFavorite();
            if (favorite != null) {
                favorite.setDoa(null);
                favorite = em.merge(favorite);
            }
            em.remove(doa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Doa> findDoaEntities() {
        return findDoaEntities(true, -1, -1);
    }

    public List<Doa> findDoaEntities(int maxResults, int firstResult) {
        return findDoaEntities(false, maxResults, firstResult);
    }

    private List<Doa> findDoaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Doa.class));
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

    public Doa findDoa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Doa.class, id);
        } finally {
            em.close();
        }
    }

    public int getDoaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Doa> rt = cq.from(Doa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
