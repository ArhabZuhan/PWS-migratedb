/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arhabzuhan.doaharian;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Zuhan
 */
@Entity
@Table(name = "doa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Doa.findAll", query = "SELECT d FROM Doa d"),
    @NamedQuery(name = "Doa.findByIdDoa", query = "SELECT d FROM Doa d WHERE d.idDoa = :idDoa"),
    @NamedQuery(name = "Doa.findByNamaDoa", query = "SELECT d FROM Doa d WHERE d.namaDoa = :namaDoa"),
    @NamedQuery(name = "Doa.findByBahasaArab", query = "SELECT d FROM Doa d WHERE d.bahasaArab = :bahasaArab"),
    @NamedQuery(name = "Doa.findByLatin", query = "SELECT d FROM Doa d WHERE d.latin = :latin"),
    @NamedQuery(name = "Doa.findByTerjemahan", query = "SELECT d FROM Doa d WHERE d.terjemahan = :terjemahan")})
public class Doa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_doa")
    private Integer idDoa;
    @Basic(optional = false)
    @Column(name = "nama_doa")
    private String namaDoa;
    @Basic(optional = false)
    @Column(name = "bahasa_arab")
    private String bahasaArab;
    @Basic(optional = false)
    @Column(name = "latin")
    private String latin;
    @Basic(optional = false)
    @Column(name = "terjemahan")
    private String terjemahan;
    @JoinColumn(name = "id_doa", referencedColumnName = "id_doa", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Favorite favorite;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "doa")
    private User user;

    public Doa() {
    }

    public Doa(Integer idDoa) {
        this.idDoa = idDoa;
    }

    public Doa(Integer idDoa, String namaDoa, String bahasaArab, String latin, String terjemahan) {
        this.idDoa = idDoa;
        this.namaDoa = namaDoa;
        this.bahasaArab = bahasaArab;
        this.latin = latin;
        this.terjemahan = terjemahan;
    }

    public Integer getIdDoa() {
        return idDoa;
    }

    public void setIdDoa(Integer idDoa) {
        this.idDoa = idDoa;
    }

    public String getNamaDoa() {
        return namaDoa;
    }

    public void setNamaDoa(String namaDoa) {
        this.namaDoa = namaDoa;
    }

    public String getBahasaArab() {
        return bahasaArab;
    }

    public void setBahasaArab(String bahasaArab) {
        this.bahasaArab = bahasaArab;
    }

    public String getLatin() {
        return latin;
    }

    public void setLatin(String latin) {
        this.latin = latin;
    }

    public String getTerjemahan() {
        return terjemahan;
    }

    public void setTerjemahan(String terjemahan) {
        this.terjemahan = terjemahan;
    }

    public Favorite getFavorite() {
        return favorite;
    }

    public void setFavorite(Favorite favorite) {
        this.favorite = favorite;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDoa != null ? idDoa.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Doa)) {
            return false;
        }
        Doa other = (Doa) object;
        if ((this.idDoa == null && other.idDoa != null) || (this.idDoa != null && !this.idDoa.equals(other.idDoa))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "arhabzuhan.doaharian.Doa[ idDoa=" + idDoa + " ]";
    }
    
}
