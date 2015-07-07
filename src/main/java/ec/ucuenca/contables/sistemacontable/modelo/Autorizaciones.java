/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ucuenca.contables.sistemacontable.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Farfan
 */
@Entity
@Table(name = "autorizaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Autorizaciones.findAll", query = "SELECT a FROM Autorizaciones a"),
    @NamedQuery(name = "Autorizaciones.findByIdautorizaciones", query = "SELECT a FROM Autorizaciones a WHERE a.idautorizaciones = :idautorizaciones"),
    @NamedQuery(name = "Autorizaciones.findByEstablecimeinto", query = "SELECT a FROM Autorizaciones a WHERE a.establecimeinto = :establecimeinto"),
    @NamedQuery(name = "Autorizaciones.findByPuntoEmision", query = "SELECT a FROM Autorizaciones a WHERE a.puntoEmision = :puntoEmision"),
    @NamedQuery(name = "Autorizaciones.findByNumeroInicialDoc", query = "SELECT a FROM Autorizaciones a WHERE a.numeroInicialDoc = :numeroInicialDoc"),
    @NamedQuery(name = "Autorizaciones.findByNumeroFinalDoc", query = "SELECT a FROM Autorizaciones a WHERE a.numeroFinalDoc = :numeroFinalDoc"),
    @NamedQuery(name = "Autorizaciones.findByNumeroActual", query = "SELECT a FROM Autorizaciones a WHERE a.numeroActual = :numeroActual"),
    @NamedQuery(name = "Autorizaciones.findByFechaCaducudad", query = "SELECT a FROM Autorizaciones a WHERE a.fechaCaducudad = :fechaCaducudad")})
public class Autorizaciones implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idautorizaciones")
    private Integer idautorizaciones;
    @Size(max = 45)
    @Column(name = "establecimeinto")
    private String establecimeinto;
    @Size(max = 45)
    @Column(name = "puntoEmision")
    private String puntoEmision;
    @Column(name = "numeroInicialDoc")
    private Integer numeroInicialDoc;
    @Column(name = "numeroFinalDoc")
    private Integer numeroFinalDoc;
    @Column(name = "numeroActual")
    private Integer numeroActual;
    @Column(name = "fechaCaducudad")
    @Temporal(TemporalType.DATE)
    private Date fechaCaducudad;
    @JoinColumn(name = "idTipoDocumento", referencedColumnName = "iddocumento")
    @ManyToOne(fetch = FetchType.LAZY)
    private Documento idTipoDocumento;

    public Autorizaciones() {
    }

    public Autorizaciones(Integer idautorizaciones) {
        this.idautorizaciones = idautorizaciones;
    }

    public Integer getIdautorizaciones() {
        return idautorizaciones;
    }

    public void setIdautorizaciones(Integer idautorizaciones) {
        this.idautorizaciones = idautorizaciones;
    }

    public String getEstablecimeinto() {
        return establecimeinto;
    }

    public void setEstablecimeinto(String establecimeinto) {
        this.establecimeinto = establecimeinto;
    }

    public String getPuntoEmision() {
        return puntoEmision;
    }

    public void setPuntoEmision(String puntoEmision) {
        this.puntoEmision = puntoEmision;
    }

    public Integer getNumeroInicialDoc() {
        return numeroInicialDoc;
    }

    public void setNumeroInicialDoc(Integer numeroInicialDoc) {
        this.numeroInicialDoc = numeroInicialDoc;
    }

    public Integer getNumeroFinalDoc() {
        return numeroFinalDoc;
    }

    public void setNumeroFinalDoc(Integer numeroFinalDoc) {
        this.numeroFinalDoc = numeroFinalDoc;
    }

    public Integer getNumeroActual() {
        return numeroActual;
    }

    public void setNumeroActual(Integer numeroActual) {
        this.numeroActual = numeroActual;
    }

    public Date getFechaCaducudad() {
        return fechaCaducudad;
    }

    public void setFechaCaducudad(Date fechaCaducudad) {
        this.fechaCaducudad = fechaCaducudad;
    }

    public Documento getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(Documento idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idautorizaciones != null ? idautorizaciones.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Autorizaciones)) {
            return false;
        }
        Autorizaciones other = (Autorizaciones) object;
        if ((this.idautorizaciones == null && other.idautorizaciones != null) || (this.idautorizaciones != null && !this.idautorizaciones.equals(other.idautorizaciones))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.ucuenca.contables.sistemacontable.modelo.Autorizaciones[ idautorizaciones=" + idautorizaciones + " ]";
    }
    
}
