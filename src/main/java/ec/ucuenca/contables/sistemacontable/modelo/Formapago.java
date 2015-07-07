/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ucuenca.contables.sistemacontable.modelo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Farfan
 */
@Entity
@Table(name = "formapago")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Formapago.findAll", query = "SELECT f FROM Formapago f"),
    @NamedQuery(name = "Formapago.findByIdformaPago", query = "SELECT f FROM Formapago f WHERE f.idformaPago = :idformaPago"),
    @NamedQuery(name = "Formapago.findByDescripcion", query = "SELECT f FROM Formapago f WHERE f.descripcion = :descripcion")})
public class Formapago implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idformaPago")
    private Integer idformaPago;
    @Size(max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    @OneToMany(mappedBy = "idFormaPago", fetch = FetchType.LAZY)
    private List<Cabecerafacturav> cabecerafacturavList;
    @OneToMany(mappedBy = "idFormaPago", fetch = FetchType.LAZY)
    private List<Cabecerafacturac> cabecerafacturacList;

    public Formapago() {
    }

    public Formapago(Integer idformaPago) {
        this.idformaPago = idformaPago;
    }

    public Integer getIdformaPago() {
        return idformaPago;
    }

    public void setIdformaPago(Integer idformaPago) {
        this.idformaPago = idformaPago;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    public List<Cabecerafacturav> getCabecerafacturavList() {
        return cabecerafacturavList;
    }

    public void setCabecerafacturavList(List<Cabecerafacturav> cabecerafacturavList) {
        this.cabecerafacturavList = cabecerafacturavList;
    }

    @XmlTransient
    public List<Cabecerafacturac> getCabecerafacturacList() {
        return cabecerafacturacList;
    }

    public void setCabecerafacturacList(List<Cabecerafacturac> cabecerafacturacList) {
        this.cabecerafacturacList = cabecerafacturacList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idformaPago != null ? idformaPago.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Formapago)) {
            return false;
        }
        Formapago other = (Formapago) object;
        if ((this.idformaPago == null && other.idformaPago != null) || (this.idformaPago != null && !this.idformaPago.equals(other.idformaPago))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.ucuenca.contables.sistemacontable.modelo.Formapago[ idformaPago=" + idformaPago + " ]";
    }
    
}
