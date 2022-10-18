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
@Table(name = "favorite")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Favorite.findAll", query = "SELECT f FROM Favorite f"),
    @NamedQuery(name = "Favorite.findByIdDoa", query = "SELECT f FROM Favorite f WHERE f.idDoa = :idDoa"),
    @NamedQuery(name = "Favorite.findByNamaDoa", query = "SELECT f FROM Favorite f WHERE f.namaDoa = :namaDoa")})
public class Favorite implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_doa")
    private Integer idDoa;
    @Basic(optional = false)
    @Column(name = "nama_doa")
    private String namaDoa;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "favorite")
    private Doa doa;

    public Favorite() {
    }

    public Favorite(Integer idDoa) {
        this.idDoa = idDoa;
    }

    public Favorite(Integer idDoa, String namaDoa) {
        this.idDoa = idDoa;
        this.namaDoa = namaDoa;
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

    public Doa getDoa() {
        return doa;
    }

    public void setDoa(Doa doa) {
        this.doa = doa;
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
        if (!(object instanceof Favorite)) {
            return false;
        }
        Favorite other = (Favorite) object;
        if ((this.idDoa == null && other.idDoa != null) || (this.idDoa != null && !this.idDoa.equals(other.idDoa))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "arhabzuhan.doaharian.Favorite[ idDoa=" + idDoa + " ]";
    }
    
}
